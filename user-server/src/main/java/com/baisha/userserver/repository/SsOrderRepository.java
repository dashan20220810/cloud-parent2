package com.baisha.userserver.repository;

import com.baisha.userserver.model.SsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yihui
 */
public interface SsOrderRepository extends JpaRepository<SsOrder, Long>, JpaSpecificationExecutor<SsOrder> {


}
