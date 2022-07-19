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

import com.baisha.gameserver.model.BetResult;
import com.baisha.gameserver.repository.BetResultRepository;
import com.baisha.gameserver.vo.BetResultPageVO;

/**
 * @author: alvin
 */
@Service
@Transactional
public class BetResultService {


    @Autowired
    BetResultRepository betResultRepository;

    public void save(BetResult betResult) {
        betResultRepository.save(betResult);
    }

    public void update(String noActive, String awardOption) {
        betResultRepository.UpdateAwardOptionByTableIdAndNoActive(awardOption, noActive);
    }

    public void updateReopenByNoActive(String noActive) {
        betResultRepository.updateReopenByNoActive(noActive);
    }

    public BetResult findByNoActive(String noActive) {
        return betResultRepository.findByNoActive(noActive);
    }

    public Page<BetResult> getBetResultPage(BetResultPageVO vo, Pageable pageable) {
        Specification<BetResult> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotEmpty(vo.getNoActive())) {
                predicates.add(cb.equal(root.get("noActive"), vo.getNoActive()));
            }
            if (vo.getTableId() != null) {
                predicates.add(cb.equal(root.get("tableId"), vo.getTableId()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<BetResult> page = betResultRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    public int updateReopenAndAwardOptionByNoActive(String noActive, String awardOption) {
        return betResultRepository.updateReopenAndAwardOptionByNoActive(noActive, awardOption);
    }
}
