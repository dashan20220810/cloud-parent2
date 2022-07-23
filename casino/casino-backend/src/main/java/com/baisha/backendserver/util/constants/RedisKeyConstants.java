package com.baisha.backendserver.util.constants;

/**
 * @author yihui
 */
public class RedisKeyConstants {

    /**
     * 等待时间 秒
     */
    public static int WAIT_TIME_ZERO = 0;

    /**
     * 自动解锁 秒
     */
    public static int UNLOCK_TIME = 5;

    /**
     * 设置信息 电报设置
     */
    public static final String FILE_TELEGRAM = "file::telegram";

    /**
     * 防止多次点击
     */
    public static final String PREVENT_CLICKS = "prevent::clicks::";

    /**
     * 用户下注统计
     */
    public static final String USER_BET_STATISTICS = "backend::userBetStatistics::";


    /**
     * * 用户结算统计
     * * 输赢金额
     */
    public static final String USER_SETTLE_BET_STATISTICS = "backend::userSettleBetStatistics::";


}
