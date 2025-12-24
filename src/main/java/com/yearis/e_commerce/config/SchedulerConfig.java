package com.yearis.e_commerce.config;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

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
