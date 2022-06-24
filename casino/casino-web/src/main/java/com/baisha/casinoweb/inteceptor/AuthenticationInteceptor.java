package com.baisha.casinoweb.inteceptor;

import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.inteceptor.AbstractAuthenticationInteceptor;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulejjwt.JjwtUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AuthenticationInteceptor extends AbstractAuthenticationInteceptor {

    @Autowired
    RedisUtil redisUtil;

    @Override
    protected boolean hasBan() {
        return false;
    }

    @Override
    public boolean hasPermission(HttpServletRequest request, HttpServletResponse response) {
        String token = CasinoWebUtil.getToken();
        //TOKEN
        if (JjwtUtil.check(token, Constants.CASINO_WEB)) {
            return true;
        }
        //token过期获取新token
        JjwtUtil.Token refreshJwtToken = refreshJwtToken(token);
        if (refreshJwtToken == null|| StringUtils.isEmpty(refreshJwtToken.getNewToken())) {
            return false;
        }
        String newToken = refreshJwtToken.getNewToken();
        response.setHeader(Constants.AUTHORIZATION, newToken);
        return true;
    }

    /**
     * 通过旧token获取新token
     * @param token
     * @return
     */
    public JjwtUtil.Token refreshJwtToken(String token) {
        if(CommonUtil.checkNull(token)){
            return null;
        }
        JjwtUtil.Subject subject = JjwtUtil.getSubject(token);
        if (subject == null || ObjectUtils.isEmpty(subject.getUserId())) {
            return null;
        }
        //获取登陆用户
        Long authId = Long.parseLong(subject.getUserId());

        //多个请求只有一个去刷新token
        synchronized (token.intern()) {

            JjwtUtil.Token redisToken =  (JjwtUtil.Token)redisUtil.get(Constants.TOKEN_CASINO_WEB + authId);
            //判断其他请求是否已经获取到新token
            if (redisToken != null && token.equals(redisToken.getOldToken()) && !ObjectUtils.isEmpty(redisToken.getNewToken())) {
                return redisToken;
            }
            //获取新token
            String refreshToken = JjwtUtil.refreshToken(token, "",  Constants.CASINO_WEB);
            if (ObjectUtils.isEmpty(refreshToken)) {
                return null;
            }
            //获取到新token后会把之前的token设置成旧的，用于判断后面其他带旧token的请求，有一个旧token获取到新token，其他直接从redis取新的
            JjwtUtil.Token jwtToken = new JjwtUtil.Token();
            jwtToken.setOldToken(token);
            jwtToken.setNewToken(refreshToken);
            redisUtil.set(Constants.TOKEN_CASINO_WEB + authId, jwtToken, Constants.WEB_REFRESH_TTL);

            //不是最新的token也可以获取到新token，但是多设备校验的时候会拦截
//            if (redisJwtToken != null && (token.equals(redisJwtToken.getOldToken()) || token.equals(redisJwtToken.getNewToken()))) {
//                redisUtil.set(Constants.TOKEN_CASINO_WEB + authId, jwtToken, Constants.WEB_REFRESH_TTL);
//            } else {
//                log.error("当前token={}，iss={} 已失效刷新token无效，redis中token信息为={}", token, Constants.CASINO_WEB, redisJwtToken);
//            }
            return jwtToken;
        }
    }


    /**
     * 多设备登录校验，后面登录的会踢掉前面登录的
     * @return
     */
    @Override
    protected boolean multiDeviceCheck() {
//        Long authId = CasinoWebUtil.getAuthId();
//        String token = CasinoWebUtil.getToken();
//        String key = Constants.TOKEN_CASINO_WEB + authId;
//        Object redisToken = redisUtil.get(key);
//        if (ObjectUtils.isEmpty(redisToken)) {
//            return true;
//        }
//        JjwtUtil.Token redisToken1 = (JjwtUtil.Token) redisToken;
//        //新旧token有一个匹配得上说明就是最新token
//        if (token.equals(redisToken1.getOldToken())||token.equals(redisToken1.getNewToken())) {
//            return true;
//        }
        return true;
    }

    /**
     * 平台维护开关校验
     * @return
     */
    @Override
    protected PlatformMaintenanceSwitch platformMaintainCheck() {
//        PlatformMaintenanceSwitch vo = new PlatformMaintenanceSwitch();
//        try {
//            PlatformConfig platformConfig = platformConfigService.findFirst();
//            if (platformConfig == null || platformConfig.getMaintenanceStart() == null || platformConfig.getMaintenanceEnd() == null) {
//                vo.setOnOff(false);
//                return vo;
//            }
//            Integer maintenance = platformConfig.getPlatformMaintenance();
//            boolean switchb = maintenance == Constants.open ? true : false;
//            //先判断开关是否是维护状态，在判断当前时间是否在维护时区间内
//            if (switchb) {
//                switchb = DateUtil.isEffectiveDate(new Date(), platformConfig.getMaintenanceStart(), platformConfig.getMaintenanceEnd());
//            }
//            vo.setOnOff(switchb);
//            //最后确定状态
//            if (switchb) {
//                SimpleDateFormat sd = DateUtil.getSimpleDateFormat();
//                if (platformConfig.getMaintenanceStart() != null) {
//                    vo.setStartTime(sd.format(platformConfig.getMaintenanceStart()));
//                }
//                if (platformConfig.getMaintenanceEnd() != null) {
//                    vo.setEndTime(sd.format(platformConfig.getMaintenanceEnd()));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("检查KK平台维护时报错，msg={}", e.getMessage());
//        }
//        return vo;
        return null;
    }
}
