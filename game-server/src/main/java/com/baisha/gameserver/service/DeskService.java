package com.baisha.gameserver.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baisha.gameserver.model.Desk;
import com.baisha.gameserver.repository.DeskRepository;
import com.baisha.gameserver.vo.DeskPageVO;
import com.baisha.modulecommon.util.PageUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author alvin
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "desk")
@Transactional(rollbackFor = Exception.class)
public class DeskService {

    @Autowired
    private DeskRepository tgDeskRepository;
    
    public void save ( Desk desk ) {
    	tgDeskRepository.save(desk);
    }

    public List<Desk> findAllDeskList() {
        return tgDeskRepository.findAllDeskList();
    }

    @Cacheable(key = "#localIp", unless = "#result == null")
    public Desk findByLocalIp ( String localIp ) {
    	return tgDeskRepository.findByLocalIp(localIp);
    }

    @Cacheable(key = "#deskCode", unless = "#result == null")
    public Desk findByDeskCode ( String deskCode ) {
    	return tgDeskRepository.findByDeskCode(deskCode);
    }

    @Cacheable(key = "#id", unless = "#result == null")
    public Desk findById ( Long id ) {
        Optional<Desk> optional = tgDeskRepository.findById(id);
        return optional.orElse(null);
    }


    public Page<Desk> getDeskPage(DeskPageVO vo) {
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<Desk> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
//            if (StringUtils.isNotBlank(vo.getUserName())) {
//                predicates.add(cb.like(root.get("userName"), "%" +vo.getUserName() +"%"));
//            }
            
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        
        Page<Desk> page = tgDeskRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

}
