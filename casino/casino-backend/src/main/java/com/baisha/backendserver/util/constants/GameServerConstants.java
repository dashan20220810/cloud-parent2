package com.baisha.backendserver.util.constants;

public class GameServerConstants {

    /**
     * 订单查询
     */
    public static final String ORDER_PAGE = "/order/page";

    /**
     * 桌台列表
     */
    public static final String DESK_LIST = "/desk/all";

    /**
     * 桌台分页列表
     */
    public static final String DESK_PAGE = "/desk/page";

    /**
     * 桌台新增
     */
    public static final String DESK_ADD = "/desk/add";

    /**
     * 桌台删除
     */
    public static final String DESK_DELETE = "/desk/delete";

    /**
     * 桌台删除
     */
    public static final String DESK_UPDATE = "/desk/update";

    /**
     * 桌台状态更新
     */
    public static final String DESK_UPDATESTATUS = "/desk/updateStatus";

    /**
     * 桌台游戏编码
     */
    public static final String GAME_CODE_LIST = "/game/gameCode";

    /**
     * 获取游戏倍率限制列表
     */
    public static final String GAME_ODDS_LIST = "/game/global/oddsList";

    /**
     * 桌台游戏百家乐赔率
     */
    public static final String GAME_SET_BACC_ODDS = "/game/global/bacc/odds";


    /**
     * 桌台游戏百家乐开奖分页列表
     */
    public static final String GAME_BET_RESULT_PAGE = "/betResult/page";

    /**
     * 桌台游戏百家乐开奖单查询
     */
    public static final String GAME_BET_RESULT_NOACTIVE = "/betResult/queryByNoActive";

    /**
     * 获取返水信息
     */
    public static final String GAME_GET_REBATE_INFO = "/prop/queryReturnAmountMultiplier";

    /**
     * 设置返水信息
     */
    public static final String GAME_SET_REBATE_INFO = "/prop/updateReturnAmountMultiplier";


}
