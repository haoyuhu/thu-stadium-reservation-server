package com.huhaoyu.thu.common;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:40.
 */
public class CommonUtil {

    public static String SEMICOLON_SEPARATOR = ";";
    public static String COMMA_SEPARATOR = ",";
    public static String SPACE_SEPARATOR = " ";
    public static String COLON_SEPARATOR = ":";

    public static List<String> splitStringBySeparator(String str, String separator) {
        if (!StringUtils.isEmpty(str)) {
            String[] list = str.split(separator);
            return Arrays.asList(list);
        }
        return new ArrayList<>();
    }

    public static int DAY_OF_WEEK = 7;
    public static int HOUR_OF_DAY = 24;
    public static int MINUTE_OF_HOUR = 60;
    public static int SECOND_OF_MINUTE = 60;
    public static int MILLIS_OF_SECOND = 1000;

    public static boolean validateWeek(Integer week) {
        return week >= 0 && week < DAY_OF_WEEK;
    }

    public static boolean validateTimeString(String time) {
        if (time != null) {
            String[] parts = time.split(COLON_SEPARATOR);
            if (parts.length == 2 || parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                int seconds = 0;
                if (parts.length == 3) seconds = Integer.parseInt(parts[2]);
                return hours >= 0 && hours < HOUR_OF_DAY
                        && minutes >= 0 && minutes < MINUTE_OF_HOUR
                        && seconds >= 0 && seconds < SECOND_OF_MINUTE;
            }
        }
        return false;
    }

    public static boolean validateTimeStrings(String... times) {
        for (String time : times) {
            if (!validateTimeString(time)) return false;
        }
        return true;
    }

    public static List<Integer> parseTimeString(String time) {
        if (validateTimeString(time)) {
            return splitStringBySeparator(time, COLON_SEPARATOR).stream().mapToInt(Integer::parseInt).boxed()
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException("wrong time string, pattern should be like 8:34:12 or 8:34");
    }

    public static Long convertTimeStringToSeconds(String time) {
        List<Integer> parts = parseTimeString(time);
        long ret = parts.get(0);
        ret = ret * MINUTE_OF_HOUR + parts.get(1);
        ret = ret * SECOND_OF_MINUTE + (parts.size() == 3 ? parts.get(2) : 0);
        return ret;
    }

    public static boolean compareTimeString(String front, String rear) {
        Long t1 = convertTimeStringToSeconds(front);
        Long t2 = convertTimeStringToSeconds(rear);
        return t1 < t2;
    }

    public static int getTimeStringIntervalInSecond(String front, String rear) {
        Long t1 = convertTimeStringToSeconds(front);
        Long t2 = convertTimeStringToSeconds(rear);
        return (int) (t1 - t2);
    }

}
