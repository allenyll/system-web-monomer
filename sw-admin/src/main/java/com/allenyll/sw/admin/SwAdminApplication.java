package com.allenyll.sw.admin;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan("com.allenyll.sw")
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SwAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwAdminApplication.class, args);
    }

}
