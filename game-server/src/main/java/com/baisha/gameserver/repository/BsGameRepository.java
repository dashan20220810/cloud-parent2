package com.baisha.gameserver.repository;

import com.baisha.gameserver.model.BsGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BsGameRepository extends JpaRepository<BsGame, Long> {


}
