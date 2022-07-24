package com.baisha.controller;

import com.baisha.business.TgBetBotBusiness;
import com.baisha.model.TgBetBot;
import com.baisha.model.vo.StatusVO;
import com.baisha.model.vo.TgBetBotPageVO;
import com.baisha.model.vo.TgBetBotVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.service.TgBetBotService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "投注机器人管理")
@Slf4j
@RestController
@RequestMapping("tgBetBot")
public class TgBetBotController {

    @Autowired
    private TgBetBotBusiness tgBetBotBusiness;

    @Autowired
    private TgBetBotService tgBetBotService;

    @ApiOperation("新增/修改投注机器人")
    @PostMapping("addBetBot")
    public ResponseEntity addBetBot(TgBetBotVO vo) {
        if (null == vo.getId()) {
            // 新增
            TgBetBot tgBetBot1 = tgBetBotService.findByBetBotId(vo.getBetBotId());
            if (null != tgBetBot1) {
                return ResponseUtil.custom("机器人用户ID已存在");
            }
            TgBetBot tgBetBot2 = tgBetBotService.findByBetBotPhone(vo.getBetBotPhone());
            if (null != tgBetBot2) {
                return ResponseUtil.custom("手机号已存在");
            }
            TgBetBot tgBetBot3 = tgBetBotService.findByBetBotName(vo.getBetBotName());
            if (null != tgBetBot3) {
                return ResponseUtil.custom("机器人名称已存在");
            }
            TgBetBot tgBetBot = new TgBetBot();
            tgBetBot.setBetBotId(vo.getBetBotId())
                    .setBetBotName(vo.getBetBotName())
                    .setBetBotPhone(vo.getBetBotPhone())
                    .setBetStartTime(vo.getBetStartTime())
                    .setBetEndTime(vo.getBetEndTime())
                    .setBetFrequency(vo.getBetFrequency())
                    .setBetContents(vo.getBetContents())
                    .setMinMultiple(vo.getMinMultiple())
                    .setMaxMultiple(vo.getMaxMultiple())
                    .setStatus(Constants.open);
            tgBetBotService.save(tgBetBot);

            return ResponseUtil.success();
        }
        // 修改
        TgBetBot byId = tgBetBotService.findById(vo.getId());
        if (null == byId) {
            return ResponseUtil.custom("机器人不存在");
        }
//        TgBetBot tgBetBot4 = tgBetBotService.findByBetBotName(vo.getBetBotName());
//        if (null != tgBetBot4) {
//            return ResponseUtil.custom("机器人名称已存在");
//        }
        byId.setBetBotName(vo.getBetBotName())
            .setBetStartTime(vo.getBetStartTime())
            .setBetEndTime(vo.getBetEndTime())
            .setBetFrequency(vo.getBetFrequency())
            .setBetContents(vo.getBetContents())
            .setMinMultiple(vo.getMinMultiple())
            .setMaxMultiple(vo.getMaxMultiple());
        tgBetBotService.save(byId);

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
        // 删除机器人
        tgBetBotService.delBot(id);
        return ResponseUtil.success();
    }
}
