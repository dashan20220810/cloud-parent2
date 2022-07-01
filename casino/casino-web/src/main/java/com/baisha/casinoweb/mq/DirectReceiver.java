package com.baisha.casinoweb.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DirectReceiver {

    @RabbitListener(queues = "hello-queue")
    public void handler1(String msg) {
        System.out.println("==============" + msg);
    }

    @RabbitListener(queues = MqConstants.SETTLEMENT_FINISH)
    public void settleFinish(SettleFinishVO vo) {
        System.out.println("==============" + JSONObject.toJSONString(vo));
    }

}
