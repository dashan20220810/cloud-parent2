package com.baisha.business;

import cn.hutool.core.util.StrUtil;
import com.baisha.model.TgChat;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class TgChatBusiness {

    @Autowired
    private TgChatService tgChatService;

    public Page<TgChat> getTgChatPage(TgBotPageVO vo) {
        Pageable pageable = TelegramServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize());
        Specification<TgChat> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StrUtil.isNotEmpty(vo.getBotName())) {
                predicates.add(cb.equal(root.get("botName"), vo.getBotName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return tgChatService.getTgChatPage(spec, pageable);
    }
}
