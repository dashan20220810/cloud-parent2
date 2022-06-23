package com.baisha.backendserver.business;

import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.constants.FileServerConstants;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.util.HttpClient4Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommonService {

    @Value("${url.fileServer}")
    private String fileServerUrl;

    @Autowired
    private AdminService adminService;

    public Admin getCurrentUser() {
        Long authId = BackendServerUtil.getCurrentUserId();
        return adminService.findAdminById(authId);
    }

    /**
     * 获取完整路径
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
            log.error("请求文件服务失败");
        }
        return null;
    }

}
