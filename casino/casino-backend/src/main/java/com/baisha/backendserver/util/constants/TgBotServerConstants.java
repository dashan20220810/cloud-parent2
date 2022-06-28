package com.baisha.backendserver.util.constants;

/**
 * @author kimi
 */
public class TgBotServerConstants {
    /**
     * 新开机器人
     */
    public static final String OPEN_TG_BOT = "/tgBot/open";
    /**
     * 分页查询
     */
    public static final String PAGE_TG_BOT = "/tgBot/page";
    /**
     * 更新状态
     */
    public static final String UPDATE_STATUS_TG_BOT = "/tgBot/updateStatusById";

    /**
     * 删除
     */
    public static final String DELETE_TG_BOT = "/tgBot/delBot";

    /**
     * 根据机器人名称查询 电报群
     */
    public static final String GET_GROUP = "/tgChat/page";

    /**
     * 机器人与TG群关系审核
     */
    public static final String GROUP_AUDIT = "/tgChat/audit";

}
