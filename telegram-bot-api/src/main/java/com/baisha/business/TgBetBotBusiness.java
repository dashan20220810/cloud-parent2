package com.baisha.business;

import cn.hutool.core.util.StrUtil;
import com.baisha.model.TgBetBot;
import com.baisha.model.vo.TgBetBotPageVO;
import com.baisha.service.TgBetBotService;
import com.baisha.util.TelegramServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class TgBetBotBusiness {
    @Autowired
    private TgBetBotService tgBetBotService;

    public Page<TgBetBot> getTgBetBotPage(TgBetBotPageVO vo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = TelegramServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<TgBetBot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StrUtil.isNotEmpty(vo.getBetBotPhone())) {
                predicates.add(cb.equal(root.get("betBotPhone"), vo.getBetBotPhone()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return tgBetBotService.getTgBetBotPage(spec, pageable);
    }
}
