package com.baisha.userserver.util.constants;

public class RedisConstants {


    /**
     * 等待时间 秒
     */
    public static int WAIT_TIME = 10;

    /**
     * 自动解锁 秒
     */
    public static int UNLOCK_TIME = 5;


    /**
     * 资产锁
     */
    public static String USER_ASSETS = "userServer::assets::";

    //加减余额同步key
    public static String BALANCE = "userServer::balance::";
    //加减打码量同步key
    public static String PLAYMONEY = "userServer::playMoney::";


}
