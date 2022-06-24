package com.baisha.backendserver.service;

import com.baisha.backendserver.model.OperateLog;
import com.baisha.backendserver.repository.OperateLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperateLogService {

    @Autowired
    private OperateLogRepository operateLogRepository;


    public OperateLog save(OperateLog operateLog) {
        operateLogRepository.save(operateLog);
        return operateLog;
    }


}
