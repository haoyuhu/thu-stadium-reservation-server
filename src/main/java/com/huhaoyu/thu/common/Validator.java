package com.huhaoyu.thu.common;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by huhaoyu
 * Created On 2017/2/7 下午8:40.
 */

public class Validator {

    private static final String REGEX_PHONE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    private static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    private static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

    private static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    private static final String REGEX_IP = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    @Getter
    public enum Error {
        OK("ok"),
        EMPTY_STRING("empty string"),
        NULL_FIELD("null field"),
        FIELD_VALUE_EXCEEDS_LIMIT("field value exceeds limit"),
        FIELD_LENGTH_EXCEEDS_LIMIT("field length exceeds limit"),
        UNACCEPTABLE_CHARACTERS("unacceptable characters"),
        UNACCEPTABLE_FIELD_PATTERN("unacceptable field pattern"),
        UNACCEPTABLE_MAIL("unacceptable mail"),
        UNACCEPTABLE_PHONE("unacceptable phone number"),
        INVALID_ID_CARD_NUMBER("invalid id card number"),
        INVALID_URL("invalid url"),
        INVALID_IP_ADDRESS("invalid ip address");

        String description;

        Error(String description) {
            this.description = description;
        }

        public boolean isPassed() {
            return this == OK;
        }
    }

    public static Error validateMail(String email) {
        return Pattern.matches(REGEX_EMAIL, email) ? Error.OK : Error.UNACCEPTABLE_MAIL;
    }

    public static Error validatePhone(String phone) {
        return Pattern.matches(REGEX_PHONE, phone) ? Error.OK : Error.UNACCEPTABLE_PHONE;
    }

    public static Error validateIdCard(String card) {
        return Pattern.matches(REGEX_ID_CARD, card) ? Error.OK : Error.INVALID_ID_CARD_NUMBER;
    }

    public static Error validateUrl(String url) {
        return Pattern.matches(REGEX_URL, url) ? Error.OK : Error.INVALID_URL;
    }

    public static Error validateIPAddress(String address) {
        return Pattern.matches(REGEX_IP, address) ? Error.OK : Error.INVALID_IP_ADDRESS;
    }

    public static Error validateStringNotEmpty(String... strings) {
        for (String str : strings) {
            if (StringUtils.isEmpty(str)) return Error.EMPTY_STRING;
        }
        return Error.OK;
    }

    public static Error validateNotNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) return Error.NULL_FIELD;
        }
        return Error.OK;
    }

    public static Error validateLength(String field, Integer min, Integer max) {
        if (field == null) {
            return min == 0 ? Error.OK : Error.FIELD_LENGTH_EXCEEDS_LIMIT;
        }
        int len = field.length();
        return len >= min && len <= max ? Error.OK : Error.FIELD_LENGTH_EXCEEDS_LIMIT;
    }

}
