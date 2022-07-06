package com.baisha.gameserver.service;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.baisha.gameserver.model.BetStatistics;
import com.baisha.gameserver.repository.BetStatisticsRepository;
import com.baisha.modulecommon.util.DateUtil;


/**
 * @author: alvin
 */
@Service
@Transactional
@CacheConfig(cacheNames = "game::BetStatistics")
public class BetStatisticsService {


    @Autowired
    BetStatisticsRepository betStatisticsRepository;

    @Caching(put = {@CachePut(key = "#entity.userId +'_' +#entity.statisticsDate")})
    public BetStatistics save(BetStatistics entity) {
        return betStatisticsRepository.save(entity);
    }

    public BetStatistics findByUserIdAndStatisticsDate(Long userId) {
        String dateStr = DateUtil.today(DateUtil.YYYYMMDD);
        return findByUserIdAndStatisticsDate(userId, Integer.parseInt(dateStr));
    }

    @Cacheable(key = "#userId +'_' +#statisticsDate", unless = "#result == null")
    public BetStatistics findByUserIdAndStatisticsDate(Long userId, Integer statisticsDate) {
        return betStatisticsRepository.findByUserIdAndStatisticsDate(userId, statisticsDate);
    }

    @Caching(put = {@CachePut(key = "#userId +'_' +#statisticsDate")})
    public BetStatistics updateFlowAmount(Long userId, Integer statisticsDate, BigDecimal flowAmount) {
        int i = betStatisticsRepository.updateFlowAmount(userId, statisticsDate, flowAmount);
        if (i > 0) {
            return findByUserIdAndStatisticsDateSql(userId, statisticsDate);
        }
        return null;
    }

    @CachePut(key = "#userId +'_' +#statisticsDate")
    public BetStatistics statisticsWinAmount(Integer statisticsDate, Long userId, BigDecimal winAmount) {
        int i = betStatisticsRepository.updateWinAmount(userId, statisticsDate, winAmount);
        if (i > 0) {
            return findByUserIdAndStatisticsDateSql(userId, statisticsDate);
        }
        return null;
    }

    private BetStatistics findByUserIdAndStatisticsDateSql(Long userId, Integer statisticsDate) {
        return betStatisticsRepository.findByUserIdAndStatisticsDate(userId, statisticsDate);
    }
}
