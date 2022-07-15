package com.baisha.backendserver.repository;

import com.baisha.backendserver.model.BetResultChange;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BetResultChangeRepository extends JpaRepository<BetResultChange, Long>, JpaSpecificationExecutor<BetResultChange> {

    @Query(value = " Select b From BetResultChange b Where b.noActive=?1 Order by b.createTime DESC ")
    List<BetResultChange> findCurrentByNoActive(String noActive, Pageable pageable);
}
