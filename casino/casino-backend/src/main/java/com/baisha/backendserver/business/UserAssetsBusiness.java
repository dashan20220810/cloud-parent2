package com.baisha.backendserver.business;

import com.baisha.backendserver.model.bo.user.UserAssetsBO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author yihui
 */
@Slf4j
@Service
public class UserAssetsBusiness {

    @Value("${url.userServer}")
    private String userServerUrl;

}
