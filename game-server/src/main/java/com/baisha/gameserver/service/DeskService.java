package com.baisha.gameserver.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baisha.gameserver.enums.GameType;
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
    private DeskRepository deskRepository;

    @CachePut(value = "desk", key = "#desk.id")
    public void save ( Desk desk ) {
    	deskRepository.save(desk);
    }

    public List<Desk> findAllDeskList() {
        return deskRepository.findAllDeskList();
    }

    public Desk findByLocalIp ( String localIp ) {
    	return deskRepository.findByLocalIp(localIp);
    }

    public Desk findByDeskCode ( String deskCode ) {
    	return deskRepository.findByDeskCode(deskCode);
    }

    @Cacheable(key = "#id", unless = "#result == null")
    public Desk findById ( Long id ) {
        Optional<Desk> optional = deskRepository.findById(id);
        return optional.orElse(null);
    }

    @CacheEvict(key = "#id")
    public void delete ( Long id ) {
    	deskRepository.deleteById(id);
    }
    
    @CachePut(key = "#id")
    public int updateStatus ( Long id, Integer status ) {
    	return deskRepository.updateStatusById(status, id);
    }

    public Page<Desk> getDeskPage(DeskPageVO vo) {
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<Desk> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
//            if (StringUtils.isNotBlank(vo.getUserName())) {
//                predicates.add(cb.like(root.get("userName"), "%" +vo.getUserName() +"%"));
//            }
            
            if ( vo.getDeskCode()!=null ) {
                predicates.add(cb.equal(root.get("deskCode"), vo.getDeskCode() ));
            }

            if ( vo.getLocalIp()!=null ) {
                predicates.add(cb.equal(root.get("localIp"), vo.getLocalIp() ));
            }

            if ( vo.getVideoAddress()!=null ) {
                predicates.add(cb.equal(root.get("videoAddress"), vo.getVideoAddress() ));
            }

            if ( vo.getStatus()!=null ) {
                predicates.add(cb.equal(root.get("status"), vo.getStatus() ));
            }

            if ( vo.getGameCode()!=null ) {
                predicates.add(cb.equal(root.get("gameCode"), vo.getGameCode() ));
            }
            
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        
        Page<Desk> page = deskRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }
    
    @CachePut(key = "#id")
    public int update ( Long id, String localIp, String videoAddress, String gameCode, Integer status ) {
    	return deskRepository.update(id, localIp, videoAddress, gameCode, status);
    }

}
