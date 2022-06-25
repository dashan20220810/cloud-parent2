package com.baisha.controller;

import com.baisha.business.TgChatBusiness;
import com.baisha.model.TgChat;
import com.baisha.model.vo.StatusVO;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.service.TgChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "机器人和TG群的关系审核")
@Slf4j
@RestController
@RequestMapping("audit")
public class TgbotAuditController {

    @Autowired
    private TgChatBusiness tgChatBusiness;

    @Autowired
    private TgChatService TgChatService;

    @ApiOperation("分页查询")
    @PostMapping("page")
    public ResponseEntity<Page<TgChat>> page(TgBotPageVO vo) {
        Page<TgChat> pageList = tgChatBusiness.getTgChatPage(vo);
        return ResponseUtil.success(pageList);
    }

    @ApiOperation("审核")
    @PostMapping("auditStatus")
    public ResponseEntity auditStatus(StatusVO statusVO) {
        Long id = statusVO.getId();
        Integer status = statusVO.getStatus();
        // 参数校验
        if (CommonUtil.checkNull(id.toString(), status.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        // 更新状态
        TgChatService.auditStatus(id, status);
        return ResponseUtil.success();
    }
}
