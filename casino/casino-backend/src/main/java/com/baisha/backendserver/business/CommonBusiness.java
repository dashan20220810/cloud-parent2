package com.baisha.backendserver.business;

import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.OperateLog;
import com.baisha.backendserver.model.vo.log.OperateLogVO;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.service.OperateLogService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.FileServerConstants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.util.HttpClient4Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class CommonBusiness {

    @Value("${url.fileServer}")
    private String fileServerUrl;

    @Value("${admin.account}")
    private String superAdmin;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.authKey}")
    private String adminAuthKey;

    @Autowired
    private AdminService adminService;
    @Autowired
    private OperateLogService operateLogService;

    public Admin getCurrentUser() {
        Long authId = BackendServerUtil.getCurrentUserId();
        try {
            Admin admin = new Admin();
            if (authId == 0L){
                admin.setId(0L);
                admin.setUserName(superAdmin);
                admin.setNickName(superAdmin);
                admin.setPassword(adminPassword);
                admin.setGoogleAuthKey(adminAuthKey);
                admin.setRoleId(Long.valueOf(0));
            } else {
                admin = adminService.findAdminById(authId);
            }
            if (Objects.nonNull(admin)) {
                return admin;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("???????????????????????????id={}", authId);
            //????????????
            throw new RuntimeException("???????????????????????????");
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param fileKey
     * @return
     */
    public String getFileServerUrl(String fileKey) {
        String url = fileServerUrl + FileServerConstants.GETFILEURL + "?fileKey=" + fileKey;
        try {
            String result = HttpClient4Util.doGet(url);
            if (StringUtils.isNotEmpty(result)) {
                ResponseEntity fileRes = JSONObject.parseObject(result, ResponseEntity.class);
                return (String) fileRes.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("????????????????????????");
        }
        return null;
    }


    public void saveOperateLog(Admin admin, OperateLogVO vo) {
        try {
            OperateLog operateLog = new OperateLog();
            BeanUtils.copyProperties(vo, operateLog);
            operateLog.setCreateBy(admin.getUserName());
            operateLog.setUpdateBy(admin.getUserName());
            operateLog.setUserName(admin.getUserName());
            operateLog.setNickName(admin.getNickName());
            operateLogService.save(operateLog);
        } catch (Exception e) {
            log.error("????????????????????????");
            e.printStackTrace();
        }
    }

}
