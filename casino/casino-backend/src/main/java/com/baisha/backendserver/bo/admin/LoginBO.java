package com.baisha.backendserver.bo.admin;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@Builder
@ApiModel(value = "后台-登陆返回对象")
public class LoginBO {
    private Long id;
    private String userName;
    private String nickName;
    private String token;
}
