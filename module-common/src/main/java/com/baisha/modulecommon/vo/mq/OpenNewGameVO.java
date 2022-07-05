package com.baisha.modulecommon.vo.mq;

import java.io.Serializable;

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
}
