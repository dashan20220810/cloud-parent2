package com.baisha.gameserver.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baisha.gameserver.model.Desk;
import com.baisha.gameserver.repository.DeskRepository;
import com.baisha.gameserver.vo.DeskPageVO;
import com.baisha.modulecommon.reponse.ResponseUtil;
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
    public Desk save(Desk desk) {
        deskRepository.save(desk);
        return desk;
    }

    public List<Desk> findAllDeskList() {
        return deskRepository.findAllDeskList();
    }

    public Desk findByLocalIp(String localIp) {
        return deskRepository.findByLocalIp(localIp);
    }

    public Desk findByDeskCode(String deskCode) {
        return deskRepository.findByDeskCode(deskCode);
    }

    @Cacheable(key = "#id", unless = "#result == null")
    public Desk findById(Long id) {
        Optional<Desk> optional = deskRepository.findById(id);
        return optional.orElse(null);
    }

    @CacheEvict(key = "#id")
    public void delete(Long id) {
        deskRepository.deleteById(id);
    }

    @Caching(put = {@CachePut(key = "#id")})
    public Desk updateStatus(Long id, Integer status) {
        int i = deskRepository.updateStatusById(status, id);
        if (i > 0) {
            return deskRepository.findById(id).get();
        }
        return null;
    }

    public Page<Desk> getDeskPage(DeskPageVO vo) {
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber(), vo.getPageSize());
        Specification<Desk> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
//            if (StringUtils.isNotBlank(vo.getUserName())) {
//                predicates.add(cb.like(root.get("userName"), "%" +vo.getUserName() +"%"));
//            }

            if (vo.getDeskCode() != null) {
                predicates.add(cb.equal(root.get("deskCode"), vo.getDeskCode()));
            }

            if (vo.getName() != null) {
                predicates.add(cb.equal(root.get("name"), vo.getName()));
            }

            if (vo.getLocalIp() != null) {
                predicates.add(cb.equal(root.get("localIp"), vo.getLocalIp()));
            }

            if (vo.getVideoAddress() != null) {
                predicates.add(cb.equal(root.get("videoAddress"), vo.getVideoAddress()));
            }

            if (vo.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), vo.getStatus()));
            }

            if (vo.getGameCode() != null) {
                predicates.add(cb.equal(root.get("gameCode"), vo.getGameCode()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Page<Desk> page = deskRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @CachePut(key = "#id", unless = "#result == null")
    public Desk update(Long id, String localIp, String videoAddress, String nearVideoAddress
    		,String closeVideoAddress, String gameCode, Integer status, String name) {
        int n = deskRepository.update(id, localIp, videoAddress, nearVideoAddress, closeVideoAddress, gameCode, status, name, new Date());
        if (n>0) {
        	return deskRepository.findById(id).get();
        }
        return null;
    }
    
    /**
     * 正確返回空字串
     * @param deskId 新增null，修改必帶值
     * @param deskCode
     * @param name
     * @param localIp
     * @return
     */
    public String validateDuplicateField (Long deskId, String deskCode, String name, String localIp) {
    	
    	Desk desk = null;
    	
    	if (deskId==null) {
    		desk = deskRepository.findByDeskCode(deskCode);
    		if (desk != null) {
    			return "桌台编号已存在或被占用";
    		}

    		desk = deskRepository.findByName(name);
    		if (desk != null) {
    			return "桌台名称已存在或被占用";
    		}

    		desk = deskRepository.findByLocalIp(localIp);
    		if (desk != null) {
    			return "内网IP已占用";
    		}
    	} else {
    		desk = deskRepository.findByDeskCode(deskCode);
    		if (desk != null && deskId != desk.getId()) {
    			return "桌台编号已存在或被占用";
    		}

    		desk = deskRepository.findByName(name);
    		if (desk != null && deskId != desk.getId()) {
    			return "桌台名称已存在或被占用";
    		}

    		desk = deskRepository.findByLocalIp(localIp);
    		if (desk != null && deskId != desk.getId()) {
    			return "内网IP已占用";
    		}
    	}
    	
    	return "";
    }

}
