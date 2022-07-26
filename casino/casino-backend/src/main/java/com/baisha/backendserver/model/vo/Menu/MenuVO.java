package com.baisha.backendserver.model.vo.Menu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel(value = "后台-新增功能請求對象")
public class MenuVO {

    @ApiModelProperty(value = "代碼 長度10內", required = true)
    private String code;

    @ApiModelProperty(value = "菜單或功能名稱", required = true)
    private String name;

    @ApiModelProperty(value = "上層節點id 無上層節點則傳入 0", required = true)
    private Long parentId;

    @ApiModelProperty(value = "類型  1 一級菜單  2 二級菜單 3 功能按鈕", required = true)
    private Integer type;

    @ApiModelProperty(value = "功能對應的 api url")
    private Integer apiUrl;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用", required = true)
    private Integer status;
}
