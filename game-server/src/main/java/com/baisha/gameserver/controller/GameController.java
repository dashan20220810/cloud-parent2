package com.baisha.gameserver.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
    @ApiOperation(value = "获取游戏倍率限制列表-提供给其他服务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameCode", value = "游戏编码 默认BACC", dataTypeClass = String.class)
    })
    public ResponseEntity<List<BsOdds>> findAllOddsList(String gameCode) {
        if (StringUtils.isEmpty(gameCode)) {
            gameCode = GameTypeEnum.BACC.getCode();
        }
        List<BsOdds> result = bsOddsService.findAllByGameCode(gameCode);
        if (CollectionUtils.isEmpty(result)) {
            result = new ArrayList<>();
        }
        return ResponseUtil.success(result);
    }

    @GetMapping(value = "global/oddsList")
    @ApiOperation(value = "获取游戏倍率限制列表(全局)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameCode", value = "游戏编码 默认BACC", dataTypeClass = String.class)
    })
    public ResponseEntity<List<BsOdds>> findAllGlobalOddsList(String gameCode) {
        if (StringUtils.isEmpty(gameCode)) {
            gameCode = GameTypeEnum.BACC.getCode();
        }
        List<BsOdds> result = bsOddsService.findAllByGameCode(gameCode);
        if (CollectionUtils.isEmpty(result)) {
            result = new ArrayList<>();
        }
        return ResponseUtil.success(result);
    }

    @PostMapping(value = "global/bacc/odds")
    @ApiOperation(value = "设置游戏百家乐赔率(全局)")
    public ResponseEntity<String> setBaccOdds(GameBaccOddsVO vo) {
        if (StringUtils.isEmpty(vo.getGameCode())) {
            return ResponseUtil.parameterNotNull();
        }
        if (checkOdds(vo.getX(), vo.getXMinAmount(), vo.getXMaxAmount())
                || checkOdds(vo.getZ(), vo.getZMinAmount(), vo.getZMaxAmount())
                || checkOdds(vo.getH(), vo.getHMinAmount(), vo.getHMaxAmount())
                || checkOdds(vo.getZd(), vo.getZdMinAmount(), vo.getZdMaxAmount())
                || checkOdds(vo.getXd(), vo.getXdMinAmount(), vo.getXdMaxAmount())
                || checkOdds(vo.getSs2(), vo.getSsMinAmount(), vo.getSsMaxAmount())
                || checkOdds(vo.getSs3(), vo.getSsMinAmount(), vo.getSsMaxAmount())) {
            return new ResponseEntity<>("数据不规范(赔率0-100 限红为整数且大小正确)");
        }
        synchronized (vo.getGameCode()) {
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.X.getCode(), vo.getX(), vo.getXMinAmount(), vo.getXMaxAmount());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.Z.getCode(), vo.getZ(), vo.getZMinAmount(), vo.getZMaxAmount());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.H.getCode(), vo.getH(), vo.getHMinAmount(), vo.getHMaxAmount());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.XD.getCode(), vo.getXd(), vo.getXdMinAmount(), vo.getXdMaxAmount());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.ZD.getCode(), vo.getZd(), vo.getZdMinAmount(), vo.getZdMaxAmount());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.SS2.getCode(), vo.getSs2(), vo.getSsMinAmount(), vo.getSsMaxAmount());
            doSetBaccOdds(vo.getGameCode(), TgBaccRuleEnum.SS3.getCode(), vo.getSs3(), vo.getSsMinAmount(), vo.getSsMaxAmount());
        }
        return ResponseUtil.success();
    }

    private boolean checkOdds(BigDecimal odds, Integer min, Integer max) {
        if (null == odds || null == min || null == max) {
            return true;
        }
        if (odds.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        BigDecimal ge = new BigDecimal("100");
        if (odds.compareTo(ge) > 0) {
            return true;
        }
        Integer ZERO = 0;
        if (max.compareTo(ZERO) < 0 || min.compareTo(min) < 0) {
            return true;
        }
        if (min.compareTo(max) >= 0) {
            return true;
        }
        return false;
    }

    private void doSetBaccOdds(String gameCode, String ruleCode, BigDecimal odds, Integer min, Integer max) {
        BsOdds bsOdds = bsOddsService.findByGameCodeAndRuleCode(gameCode, ruleCode);
        if (Objects.isNull(bsOdds)) {
            bsOdds = new BsOdds();
            bsOdds.setGameCode(gameCode);
            bsOdds.setRuleCode(ruleCode);
            bsOdds.setRuleName(TgBaccRuleEnum.nameOfCode(ruleCode).getName());
        }
        bsOdds.setOdds(odds);
        bsOdds.setMinAmount(min);
        bsOdds.setMaxAmount(max);
        bsOddsService.save(bsOdds);
    }


}
