package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.SnowFlakeUtils;
import com.baisha.userserver.model.SsOrder;
import com.baisha.userserver.model.vo.IdVO;
import com.baisha.userserver.model.vo.order.SsOrderAddVO;
import com.baisha.userserver.service.SsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "订单管理")
@RequestMapping("order")
public class SsOrderController {

    @Autowired
    private SsOrderService ssOrderService;

    @PostMapping(value = "save")
    @ApiOperation(value = "新增订单")
    public ResponseEntity<String> save(SsOrderAddVO vo) {
        if (null == vo.getOrderStatus() || null == vo.getOrderType() || null == vo.getUserId() || null == vo.getAmount()) {
            return ResponseUtil.parameterNotNull();
        }
        SsOrder ssOrder = new SsOrder();
        BeanUtils.copyProperties(vo, ssOrder);
        ssOrder.setOrderNum(SnowFlakeUtils.getSnowId());
        ssOrderService.save(ssOrder);
        return ResponseUtil.success(String.valueOf(ssOrder.getId()));
    }

    @PostMapping(value = "deleteById")
    @ApiOperation(value = "删除订单")
    public ResponseEntity<String> save(IdVO vo) {
        if (null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        ssOrderService.delete(vo.getId());
        return ResponseUtil.success(String.valueOf(vo.getId()));
    }


}
