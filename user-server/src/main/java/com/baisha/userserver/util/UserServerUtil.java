package com.baisha.userserver.util;

import com.baisha.modulecommon.Constants;
import com.baisha.userserver.constants.UserServerConstants;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.math.BigDecimal;

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
     * 验证 是否删除
     *
     * @param isDelete
     * @return
     */
    public static boolean checkIsDelete(Integer isDelete) {
        if (null == isDelete) {
            return true;
        }
        if (isDelete != UserServerConstants.DELETE_NORMAL && isDelete != UserServerConstants.DELETE_DISABLED) {
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

    /**
     * 去掉负号
     *
     * @param tgGroupId
     * @return
     */
    public static String getTgGroupId(String tgGroupId) {
        //去掉 - 负号
        String regex = "-";
        if (tgGroupId.contains(regex)) {
            tgGroupId = tgGroupId.replaceAll(regex, "");
        }
        return tgGroupId;
    }


    public static void main(String[] args) {
        String us = "111q11_Q```";
        System.out.println(checkLetterAndNumber(us));
        String us2 = "111q11sas撒上v";
        System.out.println(checkLetterAndNumberAndChinese(us2));
    }


}
