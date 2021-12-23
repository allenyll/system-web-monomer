package com.allenyll.sw.job.service.impl;

import com.allenyll.sw.common.entity.system.JobLog;
import com.allenyll.sw.common.util.DateUtil;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import com.allenyll.sw.job.mapper.JobLogMapper;
import com.allenyll.sw.job.service.IJobLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 调度任务日志记录
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-09 17:34:28
 */
@Service("jobLogService")
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements IJobLogService {

    @Autowired
    JobLogMapper jobLogMapper;

    @Override
    public void saveJobLog(String status, String errMsg, long time, JobDataMap jobDataMap) {
        Long id = jobDataMap.getLong("id");
        String name = jobDataMap.getString("jobName");
        String group = jobDataMap.getString("jobGroup");
        JobLog jobLog = new JobLog();
        jobLog.setId(SnowflakeIdWorker.generateId());
        jobLog.setJobId(id);
        jobLog.setJobName(name);
        jobLog.setJobGroup(group);
        jobLog.setStatus(status);
        jobLog.setMessage(errMsg);
        jobLog.setTime(time);
        jobLog.setIsDelete(0);
        jobLog.setAddTime(DateUtil.getCurrentDateTime());
        jobLogMapper.insert(jobLog);
    }
}
