package com.allenyll.sw.system.aspect;

import com.allenyll.sw.common.annotation.Log;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.base.ILogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Description:  日志切面
 * @Author:       allenyll
 * @Date:         2020-04-26 13:05
 * @Version:      1.0
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);
    
    @Autowired
    ILogService logService;

    @Pointcut("@annotation(com.allenyll.sw.common.annotation.Log)")
    public void logPoint(){}

    @Around("logPoint()")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LOGGER.info("执行切入点方法: {}", method.getName());
        long beginTime = System.currentTimeMillis();

        Object obj = null;

        try {
             obj = joinPoint.proceed();
        } catch (Throwable throwable) {
            LOGGER.error("执行切入方法异常");
            throwable.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        saveLog(joinPoint, endTime-beginTime);

        return obj;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log log = method.getAnnotation(Log.class);

        com.allenyll.sw.common.entity.system.Log sysLog = new com.allenyll.sw.common.entity.system.Log();

        if(log != null){
            sysLog.setOperation(log.value());
        }

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setClassName(className + "." + methodName + "()");

        Object[] args = joinPoint.getArgs();
        Long userId = null;
        String account = "";
        if(args.length > 0){
            StringBuilder params= new StringBuilder();
            for (Object arg : args) {
                if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile) {
                    //ServletRequest不能序列化，从入参里排除，
                    // 否则报异常：java.lang.IllegalStateException: It is illegal to call
                    // this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                    //ServletResponse不能序列化 从入参里排除，
                    // 否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                    continue;
                }
                if (arg instanceof User) {
                    User user = (User) arg;
                    userId = user.getId();
                    account = user.getAccount();
                } else {
                    params.append("[").append(JsonUtil.beanToJson(arg)).append("], ");
                }
            }
            if(StringUtil.isNotEmpty(params.toString())){
                params = new StringBuilder(params.substring(0, params.length() - 2));
            }
            sysLog.setParams(params.toString());
        }

        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        HttpServletResponse response = HttpContextUtils.getHttpServletResponse();
        // 设置IP地址
        sysLog.setIp(IPUtil.getIpAddr(request, response));
        sysLog.setUserId(userId);
        sysLog.setAccount(account);
        sysLog.setOperateTime(time);
        assert log != null;
        sysLog.setLogType(log.type());
        sysLog.setIsDelete(0);
        sysLog.setAddTime(DateUtil.getCurrentDateTime());
        sysLog.setAddUser(userId);
        sysLog.setUpdateTime(DateUtil.getCurrentDateTime());
        sysLog.setUpdateUser(userId);

        logService.save(sysLog);
    }

}
