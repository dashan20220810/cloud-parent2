package com.baisha.backendserver.service;

import com.baisha.backendserver.model.BlockIp;
import com.baisha.backendserver.repository.BlockIpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@CacheConfig(cacheNames = "backend::blockIp")
@Transactional(rollbackFor = Exception.class)
public class BlockIpService {

    @Autowired
    private BlockIpRepository blockIpRepository;

    public BlockIp findByIp(String ip){
        return blockIpRepository.findByIp(ip);
    }

}
