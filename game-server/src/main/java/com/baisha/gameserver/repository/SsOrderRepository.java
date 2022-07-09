package com.baisha.gameserver.repository;

import com.baisha.gameserver.model.SsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yihui
 */
public interface SsOrderRepository extends JpaRepository<SsOrder, Long>, JpaSpecificationExecutor<SsOrder> {


}
