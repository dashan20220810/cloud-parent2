package com.baisha.controller;

import com.baisha.model.TgChat;
import com.baisha.model.vo.AuditVo;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tgChat")
@Api(tags = "tg群组管理")
public class TgChatController {

    @Autowired
    TgChatService tgChatService;

    @ApiOperation("分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小(默认10条)", required = false),
            @ApiImplicitParam(name = "pageCode", value = "分页页码(默认1页)", required = false),
            @ApiImplicitParam(name = "botId", value = "机器人ID", required = false),

    })
    @GetMapping("page")
    public ResponseEntity<Page<TgChat>> page(Integer pageSize, Integer pageCode,Long botId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = TelegramBotUtil.setPageable(pageCode, pageSize, sort);

        TgChat tgChat = new TgChat();
        tgChat.setBotId(botId);

        Page<TgChat> page = tgChatService.pageByCondition(pageable, tgChat);
        return ResponseUtil.success(page);

    }

    @ApiOperation("群审计功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chatId", value = "群id", required = true),
            @ApiImplicitParam(name = "state", value = "群状态：1.通过。 0或其他. 不通过", required = true),
            @ApiImplicitParam(name = "tableId", value = "绑定游戏桌台的id", required = true),
            @ApiImplicitParam(name = "minAmount", value = "单注最小限红", required = true),
            @ApiImplicitParam(name = "maxAmount", value = "单注最大限红", required = true),
            @ApiImplicitParam(name = "maxShoeAmount", value = "局最大限红", required = true),
    })
    @PostMapping("audit")
    public ResponseEntity audit(AuditVo vo) throws IllegalAccessException {
        if (!AuditVo.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        TgChat tgChat = tgChatService.findById(vo.getChatId());
        if (tgChat == null || tgChat.getId() == null) {
            return ResponseUtil.custom("未找到群");
        }
        tgChat.setTableId(vo.getTableId());

        if (Constants.open.equals(vo.getState())) {
            tgChat.setStatus(Constants.open);
        }else {
            tgChat.setStatus(Constants.close);
        }

        tgChat.setMinAmount(vo.getMinAmount());
        tgChat.setMaxAmount(vo.getMaxAmount());
        tgChat.setMaxShoeAmount(vo.getMaxShoeAmount());
        tgChatService.save(tgChat);
        return ResponseUtil.success();
    }

    @ApiOperation("删除群组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chatId", value = "群组id", required = true),
    })
    @PostMapping("deleteById")
    public ResponseEntity deleteById(Long chatId) {
        tgChatService.deleteById(chatId);
        return ResponseUtil.success();
    }

    @ApiOperation("根据桌子查询多个群")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableId", value = "桌子id", required = true),
    })
    @GetMapping("findByTableId")
    public ResponseEntity findByTableId(Long tableId) {
        return ResponseUtil.success(tgChatService.findByTableId(tableId));
    }
}
