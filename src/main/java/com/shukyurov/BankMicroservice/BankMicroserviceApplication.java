package com.shukyurov.BankMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class})
@EnableConfigurationProperties
public class BankMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankMicroserviceApplication.class, args);
	}
}
