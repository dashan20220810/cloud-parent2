package com.baisha.gameserver.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.baisha.gameserver.model.BetResultChange;
import com.baisha.gameserver.repository.BetResultChangeRepository;
import com.baisha.gameserver.vo.BetResultChangePageVO;
import com.baisha.modulecommon.util.PageUtil;


/**
 * @author: alvin
 */
@Service
@Transactional
public class BetResultChangeService {


    @Autowired
    BetResultChangeRepository betResultChangeRepository;

    public void save(BetResultChange betResultChange) {
        betResultChangeRepository.save(betResultChange);
    }
   
    public BetResultChange findCurrentByNoActive (String noActive) {
    	List<BetResultChange> list = betResultChangeRepository.findCurrentByNoActive(noActive, PageUtil.setPageable(1, 1));
    	if ( list==null || list.size()==0 ) {
    		return null;
    	}
    	
    	return list.get(0);
    }


    public Page<BetResultChange> getBetResultChangePage(BetResultChangePageVO vo, Pageable pageable) {
        Specification<BetResultChange> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotBlank(vo.getNoActive())) {
                predicates.add(cb.like(root.get("noActive"), "%" + vo.getNoActive() + "%"));
            }
            if (vo.getTableId() != null) {
                predicates.add(cb.equal(root.get("tableId"), vo.getTableId()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<BetResultChange> page = betResultChangeRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }
}
