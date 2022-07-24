package com.baisha.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baisha.model.TgBetBot;
import com.baisha.model.TgChat;
import com.baisha.model.TgChatBetBotRelation;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.model.vo.TgChatBetBotBindingVO;
import com.baisha.modulecommon.Constants;
import com.baisha.service.TgBetBotService;
import com.baisha.service.TgChatBetBotRelationService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramServerUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class TgChatBusiness {

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private TgChatBetBotRelationService tgChatBetBotRelationService;

    @Autowired
    private TgBetBotService tgBetBotService;

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

    public List<TgChatBetBotBindingVO> findRelationByTgChatId(Long tgChatId) {
        List<TgChatBetBotBindingVO> result = Lists.newArrayList();

        List<TgBetBot> tgBetBots = tgBetBotService.findByStatus(Constants.open);
        List<TgChatBetBotRelation> relations = tgChatBetBotRelationService.findByTgChatId(tgChatId);
        for (TgBetBot tgBetBot : tgBetBots) {
            TgChatBetBotBindingVO vo = new TgChatBetBotBindingVO();
            vo.setTgBetBotId(tgBetBot.getId());
            vo.setTgBetBotName(tgBetBot.getBetBotName());
            vo.setTgBetBotPhone(tgBetBot.getBetBotPhone());
            // 循环关系表
            if (CollUtil.isEmpty(relations)) {
                vo.setBindingStatus(Constants.close);
                result.add(vo);
                continue;
            }
            for (TgChatBetBotRelation relation : relations) {
                if (tgBetBot.getId().equals(relation.getTgBetBotId())) {
                    vo.setBindingStatus(Constants.open);
                    break;
                }
                vo.setBindingStatus(Constants.close);
            }
            result.add(vo);
        }
        return result;
    }

    public Boolean confirmBind(Long tgChatId, String tgBetBotIds) {
        List<String> tgBetBotIdList = Lists.newArrayList(Arrays.asList(tgBetBotIds.split(",")));
        // 先删除
        tgChatBetBotRelationService.deleteByTgChatId(tgChatId);
        tgBetBotIdList.forEach(tgBetBotId -> {
            // 再重新添加
            TgChatBetBotRelation relation = new TgChatBetBotRelation();
            relation.setTgChatId(tgChatId);
            if (null != tgBetBotService.findById(Long.parseLong(tgBetBotId))) {
                relation.setTgBetBotId(Long.parseLong(tgBetBotId));
            }
            tgChatBetBotRelationService.save(relation);
        });
        return true;
    }
}
