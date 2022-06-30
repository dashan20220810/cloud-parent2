package com.baisha.backendserver.business;

import com.baisha.backendserver.model.bo.sys.SysPlayMoneyParameterBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PlayMoneyService {


    /**
     * 获取 打码量 倍率
     *
     * @return
     */
    public SysPlayMoneyParameterBO getSysPlayMoney() {
        return SysPlayMoneyParameterBO.builder().recharge(BigDecimal.ONE).build();
    }


}
