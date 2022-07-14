package com.baisha.casinoweb.repository;

import com.baisha.casinoweb.model.OpenCardVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Author 小智
 * @Date 14/7/22 6:29 PM
 * @Version 1.0
 */
public interface OpenCardVideoRepository extends JpaRepository<OpenCardVideo, Long>, JpaSpecificationExecutor<OpenCardVideo> {

    OpenCardVideo findByNoActive(String noActive);
}
