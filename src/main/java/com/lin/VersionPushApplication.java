package com.lin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * start up
 * @author lin
 * @date   2021/5/10
**/
@SpringBootApplication
@EnableScheduling
public class VersionPushApplication {

    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        SpringApplication.run(VersionPushApplication.class, args);
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        pool.setCorePoolSize(1);
        // 线程池维护线程的最大数量
        pool.setMaxPoolSize(10);
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }
}
