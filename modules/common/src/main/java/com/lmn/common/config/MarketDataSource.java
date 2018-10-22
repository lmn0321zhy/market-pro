package com.lmn.common.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.lmn.common.utils.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by lmn on 2018-10-11.
 */
@Component
@Configuration
public class MarketDataSource {
    @Autowired
    private MarketDataSourceConfig marketDataSourceConfig;

    @Bean(name="dataSource")
    public DataSource primaryDataSource (){
        DruidDataSource datasource = new DruidDataSource();
        /* 基础配置 */
        datasource.setName("jw_group");
        datasource.setUrl(this.marketDataSourceConfig.getUrl());
        datasource.setUsername(this.marketDataSourceConfig.getUsername());
        datasource.setPassword(this.marketDataSourceConfig.getPassword());
        datasource.setDriverClassName(this.marketDataSourceConfig.getDriverClassName());

        /* 其他配置 */
        datasource.setInitialSize(this.marketDataSourceConfig.getInitialSize());
        datasource.setMinIdle(this.marketDataSourceConfig.getMinIdle());
        datasource.setMaxActive(this.marketDataSourceConfig.getMaxActive());
        datasource.setMaxWait(this.marketDataSourceConfig.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(this.marketDataSourceConfig.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(this.marketDataSourceConfig.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(this.marketDataSourceConfig.getValidationQuery());
        datasource.setTestWhileIdle(this.marketDataSourceConfig.getTestWhileIdle());
        datasource.setTestOnBorrow(this.marketDataSourceConfig.getTestOnBorrow());
        datasource.setTestOnReturn(this.marketDataSourceConfig.getTestOnReturn());
        return datasource;
    }
}
