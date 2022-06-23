package com.baisha.casinoweb.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baisha.casinoweb.model.TgImage;
import com.baisha.casinoweb.repository.TgImageRepository;

/**
 * @author: alvin
 */
@Service
@Transactional
@CacheConfig(cacheNames = "tgImage::image")
public class TgService {

	
	@Autowired
	private TgImageRepository tgImageRepository;

    @Cacheable
	public TgImage findByTgImage ( String tgImage ) {
    	return tgImageRepository.findByTgImage(tgImage);
	}
	
}
