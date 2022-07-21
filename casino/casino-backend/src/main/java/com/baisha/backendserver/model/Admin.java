package com.baisha.backendserver.model;

import com.baisha.modulecommon.RegexEnum;
import com.baisha.modulecommon.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "admin", comment = "管理员")
@ApiModel(value = "后台-管理员对象")
public class Admin extends BaseEntity {

    @ApiModelProperty(value = "用户名")
    @Column(unique = true, columnDefinition = "varchar(30) comment '用户名'")
    private String userName;

    @ApiModelProperty(value = "昵称 账号持有人信息")
    @Column(columnDefinition = "varchar(30) comment '昵称 账号持有人信息'")
    private String nickName;

    @ApiModelProperty(value = "密码")
    @Column(columnDefinition = "varchar(64) comment '密码'")
    private String password;

    @ApiModelProperty(value = "手机号")
    @Column(columnDefinition = "varchar(20) comment '手机号'")
    private String phone;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0禁用'")
    private Integer status;

    @ApiModelProperty(value = "备注信息")
    @Column(columnDefinition = "varchar(200) comment '备注信息'")
    private String description;

    @ApiModelProperty(value = "IP白名单")
    @Column(columnDefinition = "varchar(300) comment 'ip'")
    private String allowIps;

    @ApiModelProperty(value = "员工编号")
    @Column(columnDefinition = "varchar(10) comment '员工编号'")
    private String staffNo;

    @ApiModelProperty(value = "google验证--key")
    @Column(columnDefinition = "varchar(16) comment 'google验证--key'")
    private String googleAuthKey;

    @ApiModelProperty(value = "角色权限")
    @Column(columnDefinition = "bigint comment '角色权限 id'")
    private Long roleId;

    public static boolean checkUserName(String userName) {
        if (CommonUtil.checkNull(userName)) {
            return true;
        }
        //长度验证
        int min = 6;
        int max = 15;
        int length = userName.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        String regex = RegexEnum.ACCOUNT.getRegex();
        boolean userNameRegex = userName.matches(regex);
        if (!userNameRegex) {
            //不规范
            return true;
        }
        return false;
    }

    /**
     * 昵称(长度3-10位)
     *
     * @param nickName
     * @return
     */
    public static boolean checkNickName(String nickName) {
        if (CommonUtil.checkNull(nickName)) {
            return true;
        }
        //长度验证
        int min = 1;
        int max = 20;
        int length = nickName.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        String regex = RegexEnum.NAME.getRegex();
        boolean nickNameRegex = nickName.matches(regex);
        if (!nickNameRegex) {
            //不规范
            return true;
        }
        return false;
    }

    /**
     * 密碼(长度3-20位)
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        if (CommonUtil.checkNull(password)) {
            return true;
        }
        //长度验证
        int min = 6;
        int max = 15;
        int length = password.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        String regex = RegexEnum.PASSWORD.getRegex();
        boolean passwordRegex = password.matches(regex);
        if (!passwordRegex) {
            //不规范
            return true;
        }
        return false;
    }

    /**
     * 手机号验证
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        if (CommonUtil.checkNull(phone)) {
            return true;
        }
        //长度验证
        int min = 1;
        int max = 20;
        int length = phone.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        String regex = RegexEnum.NUMBER_OR_LETTER.getRegex();
        boolean phoneRegex = phone.matches(regex);
        if (!phoneRegex) {
            //不规范
            return true;
        }
        return false;
    }

    public static boolean validateIP(String allowIps) {

        boolean isIp = true;
        if (StringUtils.isAllEmpty(allowIps)) {
            isIp = false;
        } else {
            List<String> allowIpList = Arrays.asList(allowIps.split(","));
            for (String allowIp : allowIpList) {
                if (StringUtils.isAllEmpty(allowIps)) {
                    isIp = false;
                    continue;
                }
                final Pattern ipPattern = Pattern.compile(RegexEnum.IP.getRegex());
                if (!ipPattern.matcher(allowIp).matches()) {
                    isIp = false;
                    continue;
                }
            }
        }
        return isIp;
    }

    public static boolean isFirstTime(String googleAuthKey) {
        if (StringUtils.isAllEmpty(googleAuthKey)) {
            return true;
        }
        return false;
    }
}
