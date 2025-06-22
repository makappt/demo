package com.guangyin.userservice.config;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * AI 生成
 *
 * 封装数据源，防止seata失效
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSourceProxy dataSourceProxy(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }
}