package com.baisha.gameserver.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.BsGame;
import com.baisha.gameserver.model.BsOdds;
import com.baisha.gameserver.model.bo.game.GameBO;
import com.baisha.gameserver.service.BsGameService;
import com.baisha.gameserver.service.BsOddsService;
import com.baisha.gameserver.vo.GameBaccOddsVO;
import com.baisha.modulecommon.enums.GameTypeEnum;
import com.baisha.modulecommon.enums.TgBaccRuleEnum;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

//@Slf4j
@RestController
@RequestMapping("game")
@Api(tags = {"游戏"})
public class GameController {

    @Autowired
    private BsGameService bsGameService;
    @Autowired
    private BsOddsService bsOddsService;


    @GetMapping(value = "gameCode")
    @ApiOperation(value = "获取游戏编码列表")
    public ResponseEntity<List<GameBO>> findAllList() {
        List<BsGame> list = bsGameService.findAll();
        if (CollectionUtils.isEmpty(list)) {
            //如果为空，就默认百家乐 因为目前TG投注只有百家乐
            String gameCode = GameTypeEnum.BACC.getCode();
            String gameName = GameTypeEnum.BACC.getName();
            BsGame bsGame = new BsGame();
            bsGame.setGameCode(gameCode);
            bsGame.setGameName(gameName);
            bsGameService.save(bsGame);
            list = bsGameService.findAll();
        }
        List<GameBO> result = new ArrayList<>();
        for (BsGame b : list) {
            GameBO gameBO = new GameBO();
            gameBO.setCode(b.getGameCode());
            gameBO.setName(b.getGameName());
            result.add(gameBO);
        }
        return ResponseUtil.success(result);
    }


    @GetMapping(value = "oddsList")
    @ApiOperation(value = "获取游戏倍率列表")
    public ResponseEntity<List<BsOdds>> findAllOddsList(String gameCode) {
        if (StringUtils.isEmpty(gameCode)) {
            return ResponseUtil.parameterNotNull();
        }
        List<BsOdds> result = bsOddsService.findAllByGameCode(gameCode);
        if (CollectionUtils.isEmpty(result)) {
            result = new ArrayList<>();
        }
        return ResponseUtil.success(result);
    }

    @PostMapping(value = "bacc/odds")
    @ApiOperation(value = "设置游戏百家乐赔率")
    public ResponseEntity<String> setBaccOdds(GameBaccOddsVO vo) {
        if (StringUtils.isEmpty(vo.getGameCode())) {
            return ResponseUtil.parameterNotNull();
        }
        if (null == vo.getX() || vo.getX().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getZ() || vo.getZ().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getH() || vo.getH().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getZd() || vo.getZd().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getXd() || vo.getXd().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getSs2() || vo.getSs2().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getSs3() || vo.getSs3().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseUtil.custom("赔率不规范");
        }
        synchronized (vo.getGameCode()) {
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.X.getCode(), vo.getX());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.Z.getCode(), vo.getZ());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.H.getCode(), vo.getH());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.XD.getCode(), vo.getXd());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.ZD.getCode(), vo.getZd());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.SS2.getCode(), vo.getSs2());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.SS3.getCode(), vo.getSs3());
        }
        return ResponseUtil.success();
    }

    private void doSetBaccOdds(String gameCode, String ruleCode, BigDecimal odds) {
        BsOdds bsOdds = bsOddsService.findByGameCodeAndRuleCode(gameCode, ruleCode);
        if (Objects.isNull(bsOdds)) {
            bsOdds = new BsOdds();
            bsOdds.setGameCode(gameCode);
            bsOdds.setOdds(odds);
            bsOdds.setRuleCode(ruleCode);
            bsOdds.setRuleName(TgBaccRuleEnum.nameOfCode(ruleCode).getName());
        } else {
            //更新
            bsOdds.setOdds(odds);
        }
        bsOddsService.save(bsOdds);
    }


}
