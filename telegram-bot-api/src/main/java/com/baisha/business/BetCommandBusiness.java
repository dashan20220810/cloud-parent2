package com.baisha.business;

import com.baisha.model.TgChat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BetCommandBusiness {

    @Autowired
    private CommandBusiness commandBusiness;

    @Async
    public void botStartBet(List<TgChat> chatList) {
        chatList.forEach(chat -> commandBusiness.botStartBet(chat));
    }
}
