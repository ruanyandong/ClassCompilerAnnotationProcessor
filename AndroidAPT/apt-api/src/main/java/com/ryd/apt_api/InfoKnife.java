package com.ryd.apt_api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 11/19/21 4:17 PM
 * @desc : com.ryd.apt_api
 */
public class InfoKnife {

    public static void print(){
        try {
            Class<?> clz = Class.forName("com.ryd.androidapt.ClassInfoDesc");
            Constructor<?> constructor = clz.getDeclaredConstructor();
            Object obj = constructor.newInstance();
            Method method = clz.getDeclaredMethod("printInfo",String.class);
            method.invoke(obj,"我是参数");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
