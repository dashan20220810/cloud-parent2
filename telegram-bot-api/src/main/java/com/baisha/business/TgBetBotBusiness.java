package com.baisha.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baisha.handle.CommonHandler;
import com.baisha.model.TgBetBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.OddsAndLimitVO;
import com.baisha.model.vo.TgBetBotPageVO;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.service.TgBetBotService;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TelegramServerUtil;
import com.baisha.util.constants.CommonConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;

import static com.baisha.util.constants.BotConstant.DEFAULT_USER_ID;

@Slf4j
@Service
public class TgBetBotBusiness {

    @Autowired
    private TgBetBotService tgBetBotService;

    @Autowired
    private CommonHandler commonHandler;

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

    @Async
    public void betBotSendMessage(TgChat tgChat, TgBetBot tgBetBot, List<OddsAndLimitVO> redLimits) {
        // ??????????????????
        String startTime = tgBetBot.getBetStartTime();
        String endTime = tgBetBot.getBetEndTime();
        if (StrUtil.isEmpty(startTime) || StrUtil.isEmpty(endTime)) {
            return;
        }
        DateTime betStartTime = DateUtil.parse(startTime, "HH:mm:ss");
        DateTime betEndTime = DateUtil.parse(endTime, "HH:mm:ss");
        // ????????????
        String now = DateUtil.format(new Date(), "HH:mm:ss");
        DateTime nowTime = DateUtil.parse(now, "HH:mm:ss");
        if (nowTime.isBefore(betStartTime) || nowTime.isAfter(betEndTime)) {
            // ?????????????????????
            return;
        }
        // ??????????????????
        Integer betFrequency = tgBetBot.getBetFrequency();
        if (null == betFrequency) {
            return;
        }
        if (CommonConstant.CONSTANT_0.equals(betFrequency)) {
            return;
        }
        int betFrequencyRandom = TelegramBotUtil.getRandom(0, 10);
        if (betFrequencyRandom > betFrequency) {
            // ???????????? ??????????????????
            return;
        }
        // ??????????????????
        String betContentsStr = tgBetBot.getBetContents();
        if (StrUtil.isEmpty(betContentsStr)) {
            return;
        }
        String[] betContentStr = betContentsStr.split(",");
        List<String> betContents = new ArrayList<>(Arrays.asList(betContentStr));
        // D,H,SB,SS ????????????????????? ???????????????
        if (betContents.contains(BetOption.ZD.name())) {
            betContents.add(BetOption.ZD.name());
            betContents.add(BetOption.ZD.name());
        }
        if (betContents.contains(BetOption.XD.name())) {
            betContents.add(BetOption.XD.name());
            betContents.add(BetOption.XD.name());
        }
        if (betContents.contains(BetOption.Z.name())) {
            betContents.add(BetOption.Z.name());
            betContents.add(BetOption.Z.name());
            betContents.add(BetOption.Z.name());
            betContents.add(BetOption.Z.name());
        }
        if (betContents.contains(BetOption.X.name())) {
            betContents.add(BetOption.X.name());
            betContents.add(BetOption.X.name());
            betContents.add(BetOption.X.name());
            betContents.add(BetOption.X.name());
        }
        int index = TelegramBotUtil.getRandom(0, betContents.size() - 1);
        // ?????????????????????
        String betContent = betContents.get(index);
        // ???????????????????????????
        Integer minAmountLimit;
        if (betContent.equals(BetOption.D.name())) {
            Integer minAmountLimitZD = commonHandler.getMinAmountLimit(BetOption.ZD.name(), redLimits);
            Integer minAmountLimitXD = commonHandler.getMinAmountLimit(BetOption.XD.name(), redLimits);
            List<Integer> list = Lists.newArrayList();
            list.add(minAmountLimitZD);
            list.add(minAmountLimitXD);
            minAmountLimit = CollUtil.max(list);
        } else if (betContent.equals(BetOption.SB.name())) {
            Integer minAmountLimitZD = commonHandler.getMinAmountLimit(BetOption.ZD.name(), redLimits);
            Integer minAmountLimitXD = commonHandler.getMinAmountLimit(BetOption.XD.name(), redLimits);
            Integer minAmountLimitH = commonHandler.getMinAmountLimit(BetOption.H.name(), redLimits);
            List<Integer> list = Lists.newArrayList();
            list.add(minAmountLimitZD);
            list.add(minAmountLimitXD);
            list.add(minAmountLimitH);
            minAmountLimit = CollUtil.max(list);
        } else if (betContent.equals(BetOption.SS.name())) {
            betContent = BetOption.SS.name() + "2";
            minAmountLimit = commonHandler.getMinAmountLimit(betContent, redLimits);
        } else {
            minAmountLimit = commonHandler.getMinAmountLimit(betContent, redLimits);
        }
        List<BigDecimal> amounts = Lists.newArrayList();
        Integer minMultiple = tgBetBot.getMinMultiple();
        Integer maxMultiple = tgBetBot.getMaxMultiple();
        if (null == minMultiple || null == maxMultiple) {
            return;
        }
        for (int i = minMultiple; i <= maxMultiple; i++) {
            amounts.add(NumberUtil.mul(minAmountLimit.toString(), String.valueOf(i)));
        }
        int indexAmount = TelegramBotUtil.getRandom(0, amounts.size() - 1);
        BigDecimal amount = amounts.get(indexAmount);
        // ???????????????-????????????
        int random = TelegramBotUtil.getRandom(5, 35);
        try {
            Thread.sleep(Long.parseLong(random + "000"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//            myBot.sendMessage(betContent + amount, tgChat.getChatId()+"");

        log.info("???????????????-????????????,???????????????:{},???????????????:{},????????????:{},???:{}", tgBetBot.getBetBotName(), tgBetBot.getBetBotPhone(), betContent + amount, tgChat.getChatName());

        // ?????????????????????????????????
        commonHandler.betBotSendMessage(tgBetBot.getBetBotPhone(), betContent + amount, tgChat.getChatName());
    }
}
