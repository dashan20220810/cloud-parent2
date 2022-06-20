package com.baisha.backendserver.constants;

/**
 * @author yihui
 */
public class BackendServerConstants {

    /**
     * 项目名
     */
    public static String CASINO_BACKEND = "CASINO_BACKEND";

    /**
     * 状态 1 正常
     */
    public static int STATUS_NORMAL = 1;

    /**
     * 状态 2 禁用
     */
    public static int STATUS_DISABLED = 2;


    /**
     * 是否删除 0 未删除
     */
    public static int DELETE_NORMAL = 0;

    /**
     * 是否删除 1 删除
     */
    public static int DELETE_DISABLED = 1;

    /**
     * token 有效期
     */
    public static Long WEB_REFRESH_TTL = Long.valueOf(60 * 60);

    /**
     * id token
     */
    public static String TOKEN_CASINO_BACKEND = "admin:user:";


}
