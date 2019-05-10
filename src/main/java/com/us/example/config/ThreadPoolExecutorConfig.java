package com.us.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolExecutorConfig {
    @Bean
    public ThreadPoolExecutor getGlobalThreadPool(){
        return new ThreadPoolExecutor(10, 20, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(10));
    }
}
