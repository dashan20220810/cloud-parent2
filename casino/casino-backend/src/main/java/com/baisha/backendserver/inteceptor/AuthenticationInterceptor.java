package com.baisha.backendserver.inteceptor;

import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.inteceptor.AbstractAuthenticationInterceptor;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulejjwt.JjwtUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yihui
 */
@Slf4j
@Component
public class AuthenticationInterceptor extends AbstractAuthenticationInterceptor {
    @Autowired
    private AdminService userService;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${admin.account}")
    private String superAdmin;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.authKey}")
    private String adminAuthKey;

    @Override
    protected boolean hasBan() {
        return false;
    }

    @Override
    public boolean hasPermission(HttpServletRequest request, HttpServletResponse response) {
        String token = BackendServerUtil.getToken();
        //token验证
        if (JjwtUtil.check(token, Constants.CASINO_ADMIN)) {
            return true;
        }
//        //解析token
//        JjwtUtil.Subject subject = JjwtUtil.getSubject(token);
//        if (Objects.isNull(subject) || ObjectUtils.isEmpty(subject.getUserId())) {
//            return false;
//        }
//        //获取登陆用户
//        Long authId = Long.parseLong(subject.getUserId());
//        log.info("authId={}", authId);

        //token过期获取新token
        JjwtUtil.Token refreshJwtToken = refreshJwtToken(token);
        if (refreshJwtToken == null) {
            return false;
        }
        String newToken = refreshJwtToken.getNewToken();
        response.setHeader(Constants.AUTHORIZATION, newToken);
        return true;
    }

    /**
     * 通过旧token获取新token
     *
     * @param token
     * @return
     */
    public JjwtUtil.Token refreshJwtToken(String token) {
        boolean checkNull = CommonUtil.checkNull(token);
        if (checkNull) {
            return null;
        }
        JjwtUtil.Subject subject = JjwtUtil.getSubject(token);
        if (subject == null || ObjectUtils.isEmpty(subject.getUserId())) {
            return null;
        }
        //获取登陆用户
        Admin user = new Admin();
        Long authId = Long.parseLong(subject.getUserId());
        if (authId == 0L){

            user.setUserName(superAdmin);
            user.setNickName(superAdmin);
            user.setPassword(adminPassword);
        } else {
            user = userService.findAdminById(authId);
        }
        synchronized (token.intern()) {
            //多个请求只有一个去刷新token
            Object redisToken = redisUtil.get(Constants.REDIS_TOKEN_ADMIN + authId);
            JjwtUtil.Token redisJwtToken = null;
            if (redisToken != null) {
                redisJwtToken = (JjwtUtil.Token) redisToken;
            }
            //判断其他请求是否已经获取到新token
            if (redisJwtToken != null && token.equals(redisJwtToken.getOldToken()) && !ObjectUtils.isEmpty(redisJwtToken.getNewToken())) {
                return redisJwtToken;
            }
            //获取新token
            String refreshToken = JjwtUtil.refreshToken(token, user.getPassword(), Constants.ADMIN_REFRESH_TTL,
                    Constants.CASINO_ADMIN);
            if (ObjectUtils.isEmpty(refreshToken)) {
                return null;
            }
            //获取到新token后会把之前的token设置成旧的，用于判断后面其他带旧token的请求，有一个旧token获取到新token，其他直接从redis取新的
            JjwtUtil.Token jwtTiken = new JjwtUtil.Token();
            jwtTiken.setOldToken(token);
            jwtTiken.setNewToken(refreshToken);
            //不是最新的token也可以获取到新token，但是多设备校验的时候会拦截
            /*if (redisJwtToken != null && (token.equals(redisJwtToken.getOldToken()) || token.equals(redisJwtToken.getNewToken()))) {
                redisUtil.set(Constants.REDIS_TOKEN_ADMIN + authId, jwtTiken, Constants.WEB_REFRESH_TTL);
            } else {
                log.error("当前token={}，iss={} 已失效刷新token无效，redis中token信息为={}", token, Constants.CASINO_ADMIN,
                        redisJwtToken);
            }*/
            redisUtil.set(Constants.REDIS_TOKEN_ADMIN + authId, jwtTiken, Constants.ADMIN_REFRESH_TTL);
            return jwtTiken;
        }
    }

    @Override
    protected boolean multiDeviceCheck() {
        return true;
    }
}
