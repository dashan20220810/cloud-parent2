package com.baisha.backendserver.controller;


import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.LoginLog;
import com.baisha.backendserver.model.bo.admin.LoginBO;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.vo.admin.ResetPasswordVO;
import com.baisha.backendserver.model.vo.login.GoogleLoginVO;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.service.LoginLogService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.model.vo.login.LoginVO;
import com.baisha.moduleauthenticator.GoogleAuthUtil;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.IpUtil;
import com.baisha.modulejjwt.JjwtUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.baisha.backendserver.util.BackendServerUtil.getRequest;


/**
 * @author yihui
 */
@Slf4j
@RestController
@Api(tags = "登陆")
public class LoginController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private AdminService adminService;

    @Autowired
    private CommonBusiness commonService;

    @Value("${admin.account}")
    private String superAdmin;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.authKey}")
    private String adminAuthKey;

    @ApiOperation(("管理员登陆"))
    @PostMapping("login")
    @NoAuthentication
    public ResponseEntity<LoginBO> addAdmin(LoginVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if (Admin.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        if (Admin.checkPassword(vo.getPassword())) {
            return new ResponseEntity("密码不规范");
        }
        Admin user = new Admin();
        if (superAdmin.equals(vo.getUserName()) && adminPassword.equals(vo.getPassword())) {
                user.setUserName(superAdmin);
                user.setNickName(superAdmin);
                user.setPassword(adminPassword);
                user.setGoogleAuthKey(adminAuthKey);
                user.setId(Long.valueOf(0));
                user.setRoleId(Long.valueOf(0));
                redisUtil.set("backend::admin::" + user.getId(), user);
        } else {
            // 查询用户名是否存在
            user = adminService.findByUserNameSql(vo.getUserName());
            if (Objects.isNull(user)) {
                return new ResponseEntity("帐号或密码错误");
            }
            //已经删除或者禁用
            if (!Constants.open.equals(user.getStatus())) {
                return new ResponseEntity("该帐号不可操作");
            }
            //验证密码
            String bcryptPassword = user.getPassword();
            boolean bcrypt = BackendServerUtil.checkBcrypt(vo.getPassword(), bcryptPassword);
            if (!bcrypt) {
                return new ResponseEntity("帐号或密码错误");
            }

            String allowIps = user.getAllowIps();
            HttpServletRequest request = getRequest();
            String ip = IpUtil.getIp(request);

            if (!allowIpCheck(ip, allowIps)) {
                return new ResponseEntity("用戶請求不在白名單");
            }
        }
        int isFirstTime = 0;
        if (Admin.isFirstTime(user.getGoogleAuthKey())){
            isFirstTime = 1;
        }
        return ResponseUtil.success(LoginBO.builder().id(user.getId()).userName(user.getUserName()).nickName(user.getNickName()).isFirstTime(isFirstTime).build());
    }

    private void doLoginLog(Admin user) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserName(user.getUserName());
        loginLog.setNickName(user.getNickName());
        loginLog.setContent(user.getUserName() + "登陆成功");
        loginLogService.save(loginLog);
    }

    private void setUserTokenToRedis(Long userId, String token) {
        JjwtUtil.Token jwtToken = new JjwtUtil.Token();
        jwtToken.setOldToken(token);
        redisUtil.set(Constants.REDIS_TOKEN_ADMIN + userId, jwtToken, JjwtUtil.ttl + Constants.ADMIN_REFRESH_TTL);
    }

    @ApiOperation(("管理直接登陆(测试)"))
    @PostMapping("login/test")
    @NoAuthentication
    public ResponseEntity<LoginBO> addAdminTest() {
        LoginVO vo = LoginVO.builder().userName("buyaodong").password("abcd1234").build();
        return addAdmin(vo);
    }


    @ApiOperation(("登陆退出"))
    @PostMapping("quit")
    public ResponseEntity quit() {
        Long authId = BackendServerUtil.getCurrentUserId();
        if (null != authId) {
            String key = Constants.REDIS_TOKEN_ADMIN + authId;
            if (redisUtil.hasKey(key)) {
                redisUtil.del(key);
            }
        }
        return ResponseUtil.success();
    }

    private boolean allowIpCheck(String ip, String allowIps) {
        if (ObjectUtils.isEmpty(allowIps)) {
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

    @ApiOperation(("生成google key"))
    @GetMapping("genGoogleAuthKey")
    public ResponseEntity genGoogleAuthKey() {
        return ResponseUtil.success(GoogleAuthUtil.generateSecretKey());
    }

    @ApiOperation(("重置密碼(登陆用户)"))
    @PostMapping("resetPassword")
    public ResponseEntity updatePassword(ResetPasswordVO vo) throws InterruptedException {
        if (null == vo.getId()) {
            return new ResponseEntity("ID为空");
        }
        if (Admin.checkPassword(vo.getNewPassword())) {
            return new ResponseEntity("新密码不规范");
        }
        //获取当前登陆用户
        Admin admin = adminService.findAdminById(vo.getId());
        if (Objects.isNull(admin)) {
            return new ResponseEntity("管理员不存在");
        }
        if (! GoogleAuthUtil.check_code(vo.getGoogleAuthKey(), vo.getGoogleAuthCode())){
            return new ResponseEntity("google 驗證不通過");
        }
        //更新新密码
        adminService.updateAuthKeyAndPasswordById(vo.getGoogleAuthKey(),vo.getNewPassword(), vo.getId());
        log.info("{} 重置管理员密码與google驗證, 管理員id={}", admin.getUserName(), vo.getId());
        JjwtUtil.Subject subject = new JjwtUtil.Subject();
        subject.setUserId(String.valueOf(admin.getId()));
        subject.setBcryptPassword(admin.getPassword());
        String token = JjwtUtil.generic(subject, Constants.CASINO_ADMIN);
        setUserTokenToRedis(admin.getId(), token);
        doLoginLog(admin);
        return ResponseUtil.success(LoginBO.builder().id(admin.getId()).userName(admin.getUserName()).nickName(admin.getNickName()).token(token).build());
    }

    @ApiOperation(("google 認證登入"))
    @PostMapping("googleAuthLogin")
    public ResponseEntity googleAuthLogin(GoogleLoginVO vo) {

        if (null == vo.getId() || null == vo.getGoogleAuthCode()) {
            return ResponseUtil.parameterNotNull();
        }
        Admin admin = new Admin();
        if (0L == vo.getId()){
            if (! GoogleAuthUtil.check_code(adminAuthKey, vo.getGoogleAuthCode())){
                return new ResponseEntity("google 驗證不通過");
            }
            admin.setUserName(superAdmin);
            admin.setNickName(superAdmin);
            admin.setPassword(adminPassword);
            admin.setGoogleAuthKey(adminAuthKey);
            admin.setId(Long.valueOf(0));
            admin.setRoleId(Long.valueOf(0));
            redisUtil.set("backend::admin::" + admin.getId(), admin);
        }
        else {
            admin = adminService.findAdminById(vo.getId());
            if (! GoogleAuthUtil.check_code(admin.getGoogleAuthKey(), vo.getGoogleAuthCode())){
                return new ResponseEntity("google 驗證不通過");
            }

        }
        JjwtUtil.Subject subject = new JjwtUtil.Subject();
        subject.setUserId(String.valueOf(admin.getId()));
        subject.setBcryptPassword(admin.getPassword());
        String token = JjwtUtil.generic(subject, Constants.CASINO_ADMIN);
        setUserTokenToRedis(admin.getId(), token);
        doLoginLog(admin);
        return ResponseUtil.success(LoginBO.builder().id(admin.getId()).userName(admin.getUserName()).nickName(admin.getNickName()).token(token).build());

    }
}