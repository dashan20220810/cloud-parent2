package com.baisha.modulecommon;

/**
 * mq 变量
 */
public class MqConstants {

    /** 下注结算 */
    public final static String BET_SETTLEMENT = "gs_bet_settlement";

    /*
     * web端
     */
    /** 开新局 */
    public final static String WEB_OPEN_NEW_GAME = "web_open_new_game";
    /** 结束 */
    public final static String WEB_CLOSE_GAME = "web_close_game";
    /** 结算完成通知 */
    public final static String SETTLEMENT_FINISH = "web_settlement_game";
    /** 结算完成通知用户端余额和打码量变化 */
    public final static String USER_SETTLEMENT_ASSETS = "user_settlement_assets";

    /** 下注後用戶總計 */
    public final static String USER_BET_STATISTICS = "user_bet_statistics";
    /** 结算后告诉admin统计注单信息 */
    public final static String BACKEND_BET_SETTLEMENT_STATISTICS = "backend_bet_settlement_statistics";
    /** 荷官端截屏通知 */
    public final static String WEB_PAIR_IMAGE = "web_pair_image";




    //=========================backend告诉其他服务=======================================================
    /** admin告诉gs补单 结算 */
    public final static String GS_REPAIR_BET_RESULT = "gs_repair_bet_result";
    /** admin告诉gs重开 结算 */
    public final static String GS_REOPEN_BET_RESULT = "gs_reopen_bet_result";



    /** 重新开牌-结算 告诉user扣除之前的金额 */
    public static final String USER_SUBTRACT_ASSETS = "user_subtract_assets";

    /** 重新开牌-结算 告诉user加回之前的打码量 */
    public static final String USER_ADD_PLAYMONEY_ASSETS = "user_add_playMoney_assets";


}
