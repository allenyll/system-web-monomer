package com.allenyll.sw.admin.job;

import com.allenyll.sw.common.entity.system.JobLog;
import com.allenyll.sw.job.service.impl.JobLogServiceImpl;
import com.allenyll.sw.system.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调度任务日志记录
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-09 17:34:28
 */
@Slf4j
@Api(value = "调度任务日志记录", tags = "调度任务日志记录")
@RestController
@RequestMapping("/jobLog")
public class JobLogController extends BaseController<JobLogServiceImpl, JobLog> {


}
