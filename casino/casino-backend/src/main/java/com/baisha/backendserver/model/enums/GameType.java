package com.baisha.backendserver.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 游戏类型
 *
 * @author yihui
 */
public enum GameType {
    BACC("BACC", "百家乐"),
    //LONGHU("LONGHU", "龙虎"),
    ;

    private String code;
    private String name;

    GameType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static GameType nameOfCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        GameType[] types = GameType.values();
        for (GameType type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
