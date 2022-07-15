package com.baisha.gameserver.util;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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


    public static void main(String[] args) {
        String yiAwardOption = "Z,X,SS,XD,ZD";
        String[] yiAwardOptionArr = yiAwardOption.split(",");
        Arrays.sort(yiAwardOptionArr);
        String yiJsonStr = JSONObject.toJSONString(yiAwardOptionArr);
        System.out.println(yiJsonStr);

        String awardOption = "X,ZD,XD,Z,SS";
        String[] awardOptionArr = awardOption.split(",");
        Arrays.sort(awardOptionArr);
        String awardJsonStr = JSONObject.toJSONString(awardOptionArr);
        System.out.println(awardJsonStr);

        System.out.println(yiJsonStr.equals(awardJsonStr));

    }


}
