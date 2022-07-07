package com.baisha.gameserver.controller;

import com.baisha.gameserver.model.BsGame;
import com.baisha.gameserver.model.BsOdds;
import com.baisha.gameserver.model.bo.game.GameBO;
import com.baisha.gameserver.service.BsGameService;
import com.baisha.gameserver.service.BsOddsService;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
            String gameCode = "BACC";
            String gameName = "百家乐";
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


}
