package com.huhaoyu.thu.common;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:22.
 */
public interface Constants {

    String GLOBAL_ADMIN_RECEIVER = "subscribe@huhaoyu.com";

    @Getter
    enum SimpleMailTemplate {
        Test("Test mail subject for thu-stadium-reservation-server",
                "Test mail body for thu-stadium-reservation-server"),
        ReservationSuccess("[新预约]您好，场馆预约助手为您预定到一个新的羽毛球场地",
                "场馆位置: %s;\n预约时间: %s;\n所用账号: %s;\n总费用: %.2f;\n" +
                        "备注：如您预订的场馆为气膜馆，则可在50.tsinghua.edu.cn中退订后重订并采用支付宝支付。\n\n" +
                        "本订单由场馆预约助手于%s完成预约ʕ •ᴥ•ʔ。");


        String subject;
        String body;

        SimpleMailTemplate(String subject, String body) {
            this.subject = subject;
            this.body = body;
        }
    }

    @Getter
    enum Response {
        Ok(0, "ok"),
        BadRequest(-1000, "bad request"),
        ServerError(-1001, "server error"),
        IncorrectRequestParameterOrMethod(-1002, "incorrect request parameter or request method"),
        Unauthorized(-1003, "incorrect or empty session_id"),
        Unverified(-1004, "incorrect or empty secret_id"),
        EntityExists(-1005, "entity exists"),
        IncorrectTHUAccount(-1006, "incorrect tsinghua account or tsinghua platform error"),
        IncorrectAuthCode(-1007, "incorrect wechat authorization code"),
        WechatServerError(-1008, "wechat server error for authorization");

        int errorCode;
        String errorDescription;

        Response(int errorCode, String errorDescription) {
            this.errorCode = errorCode;
            this.errorDescription = errorDescription;
        }

        public Map<String, Object> createResponseMap() {
            return createResponseMap(null);
        }

        public Map<String, Object> createResponseMap(Object data) {
            Map<String, Object> ret = new HashMap<>();
            ret.put("error_code", this.errorCode);
            ret.put("error_description", this.errorDescription);
            if (data != null) {
                ret.put("data", data);
            }
            return ret;
        }
    }

    @Getter
    enum AccountStatus {
        Using(0), Available(1), Locked(2), VerificationFail(3);
        Integer code;

        AccountStatus(int code) {
            this.code = code;
        }

        public static AccountStatus from(Integer code) {
            for (AccountStatus status : AccountStatus.values()) {
                if (status.code.equals(code))
                    return status;
            }
            return null;
        }

        public static boolean validate(Integer code) {
            return from(code) != null;
        }
    }

    @Getter
    enum ReservationStatus {

    }

    @Getter
    enum RecordStatus {
        Finished(0), Ready(1), Unpaid(2), Discard(3);
        Integer code;

        RecordStatus(Integer code) {
            this.code = code;
        }

        public static RecordStatus from(Integer code) {
            for (RecordStatus status : RecordStatus.values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return null;
        }

        public static boolean validate(Integer code) {
            return from(code) != null;
        }
    }

    @Getter
    enum UserType {
        Student(0, "student"), Teacher(1, "teacher");
        Integer code;
        String name;

        UserType(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public static UserType from(Integer code) {
            for (UserType type : UserType.values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return null;
        }

        public static boolean validate(Integer code) {
            return from(code) != null;
        }
    }

    @Getter
    enum SportType {
        Badminton(0, "badminton"), PingPong(1, "pingpong"), Basketball(2, "basketball");
        Integer code;
        String name;

        SportType(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public static SportType from(Integer code) {
            for (SportType type : SportType.values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return null;
        }

        public static boolean validate(Integer code) {
            return from(code) != null;
        }
    }

    @Getter
    enum Gender {
        Unknown(0), Male(1), Female(2);
        Integer code;

        Gender(Integer code) {
            this.code = code;
        }

        public static Gender from(Integer code) {
            for (Gender gender : Gender.values()) {
                if (gender.code.equals(code)) {
                    return gender;
                }
            }
            return null;
        }

        public static boolean validate(Integer code) {
            return from(code) != null;
        }
    }

}
