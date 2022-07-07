package com.baisha.casinoweb.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.business.DealerBusiness;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.OpenNewGameVO;
import com.baisha.modulecommon.vo.mq.OpenVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;

@Component
public class DirectReceiver {
	
	@Autowired
	private DealerBusiness dealerBusiness;

    @RabbitListener(queues = "hello-queue")
    public void handler1(String msg) {
        System.out.println("==============" + msg);
    }

    @RabbitListener(queues = MqConstants.WEB_OPEN_NEW_GAME)
    public void openNewGame(String jsonStr) {
        System.out.println("==============" + jsonStr);
        
        OpenNewGameVO vo = JSONObject.parseObject(jsonStr, OpenNewGameVO.class);
        //TODO
        dealerBusiness.openNewGame(vo.getDealerIp(), vo.getGameNo());
    }

    @RabbitListener(queues = MqConstants.WEB_CLOSE_GAME)
    public void open(OpenVO vo) {
        System.out.println("==============" + JSONObject.toJSONString(vo));
        dealerBusiness.open(vo.getDealerIp(), vo.getAwardOption());
    }

    /**
     * @param vo
     */
    @RabbitListener(queues = MqConstants.SETTLEMENT_FINISH)
    public void settleFinish(SettleFinishVO vo) {
        System.out.println("==============" + JSONObject.toJSONString(vo));
        dealerBusiness.settlement(vo.getNoActive());
    }
    

}
