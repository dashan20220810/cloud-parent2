package com.baisha.backendserver.service;

import com.baisha.backendserver.model.LoginLog;
import com.baisha.backendserver.repository.LoginLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yihui
 */
@Slf4j
@Service
public class LoginLogService {

    @Autowired
    private LoginLogRepository loginLogRepository;


    public LoginLog save(LoginLog loginLog) {
        loginLogRepository.save(loginLog);
        return loginLog;
    }


}
