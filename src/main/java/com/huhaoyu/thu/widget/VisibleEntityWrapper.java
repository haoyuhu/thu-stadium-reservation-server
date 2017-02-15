package com.huhaoyu.thu.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by huhaoyu
 * Created On 2017/2/6 上午12:08.
 */

public abstract class VisibleEntityWrapper extends HashMap<String, Object> {

    private static Logger logger = LoggerFactory.getLogger(VisibleEntityWrapper.class);

    private static String getUnderscoreLowercaseFieldName(String name) {
        StringBuilder result = new StringBuilder();
        if (!StringUtils.isEmpty(name)) {
            // append the first character
            result.append(name.substring(0, 1).toLowerCase());
            // handle the rest characters
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // append underscore before uppercase character
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // convert to lowercase for all characters
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

    public static Object createVisibleFieldsMap(Object entity) {
        if (entity == null) {
            return null;
        }
        // Collection, Map, Date
        if (entity instanceof Collection) {
            Collection c = (Collection) entity;
            List<Object> ret = new ArrayList<>();
            for (Object item : c) {
                ret.add(createVisibleFieldsMap(item));
            }
            return ret;
        }
        if (entity instanceof Map) {
            Map m = (Map) entity;
            Map<Object, Object> ret = new HashMap<>();
            for (Object key : m.keySet()) {
                ret.put(key, createVisibleFieldsMap(m.get(key)));
            }
            return ret;
        }
        if (entity instanceof Date) {
            Date d = (Date) entity;
            return d.getTime();
        }

        Class clazz = entity.getClass();
        VisibleEntity visibleEntityAnnotation = (VisibleEntity) clazz.getAnnotation(VisibleEntity.class);
        if (visibleEntityAnnotation == null || !visibleEntityAnnotation.visible()) {
            return entity;
        }

        Map<String, Object> ret = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                VisibleField visibleFieldAnnotation = field.getAnnotation(VisibleField.class);
                if (visibleFieldAnnotation != null && visibleFieldAnnotation.visible()) {
                    String name = StringUtils.isEmpty(visibleFieldAnnotation.name()) ?
                            getUnderscoreLowercaseFieldName(field.getName()) : visibleFieldAnnotation.name();
                    try {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        Object child = field.get(entity);
                        ret.put(name, createVisibleFieldsMap(child));
                        field.setAccessible(accessible);
                    } catch (IllegalAccessException e) {
                        logger.error("cannot get field value of " + field.getName(), e);
                    }
                }
            }
        }
        return ret;
    }

}
