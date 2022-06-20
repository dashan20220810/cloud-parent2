package com.baisha.modulecommon.executor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface AsyncService{

    /**
     * 线程任务实现此方法
     */
    void executeAsync();
}
