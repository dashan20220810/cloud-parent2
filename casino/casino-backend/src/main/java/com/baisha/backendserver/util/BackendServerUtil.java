package com.baisha.backendserver.util;

import com.baisha.backendserver.constants.BackendServerConstants;
import com.baisha.modulecommon.Constants;
import com.baisha.modulejjwt.JjwtUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.baisha.modulecommon.util.CommonUtil.checkNull;

/**
 * @author yihui
 */
public class BackendServerUtil {

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

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getToken() {
        String token = getRequest().getHeader(Constants.AUTHORIZATION);
        if (!ObjectUtils.isEmpty(token)) {
            token = token.replaceAll("Bearer ", "");
        }
        return token;
    }

    public static String getToken(String token) {
        if (!ObjectUtils.isEmpty(token)) {
            token = token.replaceAll("Bearer ", "");
        }
        return token;
    }

    public static Long getCurrentUserId() {
        String token = getToken();
        JjwtUtil.Subject subject = JjwtUtil.getSubject(token);
        if (subject == null || ObjectUtils.isEmpty(subject.getUserId())) {
            return null;
        }
        //获取登陆用户ID
        Long authId = Long.parseLong(subject.getUserId());
        return authId;
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
        if (isDelete != BackendServerConstants.DELETE_NORMAL && isDelete != BackendServerConstants.DELETE_DISABLED) {
            return true;
        }
        return false;
    }


    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>(16);
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


}
