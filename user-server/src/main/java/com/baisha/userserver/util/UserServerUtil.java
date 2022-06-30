package com.baisha.userserver.util;

import com.baisha.modulecommon.Constants;
import com.baisha.userserver.util.constants.UserServerConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

import static com.baisha.modulecommon.util.CommonUtil.checkNull;

/**
 * @author yihui
 */
public class UserServerUtil {

    /**
     * 加密
     *
     * @param value
     * @return
     */
    public static String bcrypt(String value) {
        return BCrypt.hashpw(value, BCrypt.gensalt());
    }

    /**
     * 校验加密
     *
     * @param value
     * @param bcryptValue
     * @return
     */
    public static boolean checkBcrypt(String value, String bcryptValue) {
        if (checkNull(value) || checkNull(bcryptValue)) {
            return false;
        }
        return BCrypt.checkpw(value, bcryptValue);
    }


    /**
     * 只能输入字母和数字
     *
     * @param str
     * @return
     */
    public static boolean checkLetterAndNumber(String str) {
        String regex = "^[a-z0-9A-Z_]+$";
        return str.matches(regex);
    }

    public static boolean checkLetterAndNumberAndChinese(String str) {
        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    /**
     * 金额大于0
     *
     * @param decimal
     * @return
     */
    public static boolean checkZero(BigDecimal decimal) {
        if (null == decimal) {
            return true;
        }
        if (decimal.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 验证状态
     *
     * @param status
     * @return
     */
    public static boolean checkStatus(Integer status) {
        if (null == status) {
            return true;
        }
        if (!Constants.open.equals(status) && !Constants.close.equals(status)) {
            return true;
        }
        return false;
    }

    /**
     * 获取TG的userName
     *
     * @param tgGroupId
     * @param tgUserId
     * @return
     */
    public static String getTgUserName(String tgGroupId, String tgUserId) {
        //去掉 - 负号
        String regex = "-";
        if (tgGroupId.contains(regex)) {
            tgGroupId = tgGroupId.replaceAll(regex, "");
        }
        return tgUserId + "_" + tgGroupId;
    }

    public static Pageable setPageable(Integer pageCode, Integer pageSize, Sort sort) {
        if (Objects.isNull(sort)) {
            sort = Sort.unsorted();
        }

        if (pageSize == null || pageCode == null) {
            pageCode = 1;
            pageSize = 10;
        }

        if (pageCode < 1 || pageSize < 1) {
            pageCode = 1;
            pageSize = 10;
        }

        if (pageSize > 100) {
            pageSize = 100;
        }

        Pageable pageable = PageRequest.of(pageCode - 1, pageSize, sort);
        return pageable;
    }

    public static Pageable setPageable(Integer pageCode, Integer pageSize) {
        return setPageable(pageCode, pageSize, null);
    }

    /**
     * 邀请码 大写加数字
     * 默认8位数
     *
     * @return
     */
    public static String randomCode() {
        int i = 23456789;
        String s = "qwertyupasdfghjkzxcvbnm";
        String S = s.toUpperCase();
        String word = S + i;
        // 获取包含26个字母大写和数字的字符数组
        char[] c = word.toCharArray();
        Random rd = new Random();
        String code = "";
        for (int k = 0; k < 8; k++) {
            // 随机获取数组长度作为索引
            int index = rd.nextInt(c.length);
            // 循环添加到字符串后面
            code += c[index];
        }
        return code;
    }

   /* public static void main(String[] args) {
        String us = "111q11_Q```";
        System.out.println(checkLetterAndNumber(us));
        String us2 = "111q11sas撒上v";
        System.out.println(checkLetterAndNumberAndChinese(us2));
    }*/


}
