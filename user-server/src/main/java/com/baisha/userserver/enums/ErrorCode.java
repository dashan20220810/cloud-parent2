package com.baisha.userserver.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yihui
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    USERNAME_NOTNULLL(1101, "用户名必填"),
    NICKNAME_NOTNULLL(1102, "昵称必填");

    private int code;
    private String msg;
}
