package com.yhy.takeout;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@MapperScan("com.yhy.takeout.mapper")
@ServletComponentScan // 扫描WebFilter注解
@EnableTransactionManagement //开启事务
public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
        log.info("启动成功....");
    }
}
