package com.huhaoyu.thu.service;

import com.huhaoyu.thu.entity.Stadium;

import java.util.List;
import java.util.Map;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:51.
 */
public interface ScheduledTaskService {

    boolean notifyReceiversByMail(String secretId, String openId, Long groupId, String encrypted, String signature);

    String getEncryptedReservationListString();

    List<Stadium> getStadiumByProperties(Long stadiumId, String name, String stadiumCode, String siteCode, Integer sportType);

    Stadium createStadium(String name, String stadiumCode, String siteCode, Integer sportType, String exceptions, String description);

    Stadium updateStadium(Long stadiumId, String name, String stadiumCode, String siteCode, Integer sportType, String exceptions, String description);

    boolean deleteStadium(Long stadiumId);

}
