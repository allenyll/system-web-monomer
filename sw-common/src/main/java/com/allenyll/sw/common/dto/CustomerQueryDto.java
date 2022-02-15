package com.allenyll.sw.common.dto;

import com.allenyll.sw.common.entity.customer.Customer;
import lombok.Data;

import java.io.Serializable;


/**
 * 用户查询DTO
 * @ClassName: com.allenyll.sw.common.dto.CustomerQueryDto.java
 * @Description:
 * @author: 20012055 yuleilei
 * @date:  2021/1/5 11:30
 * @version V1.0
 */
@Data
public class CustomerQueryDto  extends BaseQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String customerName;

    /**
     * 小程序CODE
     */
    private String code;
    
    private Customer customer;
}
