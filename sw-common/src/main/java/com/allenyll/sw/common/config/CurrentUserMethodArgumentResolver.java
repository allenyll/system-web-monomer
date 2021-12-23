package com.allenyll.sw.common.config;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.constants.SecurityConstants;
import com.allenyll.sw.common.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:  CurrentUserMethodArgumentResolver
 * @Author:       allenyll
 * @Date:         2020/6/1 8:14 下午
 * @Version:      1.0
 */
@Slf4j
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        CurrentUser currentUser = parameter.getParameterAnnotation(CurrentUser.class);
        boolean isFull = currentUser.isFull();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        
        User user;
        if (isFull) {
            user = (User) request.getAttribute(SecurityConstants.CURRENT_USER);
        } else {
            user = new User();
            user.setUserName("");
        }
        return user;
    }
}
