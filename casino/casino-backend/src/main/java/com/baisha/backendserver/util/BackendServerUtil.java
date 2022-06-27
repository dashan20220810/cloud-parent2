package com.baisha.backendserver.util;

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
import java.util.*;

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
        if (!Constants.open.equals(isDelete) && !Constants.close.equals(isDelete)) {
            return true;
        }
        return false;
    }


    /**
     * 实体对象转成Map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> reMap = new HashMap<>(16);
        if (obj == null) {
            return null;
        }
        List<Field> fields = new ArrayList<>();
        List<Field> childFields;
        List<String> fieldsName = new ArrayList<>();
        Class tempClass = obj.getClass();
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        childFields = Arrays.asList(obj.getClass().getDeclaredFields());
        for (Field field : childFields) {
            fieldsName.add(field.getName());
        }
        try {
            for (Field field : fields) {
                try {
                    if (fieldsName.contains(field.getName())) {
                        Field f = obj.getClass().getDeclaredField(field.getName());
                        f.setAccessible(true);
                        Object o = f.get(obj);
                        reMap.put(field.getName(), o);
                    } else {
                        Field f = obj.getClass().getSuperclass().getDeclaredField(field.getName());
                        f.setAccessible(true);
                        Object o = f.get(obj);
                        reMap.put(field.getName(), o);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return reMap;
    }


   /* public static void main(String[] args) {
        TgGroupPageVO vo = new TgGroupPageVO();
        vo.setBotName("1");
        System.out.println(JSON.toJSONString(vo));
        System.out.println(objectToMap(vo));

        //TgGroupBoundVO v = new TgGroupBoundVO();
        //v.setTgGroupId("1111");
        //System.out.println(objectToMap(v));
    }*/


}
