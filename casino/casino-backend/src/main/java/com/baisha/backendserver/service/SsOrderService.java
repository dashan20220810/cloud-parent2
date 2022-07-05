package com.baisha.backendserver.service;

import com.baisha.backendserver.model.SsOrder;
import com.baisha.backendserver.repository.SsOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SsOrderService {

    @Autowired
    private SsOrderRepository orderRepository;

    public SsOrder save(SsOrder order) {
        orderRepository.save(order);
        return order;
    }

    public int delete(Long id) {
        try {
            orderRepository.deleteById(id);
            return BigDecimal.ONE.intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.intValue();
    }


}
