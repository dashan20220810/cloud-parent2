package com.baisha.controller;

import com.baisha.business.ControlBotBusiness;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.generics.BotSession;

@Api(tags = "机器人管理")
@Slf4j
@RestController
@RequestMapping("tgBot")
public class TgManageBotController {

    @Autowired
    private ControlBotBusiness controlBotBusiness;

    @Autowired
    private TgBotBusiness tgBotBusiness;

    @Autowired
    private TgBotService tgBotService;

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
        boolean isSuccess = controlBotBusiness.startupBot(username, token);
        if (!isSuccess) {
            return ResponseUtil.custom("机器人启动失败，请联系技术处理");
        }

        //启动机器人成功更新机器人资料
        TgBot tgBot = tgBotService.findByBotName(username);
        if (ObjectUtils.isEmpty(tgBot) || StringUtils.isEmpty(tgBot.getBotName())) {
            //新增
            tgBot = new TgBot();
            tgBot.setBotName(username)
                    .setBotToken(token);
        }
        tgBot.setStatus(Constants.open);
        tgBotService.save(tgBot);

        return ResponseUtil.success();
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
        // 更新BotSession
        tgBotBusiness.updateBotSession(id, status);
        // 更新状态
        tgBotService.updateStatusById(id, status);
        return ResponseUtil.success();
    }

    @ApiOperation("删除机器人")
    @PostMapping("delBot")
    public ResponseEntity delBot(Long id) {
        // 参数校验
        if (CommonUtil.checkNull(id.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        TgBot tgBot = tgBotService.findById(id);
        // 停止机器人
        BotSession botSession = controlBotBusiness.getBotSession(tgBot.getBotName());
        if (botSession != null && botSession.isRunning()) {
            botSession.stop();
        }
        // 删除MAP
        controlBotBusiness.botSessionMap.remove(tgBot.getBotName());



        // 删除机器人
        tgBotService.delBot(id);
        return ResponseUtil.success();
    }
}
