package com.baisha.modulecommon.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonUtil {

    //返回num个随机数
    public static String random(int num) {

        String randomStr = "";
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            //使用nextInt(int count)获得count以内的整数，不含count
            int j = random.nextInt(10);
            randomStr = randomStr + j;
        }

        return randomStr;
    }

    public static Boolean isWindows() {
        String os = System.getProperty("os.name");
        System.out.println(os);
        if (os.toLowerCase().startsWith("win")) {
            return true;
        }
        return false;
    }

    public static boolean checkNull(String... args) {
        if (args == null || args.length == 0) {
            return true;
        }

        for (String arg : args) {
            if (ObjectUtils.isEmpty(arg)) {
                return true;
            }
        }

        return false;
    }

    public static String getLocalPicPath() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "D:/pic/";
        } else {
            return "/usr/path/";
        }
    }

    public static BigDecimal checkMoney(String money){
        BigDecimal decMoney = null;
        try {
            decMoney = new BigDecimal(money);
        }catch (Exception e){
            decMoney = new BigDecimal(-1);
        }

        return decMoney;
    }
    //集合去重
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    // 检测对象字段值是否为null
    public static boolean checkObjectFieldNotNull(Object object) throws IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.get(object) == null || StringUtils.isEmpty(field.get(object).toString())) {
                return false;
            }
        }
        return true;
    }
}
