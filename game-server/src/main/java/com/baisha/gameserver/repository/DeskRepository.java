package com.baisha.gameserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.Desk;

public interface DeskRepository extends JpaRepository<Desk, Long>, JpaSpecificationExecutor<Desk> {

    @Query(value = " select t from Desk t where t.status=1 order by t.createTime desc ")
    List<Desk> findAllDeskList();

    Desk findByLocalIp ( String localIp );

    Desk findByDeskCode ( String deskCode );

}
