package com.baisha.modulecommon.vo.mq;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 开新局
 * @author alvin
 */

@Builder
@Data
public class OpenNewGameVO implements Serializable {
	
	private static final long serialVersionUID = 6359244205039129481L;
	
	private Integer gameNo;
	
	private String dealerIp;

	/** 牌局开始时间 */
	private Date startTime;

	/** 倒计时时间 */
	private Integer countDown;
}
