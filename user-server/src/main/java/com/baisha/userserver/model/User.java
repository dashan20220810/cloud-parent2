package com.baisha.userserver.model;

import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.userserver.constants.UserServerConstants;
import com.baisha.userserver.util.UserServerUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userName"}))
public class User extends BaseEntity {

    /**
     * 会员名
     */
    @Column(unique = true, length = 30)
    private String userName;
    /**
     * 昵称
     */
    @Column(length = 20)
    private String nickName;
    /**
     * 密码
     */
    @Column(length = 64)
    private String password;
    /**
     * IP
     */
    @Column(length = 130)
    private String ip;
    /**
     * 状态 1 正常 ，2禁用
     */
    @Column(precision = 1)
    private Integer status = 1;


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
