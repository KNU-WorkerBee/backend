package com.example.workbee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class WorkbeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkbeeApplication.class, args);
    }

}
