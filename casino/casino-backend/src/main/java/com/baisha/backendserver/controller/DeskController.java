package com.baisha.backendserver.controller;

import com.baisha.backendserver.business.CommonService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping(value = "desk")
@Api(tags = "限红")
public class DeskController {

    @Autowired
    private CommonService commonService;


}
