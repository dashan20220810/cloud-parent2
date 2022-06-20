package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "机器人-分页公共对象")
public class PageVO {

    @ApiModelProperty(value = "当前页(默认第1页)", example = "1", required = true)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页条数(默认10条)", example = "10", required = true)
    private Integer pageSize;

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
