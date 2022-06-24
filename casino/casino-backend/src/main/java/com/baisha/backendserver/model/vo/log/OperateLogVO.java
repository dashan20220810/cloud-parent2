package com.baisha.backendserver.model.vo.log;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperateLogVO {

    private String moduleName;

    private String activeType;

    private String content;
}
