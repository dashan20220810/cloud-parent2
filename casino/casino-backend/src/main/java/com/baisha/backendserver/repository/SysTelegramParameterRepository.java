package com.baisha.backendserver.repository;

import com.baisha.backendserver.model.SysTelegramParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yihui
 */
public interface SysTelegramParameterRepository extends JpaRepository<SysTelegramParameter, Long>,
        JpaSpecificationExecutor<SysTelegramParameter> {


}
