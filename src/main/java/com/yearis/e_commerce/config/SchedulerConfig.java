package com.yearis.e_commerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

        taskScheduler.setPoolSize(5);

        taskScheduler.setThreadNamePrefix("App-Worker-Scheduler-");

        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
