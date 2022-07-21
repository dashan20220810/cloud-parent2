package com.baisha.backendserver.util.constants;

/**
 * @author yh
 */
public class UserServerConstants {

    //**************************用户中心***************************************

    /**
     * 用户新增
     */
    public static final String USERSERVER_USER_SAVE = "/user/save";

    /**
     * 用户分页接口
     */
    public static final String USERSERVER_USER_PAGE = "/user/page";

    /**
     * 设置用户为机器人
     */
    public static final String USERSERVER_USER_TYPE = "/user/userType";

    /**
     * 用户删除
     */
    public static final String USERSERVER_USER_DELETE = "/user/delete";
    /**
     * 用户启用禁用
     */
    public static final String USERSERVER_USER_STATUS = "/user/status";

    /**
     * 用户查询余额
     */
    public static final String USERSERVER_ASSETS_QUERY = "/assets/query";

    /**
     * 用户资产
     */
    public static final String USERSERVER_ASSETS_BYID = "/assets/findAssetsById";

    /**
     * 用户资产
     */
    public static final String USERSERVER_ASSETS_BYTGUSERID = "/assets/findAssetsByTgUserId";

    /**
     * 订单新增
     */
    public static final String USER_ORDER_ADD = "/order/save";


    /**
     * 订单删除
     */
    public static final String USER_ORDER_DELETE = "/order/deleteById";

    /**
     * 用户增加/减少余额
     */
    public static final String USERSERVER_ASSETS_BALANCE = "/assets/balance";

    /**
     * 用户增加/减少打码量
     */
    public static final String USERSERVER_ASSETS_PLAY_MONEY = "/assets/playMoney";


    /**
     * 用户余额变动分页接口
     */
    public static final String USERSERVER_ASSETS_CHANGE_BALANCE_PAGE = "/assets/changeBalancePage";


    /**
     * 用户打码量变动分页接口
     */
    public static final String USERSERVER_ASSETS_CHANGE_PLAYMONEY_PAGE = "/assets/changePlayMoneyPage";

    //**************************用户中心***************************************


}
