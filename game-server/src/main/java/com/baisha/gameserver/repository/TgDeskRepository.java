package com.baisha.gameserver.repository;

import com.baisha.gameserver.model.TgDesk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TgDeskRepository extends JpaRepository<TgDesk, Long>, JpaSpecificationExecutor<TgDesk> {

    @Query(value = " select t.id ,t.deskCode from TgDesk t  order by t.createTime desc ")
    List<TgDesk> findAllDeskList();


}
