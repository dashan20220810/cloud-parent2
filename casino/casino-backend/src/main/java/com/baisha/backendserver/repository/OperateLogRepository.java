package com.baisha.backendserver.repository;

import com.baisha.backendserver.model.OperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OperateLogRepository extends JpaRepository<OperateLog, Long>, JpaSpecificationExecutor<OperateLog> {
}
