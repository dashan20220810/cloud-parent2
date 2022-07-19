package com.baisha.casinoweb.service;

import com.baisha.casinoweb.model.OpenCardVideo;
import com.baisha.casinoweb.repository.OpenCardVideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author 小智
 * @Date 14/7/22 6:25 PM
 * @Version 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OpenCardVideoService {
    @Autowired
    private OpenCardVideoRepository openCardVideoRepository;

    public OpenCardVideo save(OpenCardVideo openCardVideo) {
        openCardVideoRepository.save(openCardVideo);
        return openCardVideo;
    }

    public OpenCardVideo findByNoActive(String noActive) {
        return openCardVideoRepository.findByNoActive(noActive);
    }

    public void saveOpenCardVideoAndPic(
            final String openCardVideoAddress, final String picAddress, final String noActive) {
        OpenCardVideo openCardVideo = new OpenCardVideo();
        openCardVideo.setNoActive(noActive);
        openCardVideo.setPicAddress(picAddress);
        openCardVideo.setVideoAddress(openCardVideoAddress);
        save(openCardVideo);
    }
}
