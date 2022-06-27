package com.baisha.gameserver.vo;

import com.baisha.modulecommon.PageVO;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(value = "web-桌台分页对象")
public class DeskPageVO extends PageVO {

}
