package com.baisha.modulecommon.inteceptor;

import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.annotation.NoAuthorization;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InteceptorController implements ErrorController {

    @NoAuthentication
    @RequestMapping("error")
    public ResponseEntity error() {
        return ResponseUtil.error();
    }

    @NoAuthentication
    @RequestMapping("authenticationNopass")
    public ResponseEntity authenticationNopass() {
        return ResponseUtil.authenticationNopass();
    }

    @RequestMapping("authenticationBan")
    public ResponseEntity authenticationBan() {
        return ResponseUtil.custom("帐号被封");
    }

    @NoAuthentication
    @RequestMapping("authenticationIpLimit")
    public ResponseEntity authenticationIpLimit() {
        return ResponseUtil.custom(Constants.IP_BLOCK);
    }

    @NoAuthorization
    @RequestMapping("authorizationNopass")
    public ResponseEntity authorizationNopass() {
        return ResponseUtil.authorizationNopass();
    }


    @NoAuthentication
    @RequestMapping("risk")
    public ResponseEntity risk() {
        return ResponseUtil.risk();
    }


    @NoAuthentication
    @RequestMapping("authenticationMultiDevice")
    public ResponseEntity authenticationMultiDevice() {
        return ResponseUtil.multiDevice();
    }

    @NoAuthentication
    @RequestMapping("authenticationPlatformMaintain")
    public ResponseEntity authenticationPlatformMaintain(String startTime, String endTime) {
        ResponseEntity response = ResponseUtil.platformMaintain();
        //因为不能依赖core模块所以参数改为从前面传递而不是直接查数据库
        AbstractAuthenticationInteceptor.PlatformMaintenanceSwitch data = new AbstractAuthenticationInteceptor.PlatformMaintenanceSwitch();
        data.setOnOff(true);
        data.setStartTime(startTime);
        data.setEndTime(endTime);
        response.setData(data);
        return response;
    }
}
