package com.baisha.casinoweb.model.vo;

import java.io.Serializable;

import com.baisha.modulecommon.enums.BetOption;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@Data
@Slf4j
@ApiModel(value = "web-下注请求对象")
public class BetVO implements Serializable {

	private static final long serialVersionUID = -8447293147608212930L;

    @ApiModelProperty(required=true, value="桌台id")
	private Long tableId;

    @ApiModelProperty(required=true, value="下注类型", notes="ZD庄对,XD闲对,Z庄,X闲,H和,D对,SS超六")
	private BetOption betOption;

    @ApiModelProperty(required=true, value="下注金额")
	private Long amount;

    @ApiModelProperty(value="telegram chat id")
    private Long tgChatId;
	
    /**
     * 检核下注请求
     * @param bet
     * @return
     */
    public static boolean checkRequest ( BetVO bet ) {
    	
    	if ( bet.getTableId()==null ) {
    		log.warn(" tableId required!! ");
    		return false;
    	}

    	if ( bet.getBetOption() == null ) {
    		log.warn(" betOption required!! ");
    		return false;
    	}

    	if ( bet.getAmount()==null || bet.getAmount()<=0L ) {
    		log.warn(" Amount required!! ");
    		return false;
    	}

    	return true;
    }
}
