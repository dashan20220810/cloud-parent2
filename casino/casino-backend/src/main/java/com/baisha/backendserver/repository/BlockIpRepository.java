package com.baisha.backendserver.repository;

import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.BlockIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BlockIpRepository  extends JpaRepository<BlockIp, Long>, JpaSpecificationExecutor<BlockIp> {

    /**
     * 根据ip查询
     *
     * @param ip
     * @return
     */
    BlockIp findByIp(String ip);

    /**
     * 更新状态
     *
     * @param status
     * @param id
     * @return
     */
    @Query(value = "update BlockIp i set i.status = ?1 where i.id=?2")
    @Modifying
    int updateBlockIpStatusById(Integer status, Long id);

}
