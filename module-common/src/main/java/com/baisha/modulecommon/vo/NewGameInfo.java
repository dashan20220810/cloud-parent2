package com.baisha.modulecommon.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 小智
 * @Date 11/7/22 6:58 PM
 * @Version 1.0
 */
@Data
public class NewGameInfo implements Serializable {

    private static final long serialVersionUID = -6507842020867843825L;

    /** 当前桌号 */
    private String deskCode;

    /** 游戏局号 */
    private String noActive;

    /** 游戏开始时间 */
    private Date beginTime;

    /** 游戏结束时间 */
    private Date endTime;

    /** 游戏录单图地址 */
    private String picAddress;
}
