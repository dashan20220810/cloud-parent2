package com.baisha.gameserver.util.contants;

public class RedisConstants {

    /**
     * 结算  等待时间 秒
     */
    public static int SETTLEMENT_WAIT_TIME = 0;

    /**
     * 结算  自动解锁 秒
     */
    public static int SETTLEMENT_UNLOCK_TIME = 30;

    /**
     * 结算  将当前局锁住
     */
    public static String GAMESERVER_SETTLEMENT = "gameserver::settlement::NoActive::";


}
