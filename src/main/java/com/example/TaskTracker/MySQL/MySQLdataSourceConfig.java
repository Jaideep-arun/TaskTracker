package com.example.TaskTracker.MySQL;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.TaskTracker.Pass.MyPass;

@Configuration
public class MySQLdataSourceConfig {

    @Bean
    public static DataSource source() {
        return DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url("jdbc:mysql://localhost:3306/trackerDB")
            .username(new MyPass().getUser())
            .password(new MyPass().getPass())
            .build();
    }
}
