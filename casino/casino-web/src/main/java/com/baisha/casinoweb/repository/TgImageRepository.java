package com.baisha.casinoweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.baisha.casinoweb.model.TgImage;


/**
 * @author: alvin
 */
public interface TgImageRepository extends JpaRepository<TgImage ,Long>, JpaSpecificationExecutor<TgImage> {
	
	
	/**
	 * @param tgImage
	 * @return
	 */
	TgImage findByTgImage ( String tgImage );
}
