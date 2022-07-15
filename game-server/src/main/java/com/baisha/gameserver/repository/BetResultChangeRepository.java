package com.baisha.gameserver.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.BetResultChange;


/**
 * @author: alvin
 */
public interface BetResultChangeRepository extends JpaRepository<BetResultChange, Long>, JpaSpecificationExecutor<BetResultChange> {

	@Query(value = " Select b From BetResultChange b Where b.noActive=?1 Order by b.createTime DESC ")
	List<BetResultChange> findCurrentByNoActive (String noActive, Pageable pageable) ;
}
