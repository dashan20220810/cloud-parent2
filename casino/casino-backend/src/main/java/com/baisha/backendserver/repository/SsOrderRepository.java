package com.baisha.backendserver.repository;

import com.baisha.backendserver.model.SsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yihui
 */
public interface SsOrderRepository extends JpaRepository<SsOrder, Long>, JpaSpecificationExecutor<SsOrder> {


}
