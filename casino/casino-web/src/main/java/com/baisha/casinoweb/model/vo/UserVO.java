package com.baisha.casinoweb.model.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

@Data
@ApiModel(value = "使用者对象")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 9213588127764300550L;

	@ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status;
    
    @Getter
    public enum Status {
    	
    	DISABLE(0),
    	ENABLE(1),
    	;
    	
    	private Integer code;
    	Status(Integer code) {
    		this.code = code;
    	}
    }
}
