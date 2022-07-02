//package com.baisha.casinoweb.config;
//
//import java.util.concurrent.Executor;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//@Configuration
//@EnableAsync
//public class SpringAsyncConfig {
//
//	@Bean
//	public Executor threadPoolTaskExecutor() {
//	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//	    executor.setCorePoolSize(2);
//	    executor.setMaxPoolSize(5);
//	    executor.setQueueCapacity(500);
//	    executor.setThreadNamePrefix("BaishaAsyncLookup-");
//	    executor.setKeepAliveSeconds(180);
//	    executor.initialize();
//	    return executor;
//	}
//}
