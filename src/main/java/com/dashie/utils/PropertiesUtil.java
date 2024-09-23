package com.dashie.utils;

import java.util.Properties;

public class PropertiesUtil {
    public static boolean isNull(Properties properties) {
        if (isEmpty(properties, "address")) {
            return false;
        }
        if (isNull(properties, "limit")) {
            return false;
        }
        if (isNull(properties, "tags")) {
            return false;
        }
        if (isNull(properties, "page")) {
            return false;
        }
        if (isEmpty(properties, "output.path")) {
            return false;
        }
        if (isEmpty(properties, "strategy.api")) {
            return false;
        }
        if (isEmpty(properties, "strategy.download")) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(Properties properties, String key) {
        if (properties.getProperty(key) == null || properties.getProperty(key).trim().isEmpty()) {
            System.out.println("ERROR: 配置文件缺失[ " + key + "]项");
            return true;
        }
        return false;
    }
    public static boolean isNull(Properties properties, String key) {
        if (properties.getProperty(key) == null) {
            System.out.println("ERROR: 配置文件缺失[ " + key + "]项");
            return true;
        }
        return false;
    }
}
