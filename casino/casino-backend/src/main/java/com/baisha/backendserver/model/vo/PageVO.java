package com.baisha.backendserver.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-分页公共对象")
public class PageVO {

    @ApiModelProperty(value = "当前页（默认第1页）", example = "1")
    private Integer pageNumber = 1;

    @ApiModelProperty(value = "每页条数(默认10条)", example = "10")
    private Integer pageSize = 10;

    public Integer getPageNumber() {
        if (null == pageNumber) {
            return 1;
        }
        return pageNumber;
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            return 10;
        }
        return pageSize;
    }
    
}
