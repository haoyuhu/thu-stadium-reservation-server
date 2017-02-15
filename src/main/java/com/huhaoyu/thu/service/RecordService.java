package com.huhaoyu.thu.service;

import com.huhaoyu.thu.entity.ReservationRecord;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/2/8 上午11:33.
 */

public interface RecordService {

    List<ReservationRecord> getRecordByProperties(String openId, Long recordId, String stadiumName, String siteName,
                                                  Long startTime, Long endTime, String accountUsername,
                                                  Long createdBefore, Long createdAfter, Integer limit, Integer status);

    ReservationRecord createRecord(String openId, String stadiumName, String siteName, Long startTime, Long endTime,
                                   String accountUsername, Double cost, String description);

    boolean deleteRecord(String openId, Long recordId);

}
