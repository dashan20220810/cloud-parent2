package com.baisha.controller;

import com.baisha.business.TgBetBotBusiness;
import com.baisha.model.TgBetBot;
import com.baisha.model.vo.StatusVO;
import com.baisha.model.vo.TgBetBotPageVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.service.TgBetBotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.generics.BotSession;

@Api(tags = "投注机器人管理")
@Slf4j
@RestController
@RequestMapping("tgBetBot")
public class TgBetBotController {

    @Autowired
    private TgBetBotService tgBetBotService;

    @Autowired
    private TgBetBotBusiness tgBetBotBusiness;

    @ApiOperation("新开机器人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "机器人名称", required = true),
            @ApiImplicitParam(name = "token", value = "机器人token", required = true),
    })
    @PostMapping("open")
    public ResponseEntity open(String username, String token) {
        // 参数校验
        if (CommonUtil.checkNull(username, token)) {
            return ResponseUtil.parameterNotNull();
        }

        //啟動機器人
        boolean isSuccess = tgBetBotBusiness.startupBot(username, token);
        if (!isSuccess) {
            return ResponseUtil.custom("机器人启动失败，请联系技术处理");
        }

        //启动机器人成功更新机器人资料
        TgBetBot tgBetBot = tgBetBotService.findByBetBotName(username);
        if (ObjectUtils.isEmpty(tgBetBot) || StringUtils.isEmpty(tgBetBot.getBetBotName())) {
            //新增
            tgBetBot = new TgBetBot();
            tgBetBot.setBetBotName(username)
                    .setBetBotToken(token);
        }
        tgBetBot.setStatus(Constants.open);
        tgBetBotService.save(tgBetBot);

        return ResponseUtil.success();
    }

    @ApiOperation("分页查询")
    @PostMapping("page")
    public ResponseEntity<Page<TgBetBot>> page(TgBetBotPageVO vo) {
        Page<TgBetBot> pageList = tgBetBotBusiness.getTgBetBotPage(vo);
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
        // 更新BotSession
        tgBetBotBusiness.updateBotSession(id, status);
        // 更新状态
        tgBetBotService.updateStatusById(id, status);
        return ResponseUtil.success();
    }

    @ApiOperation("删除机器人")
    @PostMapping("delBot")
    public ResponseEntity delBot(Long id) {
        // 参数校验
        if (CommonUtil.checkNull(id.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        TgBetBot tgBetBot = tgBetBotService.findById(id);
        // 停止机器人
        BotSession botSession = tgBetBotBusiness.getBotSession(tgBetBot.getBetBotName());
        if (botSession != null && botSession.isRunning()) {
            botSession.stop();
        }
        // 删除MAP
        tgBetBotBusiness.botSerssionMap.remove(tgBetBot.getBetBotName());
        // 删除机器人
        tgBetBotService.delBot(id);
        return ResponseUtil.success();
    }
}
