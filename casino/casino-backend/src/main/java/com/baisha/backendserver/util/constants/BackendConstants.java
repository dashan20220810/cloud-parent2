package com.baisha.backendserver.util.constants;

/**
 * @author yihui
 */
public class BackendConstants {

    public static final String UTF8 = "UTF-8";

    //收入
    public static int INCOME = 1;

    //支出
    public static int EXPENSES = 2;


    //订单类型
    //充值
    public final static Integer CHARGEORDER = 1;
    //提现
    public final static Integer WITHDRAWORDER = 2;

    //订单状态
    //等待确认
    public final static Integer ORDER_WAIT = 1;
    //成功
    public final static Integer ORDER_SUCCESS = 2;
    //失败
    public final static Integer ORDER_FAIL = 3;
    //取消
    public final static Integer ORDER_CANCLE = 4;


    public static final String INSERT = "insert";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";

    public static final String USER_MODULE = "会员";
    public static final String USER_ASSETS_MODULE = "会员资产";
    public static final String TOBOT_MODULE = "机器人";
    public static final String TOBOT_GROUP_MODULE = "机器人-群";
    public static final String ADMIN_MODULE = "管理员";
    public static final String SYS_TELEGRAM_MODULE = "系统设置-电报";
    public static final String DESK_MODULE = "桌台";

}
