package com.baisha.modulecommon.vo.mq;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 开牌
 * @author alvin
 */
@Builder
@Data
public class OpenVO implements Serializable {
	
	private static final long serialVersionUID = -1466174766315387380L;

	/** 荷官端ip */
	private String dealerIp;
	
	/** 开牌结果 */
	private String consequences;

	/** 开牌时间 */
	private String endTime;

	/** 荷官端游戏局号 */
	private String gameNo;
	
}
