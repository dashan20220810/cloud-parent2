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

    @Caching(put = {@CachePut(key = "#entity.userId +'_' #entity.tgChatId +'_' +#entity.statisticsDate")})
    public BetStatistics save(BetStatistics entity) {
        return betStatisticsRepository.save(entity);
    }

    public BetStatistics findByUserIdAndTgChatIdAndStatisticsDate(Long userId, Long tgChatId) {
        String dateStr = DateUtil.today(DateUtil.YYYYMMDD);
        return findByUserIdAndTgChatIdAndStatisticsDate(userId, tgChatId, Integer.parseInt(dateStr));
    }

    @Cacheable(key = "#userId +'_' #tgChatId +'_'+#statisticsDate", unless = "#result == null")
    public BetStatistics findByUserIdAndTgChatIdAndStatisticsDate(Long userId, Long tgChatId, Integer statisticsDate) {
        return betStatisticsRepository.findByUserIdAndTgChatIdAndStatisticsDate(userId, tgChatId, statisticsDate);
    }

    @Caching(put = {@CachePut(key = "#userId +'_' #tgChatId +'_' +#statisticsDate")})
    public BetStatistics updateFlowAmount(Long userId, Long tgChatId, Integer statisticsDate, BigDecimal flowAmount) {
        int i = betStatisticsRepository.updateFlowAmount(userId, tgChatId, statisticsDate, flowAmount);
        if (i > 0) {
            return betStatisticsRepository.findByUserIdAndTgChatIdAndStatisticsDate(userId, tgChatId, statisticsDate);
        }
        return null;
    }

    @CachePut(key = "#userId +'_' #tgChatId +'_' +#statisticsDate")
    public BetStatistics statisticsWinAmount(Integer statisticsDate, Long userId, Long tgChatId, BigDecimal winAmount) {
        int i = betStatisticsRepository.updateWinAmount(userId, tgChatId, statisticsDate, winAmount);
        if (i > 0) {
            return betStatisticsRepository.findByUserIdAndTgChatIdAndStatisticsDate(userId, tgChatId, statisticsDate);
        }
        return null;
    }

}
