package com.allenyll.sw.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author yuleilei
 */
@EnableSwagger2
@ComponentScan("com.allenyll.sw")
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SwAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwAdminApplication.class, args);
    }

}
