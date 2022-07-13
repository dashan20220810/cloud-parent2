package com.baisha.modulecommon.enums.user;


import java.util.Arrays;
import java.util.List;

public enum UserTypeEnum {

    NORMAL(1, "正式"),
    TEST(2, "测试"),
    BOT(3, "机器人"),

    ;

    private Integer code;
    private String name;

    UserTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static UserTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        UserTypeEnum[] types = UserTypeEnum.values();
        for (UserTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserTypeEnum> getList() {
        return Arrays.asList(values());
    }

}
