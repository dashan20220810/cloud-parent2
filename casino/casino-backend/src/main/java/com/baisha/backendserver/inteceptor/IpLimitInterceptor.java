package com.baisha.backendserver.inteceptor;//package com.baisha.livebaccarat.inteceptor;

import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.modulecommon.inteceptor.AbstractIpLimitInterceptor;
import com.baisha.modulecommon.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//
//import com.qianyi.casinocore.model.IpBlack;
//import com.qianyi.casinocore.service.IpBlackService;
//import com.qianyi.casinoweb.config.RedisLimitExcutor;
//import com.qianyi.casinoweb.util.CasinoWebUtil;
//import com.qianyi.modulecommon.Constants;
//import com.qianyi.modulecommon.inteceptor.AbstractIpLimitInteceptor;
//import com.qianyi.modulecommon.util.IpUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ObjectUtils;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
@Slf4j
@Component
public class IpLimitInterceptor extends AbstractIpLimitInterceptor {

    @Autowired
    private AdminService adminService;

    @Override
    protected String ipLimit(HttpServletRequest request) {
        String ip = IpUtil.getIp(request);
        String path = request.getRequestURI();
        Map<String, String[]> map =request.getParameterMap();
        String query = request.getQueryString();
        String queryString = null;

        String url2=request.getMethod();
        String url3=request.getPathInfo();
        System.out.println("請求ip為"+path);
        System.out.println("request ="+map.get("password").toString());
        System.out.println("request ="+map.get("userName").toString());
        System.out.println("url2為"+url2);
        System.out.println("url3"+url3);
        Long authId = BackendServerUtil.getCurrentUserId();
        if (authId == 0L){

            return null;
        } else {

        }
        Admin admin = adminService.findAdminById(authId);
        String allowIps = admin.getAllowIps();
        System.out.println("ip白名單為"+allowIps);
        if (! allowIpCheck(ip, allowIps)){
            return "ip不在白名單上";
        }
        return null;
    }

    private boolean allowIpCheck(String ip, String allowIps) {
        if (ObjectUtils.isEmpty(allowIps)){
            return false;
        }
        String[] ipWhiteArray = allowIps.split(",");
        for (String ipw : ipWhiteArray) {
            if (ipw.trim().equals(ip)) {
                return true;
            }
        }
        return false;
    }
}

//
//    @Autowired
//    private RedisLimitExcutor redisLimitExcutor;
//    @Autowired
//    private IpBlackService ipBlackService;
//    @Value("${project.ipWhite}")
//    private String ipWhite;
//
//    @Override
//    protected String ipLimit(HttpServletRequest request) {
//        String ip = IpUtil.getIp(request);
//        if(ipWhiteCheck(ip)){
//            return null;
//        }
//        boolean access = redisLimitExcutor.tryAccess(Constants.REDIS_IPLIMIT + ip);
//        if(!access){
//            String remark="单位时间内请求次数超过上限,IP被封";
//            IpBlack ipBlack =new IpBlack();
//            ipBlack.setIp(ip);
//            ipBlack.setStatus(Constants.no);
//            ipBlack.setRemark(remark);
//            ipBlackService.save(ipBlack);
//            return remark;
//        }
//        return null;
//    }
//
//    /**
//     * admin ip放行
//     * @param ip
//     * @return
//     */
//    private Boolean ipWhiteCheck(String ip) {
//        if (ObjectUtils.isEmpty(ipWhite)) {
//            return false;
//        }
//        String[] ipWhiteArray = ipWhite.split(",");
//        for (String ipw : ipWhiteArray) {
//            if (!ObjectUtils.isEmpty(ipw) && ipw.trim().equals(ip)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
