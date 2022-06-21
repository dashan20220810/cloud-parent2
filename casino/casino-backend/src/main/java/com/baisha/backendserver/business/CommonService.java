package com.baisha.backendserver.business;

import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommonService {

    @Autowired
    private AdminService adminService;

    public Admin getCurrentUser() {
        Long authId = BackendServerUtil.getCurrentUserId();
        return adminService.findAdminById(authId);
    }

}
