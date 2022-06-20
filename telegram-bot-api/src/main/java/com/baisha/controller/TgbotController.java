package com.baisha.controller;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.constants.BotConstant;
import com.baisha.constants.TgBotRedisConstant;
import com.baisha.model.TgBot;
import com.baisha.model.vo.StatusVO;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import com.baisha.service.TgBotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Api(tags = "机器人管理")
@Slf4j
@RestController
@RequestMapping("tgBot")
public class TgbotController {

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("新开机器人")
    @ApiImplicitParams({
        @ApiImplicitParam(name="username", value="机器人名称", required = true),
        @ApiImplicitParam(name="token", value="机器人token", required = true),
        @ApiImplicitParam(name="chatId", value="TG群id", required = true)
    })
    @PostMapping("open")
    public ResponseEntity open(String username, String token, String chatId) {
        // 参数校验
        if (CommonUtil.checkNull(username, token, chatId)) {
            return ResponseUtil.parameterNotNull();
        }
        try {
            // 实例化机器人
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramLongPollingBot(username, token, chatId));
            // 保存到DB
            TgBot tgBot = (TgBot) new TgBot()
                    .setBotName(username)
                    .setBotToken(token)
                    .setChatId(chatId)
                    .setStatus(BotConstant.NORMAL)
                    .setCreateBy("测试数据")
                    .setUpdateBy("测试数据");
            ResponseEntity responseEntity = tgBotService.saveTgBot(tgBot);
            if (responseEntity.getCode() == 0) {
                // 存入redis
                TgBot tgBotRedis = (TgBot) responseEntity.getData();
                redisUtil.set(TgBotRedisConstant.TG_BOT_SAVE_PREFIX + username, tgBotRedis);
                return ResponseUtil.success();
            }
            return responseEntity;
        } catch (Throwable e) {
            log.error("新开机器人失败", e);
            return ResponseUtil.custom("新开机器人失败");
        }
    }

    @ApiOperation("分页查询")
    @PostMapping("page")
    public ResponseEntity<Page<TgBot>> page(TgBotPageVO vo) {
        Page<TgBot> pageList = tgBotService.getTgBotPage(vo);
        return ResponseUtil.success(pageList);
    }

    @ApiOperation("更新状态 1正常 2禁用")
    @PostMapping("updateStatus")
    public ResponseEntity updateStatus(StatusVO statusVO) {
        Long id = statusVO.getId();
        Integer status = statusVO.getStatus();
        // 参数校验
        if (CommonUtil.checkNull(id.toString(), status.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        // 更新状态
        tgBotService.updateStatus(id, status);
        return ResponseUtil.success();
    }
}
