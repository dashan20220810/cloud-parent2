package com.baisha.backendserver.inteceptor;

import com.baisha.backendserver.model.BlockIp;
import com.baisha.backendserver.service.BlockIpService;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.inteceptor.AbstractIpBlackCheckInterceptor;
import com.baisha.modulecommon.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class IpBlackCheckInterceptor extends AbstractIpBlackCheckInterceptor {

    @Autowired
    private BlockIpService blockIpService;

    @Override
    protected String ipBlackCheck(HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        BlockIp blockIp = blockIpService.findByIp(ip);
        if (blockIp != null && !Constants.yes.equals(blockIp.getStatus())) {
            return blockIp.getReason();
        }
        return null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
