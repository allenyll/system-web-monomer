package com.allenyll.sw.common.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.allenyll.sw.common.entity.BaseEntity;
import lombok.Data;


/**
 * 调度任务表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-03-17 12:07:34
 */
@Data
@TableName("sys_job")
public class Job extends BaseEntity<Job> {

	private static final long serialVersionUID = 1L;

	// 任务主键
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

	// 名称
    private String jobName;

	// 调用方法名
    private String methodName;

	// 表达式
    private String corn;

	// 任务状态
    private String status;

	// 调用类名
    private String className;

	// 任务分组
    private String jobGroup;

	// 描述
    private String description;

	// springBean
    private String springBean;

}
