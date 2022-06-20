package com.baisha.backendserver.model;

import com.baisha.modulecommon.RegexEnum;
import com.baisha.modulecommon.util.CommonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;

@Slf4j
@Data
@Entity
public class Admin extends BaseEntity {

    /**
     * 用户名
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
     * 手机号
     */
    @Column(length = 20)
    private String phone;

    /**
     * 状态 1 正常 ，2禁用
     */
    @Column(precision = 1)
    private Integer status = 1;

    /**
     * 是否删除 0否 1是
     */
    @Column(precision = 1)
    private Integer isDelete = 0;


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


}
