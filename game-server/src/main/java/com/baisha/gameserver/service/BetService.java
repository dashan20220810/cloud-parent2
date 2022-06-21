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

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.repository.BetRepository;
import com.baisha.gameserver.vo.BetPageVO;
import com.baisha.modulecommon.util.PageUtil;

/**
 * @author: alvin
 */
@Service
@Transactional
public class BetService {


    @Autowired
    BetRepository betRepository;
	
    public void save ( Bet bet ) {
    	betRepository.save(bet);
    }

    public Page<Bet> getBetPage(BetPageVO vo) {
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<Bet> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotBlank(vo.getUserName())) {
                predicates.add(cb.like(root.get("userName"), "%" +vo.getUserName() +"%"));
            }
            
            if ( vo.getBetOption()!=null ) {
                predicates.add(cb.equal(root.get("betOption"), vo.getBetOption() ));
            }
            
            if ( StringUtils.isNotBlank(vo.getClientType()) ) {
                predicates.add(cb.equal(root.get("clientType"), vo.getClientType() ));
            }
            
            if ( StringUtils.isNotBlank(vo.getNoRun()) ) {
                predicates.add(cb.equal(root.get("noRun"), vo.getNoRun() ));
            }
            
            if ( StringUtils.isNotBlank(vo.getNoActive()) ) {
                predicates.add(cb.equal(root.get("noActive"), vo.getNoActive() ));
            }
            
            if ( vo.getStatus()!=null ) {
                predicates.add(cb.equal(root.get("status"), vo.getStatus() ));
            }
            
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<Bet> page = betRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }
}