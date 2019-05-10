package com.us.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static org.springframework.boot.SpringApplication.run;

/**
 * Created by liuyuhao on 17/1/17.
 */
@EnableSwagger2
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = run(Application.class, args);
    }

}
