package com.baisha.gameserver.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


    /**
     * 按指定大小，分隔集合，将集合按规定个数分为n个部分
     *
     * @param list
     * @param len
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.isEmpty() || len < 1) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }


}
