package com.baisha.gameserver.business;

import com.baisha.gameserver.model.BsOdds;
import com.baisha.gameserver.model.bo.game.GameBaccOddsBO;
import com.baisha.gameserver.service.BsOddsService;
import com.baisha.modulecommon.enums.GameTypeEnum;
import com.baisha.modulecommon.enums.TgBaccRuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yihui
 */
@Slf4j
@Service
public class GameBusiness {

    @Autowired
    private BsOddsService bsOddsService;


    /**
     * 获取TG百家乐玩法赔率
     *
     * @return
     */
    public GameBaccOddsBO getBaccOdds() {
        String gameCode = GameTypeEnum.BACC.getCode();
        List<BsOdds> oddsList = bsOddsService.findAllByGameCode(gameCode);
        if (CollectionUtils.isEmpty(oddsList)) {
            return new GameBaccOddsBO();
        }
        return transOddsBo(oddsList);
    }


    private GameBaccOddsBO transOddsBo(List<BsOdds> list) {
        GameBaccOddsBO bo = new GameBaccOddsBO();
        for (BsOdds g : list) {
            BigDecimal odds = g.getOdds();
            String ruleCode = g.getRuleCode().toUpperCase();
            if (ruleCode.equals(TgBaccRuleEnum.Z.getCode())) {
                bo.setZ(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.X.getCode())) {
                bo.setX(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.H.getCode())) {
                bo.setH(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.ZD.getCode())) {
                bo.setZd(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.XD.getCode())) {
                bo.setXd(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.SS2.getCode())) {
                bo.setSs2(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.SS3.getCode())) {
                bo.setSs3(odds);
            }
        }
        return bo;
    }

}
