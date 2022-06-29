package com.baisha.modulecommon.vo;

import java.io.Serializable;
import java.util.Date;

import com.baisha.modulecommon.enums.GameStatusEnum;

import lombok.Data;

@Data
public class GameInfo implements Serializable {

	/** 当前局号 */
	private String currentActive;
	
	/** 前局游戏资讯 */ // 设置前请先清空当前局的前一局
//	private GameInfo lastGameInfo;
	
	private GameStatusEnum status;
	
	private Date beginTime;
	
}
