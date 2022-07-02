package com.baisha.core.constants;

public class RedisKeyConstants {

    //redis 客服设置前缀
    public static final String SYS_TELEGRAM = "common::sys_telegram";

    //redis 群限红设置前缀
    public static final String GROUP_TELEGRAM_BOUND = "common::group_telegram_bound";

    //redis 游戏资讯前缀
    public static final String SYS_GAME_INFO = "game::game_info";

    //redis 游戏开盘结果前缀
    public static final String SYS_GAME_RESULT = "game::game_result";

    //redis 游戏局号查桌台编码
    public static final String SYS_GAME_DESK = "game::game_desk";
    
    /** 游戏局号 */
    public static final String GAMBLING_ACTIVE_INFO = "gambling::active_info";

}
