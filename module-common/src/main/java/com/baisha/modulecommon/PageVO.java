package com.baisha.modulecommon;

import lombok.Data;

/**
 * @author yihui
 */
@Data
public class PageVO {

    private Integer pageNumber;

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
