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
@org.hibernate.annotations.Table(appliesTo = "admin", comment = "管理员")
public class Admin extends BaseEntity {

    @Column(unique = true, columnDefinition = "varchar(30) comment '用户名'")
    private String userName;

    @Column(columnDefinition = "varchar(30) comment '昵称'")
    private String nickName;

    @Column(columnDefinition = "varchar(64) comment '密码'")
    private String password;

    @Column(columnDefinition = "varchar(20) comment '手机号'")
    private String phone;

    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0禁用'")
    private Integer status = 1;


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
