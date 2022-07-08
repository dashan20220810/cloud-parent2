package com.baisha.gameserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

	
	private static final int TASK_POOL_SIZE = 10;
	
	private static final String TASK_THREAD_PREFIX = "game-server-task-";
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegisterar) {
		ThreadPoolTaskScheduler taskPool = new ThreadPoolTaskScheduler();
		taskPool.setPoolSize(TASK_POOL_SIZE);
		taskPool.setThreadNamePrefix(TASK_THREAD_PREFIX);
		taskPool.initialize();
		scheduledTaskRegisterar.setTaskScheduler(taskPool);
	}
	
}
