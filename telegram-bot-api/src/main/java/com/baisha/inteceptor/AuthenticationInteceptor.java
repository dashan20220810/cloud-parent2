package com.baisha.inteceptor;

import com.baisha.modulecommon.inteceptor.AbstractAuthenticationInteceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AuthenticationInteceptor extends AbstractAuthenticationInteceptor {

    @Override
    protected boolean hasBan() {
        return false;
    }

    @Override
    public boolean hasPermission(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    protected boolean multiDeviceCheck() {
        return false;
    }

}
