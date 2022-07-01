package com.baisha.gameserver.util;

import com.baisha.gameserver.enums.BetOddsEnum;

/**
 * @author yihui
 */
public class GameServerUtil {

    /**
     * 是否中奖
     *
     * @param betOption   下注玩法
     * @param awardOption 中奖玩法
     * @return
     */
    public static boolean isWin(String betOption, String awardOption) {
        if (betOption.equals(awardOption)) {
            return true;
        }
        return false;
    }


}
