package com.baisha.casinoweb.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    // 線程池
    private ExecutorService pool;
    // 線程實例
    private static volatile ThreadPool instance;

    private ThreadPool() {
        pool = Executors.newFixedThreadPool(80);
    }

    public static ThreadPool getInstance() {
        if (instance == null) {
            synchronized (ThreadPool.class) {
                if (instance == null) {
                    instance = new ThreadPool();
                }
            }
        }
        return instance;
    }

    // 放入線程池的服務對象
    public void putThread(Runnable runnable) {
        pool.execute(runnable);
    }

}
