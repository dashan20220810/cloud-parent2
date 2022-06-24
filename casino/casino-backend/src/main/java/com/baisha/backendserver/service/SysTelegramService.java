package com.baisha.backendserver.service;

import com.baisha.backendserver.model.SysTelegramParameter;
import com.baisha.backendserver.repository.SysTelegramParameterRepository;
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
public class SysTelegramService {

    @Autowired
    private SysTelegramParameterRepository sysTelegramParameterRepository;


    public SysTelegramParameter getSysTelegram() {
        List<SysTelegramParameter> list = sysTelegramParameterRepository.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return new SysTelegramParameter();
        }
        return list.get(0);
    }


    public int findAllSize() {
        List<SysTelegramParameter> list = sysTelegramParameterRepository.findAll();
        return list.size();
    }


    public SysTelegramParameter save(SysTelegramParameter stp) {
        sysTelegramParameterRepository.save(stp);
        return stp;
    }

    public SysTelegramParameter findById(Long id) {
        Optional<SysTelegramParameter> optional = sysTelegramParameterRepository.findById(id);
        return optional.orElse(null);
    }
}
