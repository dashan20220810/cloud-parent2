package com.baisha.userserver.model;

import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.userserver.util.UserServerUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "user", comment = "会员")
@Table(name = "User", indexes = {@Index(columnList = "tgUserId"), @Index(columnList = "inviteUserId"), @Index(columnList = "userType")})
@ApiModel(value = "用户中心-用户对象")
public class User extends BaseEntity {

    @ApiModelProperty(value = "会员账号")
    @Column(unique = true, columnDefinition = "varchar(30) comment '会员账号'")
    private String userName;

    @ApiModelProperty(value = "昵称")
    @Column(columnDefinition = "varchar(20) comment '昵称'")
    private String nickName;

    @ApiModelProperty(value = "密码")
    @Column(columnDefinition = "varchar(64) comment '密码'")
    private String password;

    @ApiModelProperty(value = "TG用户ID")
    @Column(columnDefinition = "varchar(64) comment 'TG用户ID'")
    private String tgUserId;

    @ApiModelProperty(value = "TG群ID")
    @Column(columnDefinition = "varchar(64) comment 'TG群ID'")
    private String tgGroupId;

    @ApiModelProperty(value = "TG用户名称(@xxx)")
    @Column(columnDefinition = "varchar(30) comment 'TG用户名称(@xxx)'")
    private String tgUserName;

    @ApiModelProperty(value = "TG群名称")
    @Column(columnDefinition = "varchar(64) comment 'TG群名称'")
    private String tgGroupName;

    @ApiModelProperty(value = "IP")
    @Column(columnDefinition = "varchar(130) comment 'IP'")
    private String ip;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0禁用'")
    private Integer status = 1;

    @ApiModelProperty(value = "来源")
    @Column(columnDefinition = "varchar(20) comment '来源'")
    private String origin;

    @ApiModelProperty(value = "邀请码")
    @Column(columnDefinition = "varchar(10) comment '邀请码'")
    private String inviteCode;

    @ApiModelProperty(value = "邀请人会员ID")
    @Column(columnDefinition = "bigint(11) comment '邀请人会员ID'")
    private Long inviteUserId;

    @ApiModelProperty(value = "用户类型")
    @Column(columnDefinition = "tinyint(2) comment '用户类型 1正式 2测试 3机器人'")
    private Integer userType = 1;

    @ApiModelProperty(value = "渠道")
    @Column(columnDefinition = "varchar(20) comment '渠道'")
    private String channelCode;

    @ApiModelProperty(value = "手机号")
    @Column(columnDefinition = "varchar(20) comment '手机号'")
    private String phone;


    /**
     * 用户名(长度3-20,只能输入字母和数字和_)
     *
     * @param userName
     * @return
     */
    public static boolean checkUserName(String userName) {
        if (CommonUtil.checkNull(userName)) {
            return true;
        }
        //长度验证
        int min = 3;
        int max = 30;
        int length = userName.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        boolean userNameRegex = UserServerUtil.checkLetterAndNumber(userName);
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
        int min = 3;
        int max = 10;
        int length = nickName.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        boolean nickNameRegex = UserServerUtil.checkLetterAndNumberAndChinese(nickName);
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
        int min = 3;
        int max = 20;
        int length = password.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        return false;
    }

    /**
     * ip(长度3-150位)
     *
     * @param ip
     * @return
     */
    public static boolean checkIp(String ip) {
        if (CommonUtil.checkNull(ip)) {
            return true;
        }
        //长度验证
        int min = 3;
        int max = 128;
        int length = ip.length();
        if (length < min || length > max) {
            //长度不规范
            return true;
        }
        return false;
    }


}
