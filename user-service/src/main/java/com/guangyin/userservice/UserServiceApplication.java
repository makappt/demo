package com.guangyin.userservice;

import com.guangyin.core.constants.MicroServiceConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = MicroServiceConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = MicroServiceConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement
// 支持mybatis mapper扫描
@MapperScan(basePackages = MicroServiceConstants.BASE_COMPONENT_SCAN_PATH + ".userservice.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
