package com.baisha.business;

import cn.hutool.core.util.StrUtil;
import com.baisha.model.TgBot;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.modulecommon.Constants;
import com.baisha.service.TgBotService;
import com.baisha.util.TelegramServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class TgBotBusiness {

    @Autowired
    private ControlBotBusiness controlBotBusiness;

    @Autowired
    private TgBotService tgBotService;

    public Page<TgBot> getTgBotPage(TgBotPageVO vo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = TelegramServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<TgBot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StrUtil.isNotEmpty(vo.getBotName())) {
                predicates.add(cb.equal(root.get("botName"), vo.getBotName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return tgBotService.getTgBotPage(spec, pageable);
    }

    public Boolean updateBotSession(Long id, Integer status) {
        TgBot tgBot = tgBotService.findById(id);
        if (tgBot == null) {
            return false;
        }
        if (Constants.open.equals(status)) {
            return controlBotBusiness.startupBot(tgBot.getBotName(), tgBot.getBotToken());
        }
        return controlBotBusiness.shutdownBot(tgBot.getBotName());
    }
}
