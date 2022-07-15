package com.baisha.backendserver.service;

import com.baisha.backendserver.model.BetResultChange;
import com.baisha.backendserver.repository.BetResultChangeRepository;
import com.baisha.modulecommon.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author yihui
 */
@Service
@Transactional
public class BetResultChangeService {


    @Autowired
    BetResultChangeRepository betResultChangeRepository;

    public void save(BetResultChange betResultChange) {
        betResultChangeRepository.save(betResultChange);
    }

    public BetResultChange findCurrentByNoActive(String noActive) {
        List<BetResultChange> list = betResultChangeRepository.findCurrentByNoActive(noActive, PageUtil.setPageable(1, 1));
        if (list == null || list.size() == 0) {
            return null;
        }

        return list.get(0);
    }


    /*public Page<BetResultChange> getBetResultChangePage(BetResultChangePageVO vo, Pageable pageable) {
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
    }*/

}
