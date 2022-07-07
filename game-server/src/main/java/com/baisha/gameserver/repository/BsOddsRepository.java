package com.baisha.gameserver.repository;

import com.baisha.gameserver.model.BsOdds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BsOddsRepository extends JpaRepository<BsOdds, Long> {

    /**
     * 根据gamecode查询
     *
     * @param gameCode
     * @return
     */
    List<BsOdds> findAllByGameCode(String gameCode);


}
