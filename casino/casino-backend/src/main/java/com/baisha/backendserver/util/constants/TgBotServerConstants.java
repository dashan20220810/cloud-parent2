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

    /**
     * 机器人与TG群关系删除
     */
    public static final String GROUP_DELETEBYID = "/tgChat/deleteById";

    /**
     * 投注机器人 - 新增
     */
    public static final String TGBETBOT_ADDBETBOT = "/tgBetBot/addBetBot";

    /**
     * 投注机器人 - 删除
     */
    public static final String TGBETBOT_DELBOT = "/tgBetBot/delBot";

    /**
     * 分页查询 - 投注机器人
     */
    public static final String TGBETBOT_PAGE = "/tgBetBot/page";

    /**
     * 投注机器人 -状态
     */
    public static final String TGBETBOT_UPDATESTATUSBYID = "/tgBetBot/updateStatusById";

    /**
     * 投注机器人 -点击绑定-TG群下的投注机器人
     */
    public static final String TGBETBOT_FINDRELATIONBYTGCHATID = "/tgChat/findRelationByTgChatId";

    /**
     * 投注机器人 -点击绑定-TG群下的投注机器人
     */
    public static final String TGBETBOT_CONFIRMBIND = "/tgChat/confirmBind";


}
