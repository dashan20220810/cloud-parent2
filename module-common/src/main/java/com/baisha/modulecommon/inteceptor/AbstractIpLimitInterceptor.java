package com.baisha.modulecommon.inteceptor;

import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public abstract class AbstractIpLimitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url=request.getServletPath();
        if(url.contains("authenticationIpLimit")){
            return true;
        }
        String ipRemark = ipLimit(request);
        if(!ObjectUtils.isEmpty(ipRemark)){
            log.error("ip={}被封，原因:{},请求路径:{},token:{}", IpUtil.getIp(request),ipRemark,request.getRequestURI(),request.getHeader(Constants.AUTHORIZATION));
//            ipRemark = URLEncoder.encode(ipRemark,"UTF-8");
            response.sendRedirect(request.getContextPath()+"/authenticationIpLimit");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    protected abstract String ipLimit(HttpServletRequest request);
}
