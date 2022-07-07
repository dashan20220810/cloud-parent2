package com.baisha.backendserver.service;

import com.baisha.backendserver.model.SysPlayMoneyParameter;
import com.baisha.backendserver.repository.SysPlayMoneyParameterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author yihui
 */
@Slf4j
@Service
public class SysPlayMoneyService {

    @Autowired
    private SysPlayMoneyParameterRepository sysplayMoneyParameterRepository;

    public SysPlayMoneyParameter getSysPlayMoney() {
        List<SysPlayMoneyParameter> list = sysplayMoneyParameterRepository.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return new SysPlayMoneyParameter();
        }
        return list.get(0);
    }


    public int findAllSize() {
        List<SysPlayMoneyParameter> list = sysplayMoneyParameterRepository.findAll();
        return list.size();
    }


    public SysPlayMoneyParameter findById(Long id) {
        Optional<SysPlayMoneyParameter> optional = sysplayMoneyParameterRepository.findById(id);
        return optional.orElse(null);
    }


    public SysPlayMoneyParameter save(SysPlayMoneyParameter stp) {
        sysplayMoneyParameterRepository.save(stp);
        return stp;
    }
}
