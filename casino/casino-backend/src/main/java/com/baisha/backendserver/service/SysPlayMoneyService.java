package com.baisha.backendserver.service;

import com.baisha.backendserver.model.SysPlayMoneyParameter;
import com.baisha.backendserver.repository.SysPlayMoneyParameterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class SysPlayMoneyService {

    @Autowired
    private SysPlayMoneyParameterRepository playMoneyParameterRepository;

    public SysPlayMoneyParameter getSysPlayMoney() {
        List<SysPlayMoneyParameter> list = playMoneyParameterRepository.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return new SysPlayMoneyParameter();
        }
        return list.get(0);
    }


    public int findAllSize() {
        List<SysPlayMoneyParameter> list = playMoneyParameterRepository.findAll();
        return list.size();
    }


}
