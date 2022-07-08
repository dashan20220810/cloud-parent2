package com.baisha.backendserver.business;

import com.baisha.backendserver.model.SysPlayMoneyParameter;
import com.baisha.backendserver.model.bo.sys.SysPlayMoneyParameterBO;
import com.baisha.backendserver.service.SysPlayMoneyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@Service
public class PlayMoneyBusiness {
    
    @Autowired
    private SysPlayMoneyService sysPlayMoneyService;

    /**
     * 获取 打码量 倍率
     *
     * @return
     */
    public SysPlayMoneyParameterBO getSysPlayMoney() {
        SysPlayMoneyParameter sysPlayMoney = sysPlayMoneyService.getSysPlayMoney();
        if (Objects.isNull(sysPlayMoney)) {
            return SysPlayMoneyParameterBO.builder().recharge(BigDecimal.ONE).build();
        }
        SysPlayMoneyParameterBO bo = new SysPlayMoneyParameterBO();
        BeanUtils.copyProperties(sysPlayMoney, bo);
        return bo;
    }


}
