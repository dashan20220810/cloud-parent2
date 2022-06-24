package com.baisha.backendserver.vo.log;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

@Data
@Builder
public class OperateLogVO {

    private String moduleName;

    private String activeType;

    private String content;
}
