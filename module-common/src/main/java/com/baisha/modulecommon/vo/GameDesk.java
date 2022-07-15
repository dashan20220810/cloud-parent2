package com.baisha.modulecommon.vo;

import com.baisha.modulecommon.enums.GameStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDesk implements Serializable {


	private static final long serialVersionUID = -443229114330812773L;
	/** 当前局号 */
	private String currentActive;

	/** 当前游戏桌台code */
	private String deskCode;

	/** 桌子id */
	private Long deskId;

	/** 视频流截取code */
	private String streamVideoCode;

}
