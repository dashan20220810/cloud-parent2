package com.baisha.controller;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.business.TgBotBusiness;
import com.baisha.model.TgBot;
import com.baisha.model.vo.StatusVO;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
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
    private TgBotBusiness tgBotBusiness;

    @ApiOperation("新开机器人")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value="机器人名称", required = true),
        @ApiImplicitParam(name = "token", value="机器人token", required = true),
        @ApiImplicitParam(name = "chatId", value="TG群id", required = true),
        @ApiImplicitParam(name = "chatName", value = "TG群名称", required = true),
        @ApiImplicitParam(name = "createBy", value="创建人"),
        @ApiImplicitParam(name = "updateBy", value="最后更新人")
    })
    @PostMapping("open")
    public ResponseEntity open(String username, String token, String chatId, String chatName, String createBy, String updateBy) {
        // 参数校验
        if (CommonUtil.checkNull(username, token, chatId, chatName)) {
            return ResponseUtil.parameterNotNull();
        }
        try {
            if (null != tgBotService.findByBotName(username)) {
                return ResponseUtil.custom("当前机器人已经存在！");
            }
            // 实例化机器人
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramLongPollingBot(username, token, chatId, chatName));
            // 保存到DB
            TgBot tgBot = (TgBot) new TgBot()
                    .setBotName(username)
                    .setBotToken(token)
                    .setChatId(chatId)
                    .setStatus(Constants.open)
                    .setCreateBy(createBy)
                    .setUpdateBy(updateBy);
            tgBotService.save(tgBot);
            return ResponseUtil.success();
        } catch (Throwable e) {
            log.error("Token错误，请填写正确的机器人Token", e);
            return ResponseUtil.custom("Token错误，请填写正确的机器人Token");
        }
    }

    @ApiOperation("分页查询")
    @PostMapping("page")
    public ResponseEntity<Page<TgBot>> page(TgBotPageVO vo) {
        Page<TgBot> pageList = tgBotBusiness.getTgBotPage(vo);
        return ResponseUtil.success(pageList);
    }

    @ApiOperation("更新状态 0禁用 1启用")
    @PostMapping("updateStatusById")
    public ResponseEntity updateStatusById(StatusVO statusVO) {
        Long id = statusVO.getId();
        Integer status = statusVO.getStatus();
        // 参数校验
        if (CommonUtil.checkNull(id.toString(), status.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        // 更新状态
        tgBotService.updateStatusById(id, status);
        return ResponseUtil.success();
    }
}
