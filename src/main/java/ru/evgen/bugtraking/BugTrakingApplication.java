package ru.evgen.bugtraking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class BugTrakingApplication {

    public static void main(String[] args) {

        ApplicationContext applicationContext = SpringApplication.run(BugTrakingApplication.class, args);

    }


}
