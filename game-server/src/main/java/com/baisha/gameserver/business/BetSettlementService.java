package com.baisha.gameserver.business;

import com.baisha.gameserver.service.BetService;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yihui
 */
@Slf4j
@Service
public class BetSettlementService {

    @Autowired
    private BetService betService;

    @Transactional(rollbackFor = Exception.class)
    public boolean betSettlement(BetSettleVO vo) {
        log.info("===============开始结算=================");
        //下注成功状态
        int status = 1;
        betService.findBetNoSettle(vo.getNoActive(), status);

        log.info("===============结束结算=================");
        return false;
    }

}
