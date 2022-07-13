package com.baisha.gameserver.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.Desk;

public interface DeskRepository extends JpaRepository<Desk, Long>, JpaSpecificationExecutor<Desk> {

    @Query(value = " select t from Desk t where t.status=1 order by t.createTime desc ")
    List<Desk> findAllDeskList();

    Desk findByLocalIp ( String localIp );

    Desk findByDeskCode ( String deskCode );
    
    /**
     * 更新状态
     * @param status
     * @param id
     */
    @Query(value = "update Desk  d set d.status = ?1 where d.id=?2")
    @Modifying
    int updateStatusById(Integer status, Long id);

    @Query(value = "update Desk  d set d.localIp=?2, d.videoAddress=?3, d.nearVideoAddress=?4, d.gameCode=?5, d.status = ?6, d.name=?7, d.updateTime=?8 where d.id=?1")
    @Modifying
    int update(Long id, String localIp, String videoAddress, String nearVideoAddress, String gameCode, Integer status, String name, Date now);
}
