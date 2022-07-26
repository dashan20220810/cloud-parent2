package com.baisha.backendserver.model.bo.Menu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "后台-功能菜單返回對象")
public class MenuBO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "菜单编码")
    private String code;

    @ApiModelProperty(value = "上層節點id")
    private Long parentId;

    @ApiModelProperty(value = "节点类型，1文件夹，2页面，3按钮")
    private Integer type;

    @ApiModelProperty(value = "页面对应的地址")
    private String apiUrl;

    @ApiModelProperty(value = "层次")
    private Integer level;

    @ApiModelProperty(value = "树id的路径")
    private String path;

    @ApiModelProperty(value = "子菜单集合")
    List<MenuBO> childMenu;
}
