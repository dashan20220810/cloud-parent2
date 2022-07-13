package com.baisha.casinoweb.model.vo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

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

    @ApiModelProperty(required=true, value="单注最低")
    private Integer minAmount;

    @ApiModelProperty(required=true, value="单注最高")
    private Integer maxAmount;

//    @ApiModelProperty(required=true, value="当局最高")
//    private Integer maxShoeAmount;

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
    
    public List<String> getBetOptionList () {
    	switch (betOption) {
			case D: // XD,ZD
    			return Arrays.asList(new String[]{BetOption.XD.toString(), BetOption.ZD.toString()});
    		case SB: // XD,ZD,H
    			return Arrays.asList(new String[]{BetOption.XD.toString(), BetOption.ZD.toString(), BetOption.H.toString()});
    		default:
    			return Arrays.asList(new String[]{betOption.toString()});
    	}
    }
    
    public Long getTotalAmount () {
    	switch (betOption) {
		case D: // XD,ZD
			return amount * 2;
		case SB: // XD,ZD,H
			return amount * 3;
		default:
			return amount;
	}
    }
}
