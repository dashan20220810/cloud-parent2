package com.baisha.modulecommon.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author 小智
 * @Date 16/7/22 4:09 PM
 * @Version 1.0
 */
@Data
public class HttpResultDTO implements Serializable {

    private static final long serialVersionUID = -6978288230885516177L;

    private int status;

    private String body;

    private Map<String, String> headerMap;

    public HttpResultDTO(int status, String body, Map<String, String> headerMap) {
        this.status = status;
        this.body = body;
        this.headerMap = headerMap;
    }

    public HttpResultDTO() {

    }
}
