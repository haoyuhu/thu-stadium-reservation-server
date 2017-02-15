package com.huhaoyu.thu.service.impl;

import com.huhaoyu.thu.common.Constants;
import com.huhaoyu.thu.common.UnacceptableParamException;
import com.huhaoyu.thu.common.Validator;
import com.huhaoyu.thu.entity.ReservationRecord;
import com.huhaoyu.thu.repository.RecordRepository;
import com.huhaoyu.thu.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/2/8 上午11:34.
 */

@Service
@Transactional
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public List<ReservationRecord> getRecordByProperties(String openId, Long recordId, String stadiumName,
                                                         String siteName, Long startTime, Long endTime,
                                                         String accountUsername, Long createdBefore, Long createdAfter,
                                                         Integer limit, Integer status) {
        ReservationRecord record = new ReservationRecord();
        record.setOpenId(openId);
        record.setId(recordId);
        record.setStadiumName(stadiumName);
        record.setSiteName(siteName);
        record.setStartTime(startTime != null ? new Date(startTime) : null);
        record.setEndTime(endTime != null ? new Date(endTime) : null);
        record.setAccountUsername(accountUsername);
        record.setStatus(status);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<ReservationRecord> example = Example.of(record, matcher);
        List<ReservationRecord> list = recordRepository.findAll(example);

        List<ReservationRecord> ret = new ArrayList<>();
        long current = new Date().getTime();
        for (ReservationRecord item : list) {
            long createdTime = item.getCreatedTime().getTime();
            if ((createdAfter == null || createdTime >= createdAfter)
                    && (createdBefore == null || createdTime <= createdBefore)) {
                // update status for record which was started
                Constants.RecordStatus s = Constants.RecordStatus.from(item.getStatus());
                if ((Constants.RecordStatus.Ready.equals(s) || Constants.RecordStatus.Unpaid.equals(s))
                        && item.getStartTime().getTime() <= current) {
                    item.setStatus(Constants.RecordStatus.Finished.getCode());
                    recordRepository.save(item);
                }
                // add record to list
                ret.add(item);
                if (limit != null && ret.size() >= limit) break;
            }
        }

        return ret;
    }

    @Override
    public ReservationRecord createRecord(String openId, String stadiumName, String siteName, Long startTime,
                                          Long endTime, String accountUsername, Double cost, String description) {
        if (!Validator.validateNotNull(startTime, endTime, cost).isPassed()
                || !Validator.validateStringNotEmpty(openId, stadiumName, siteName, accountUsername).isPassed()
                || startTime >= endTime) {
            throw new UnacceptableParamException();
        }

        ReservationRecord record = new ReservationRecord();
        record.setOpenId(openId);
        record.setStadiumName(stadiumName);
        record.setSiteName(siteName);
        record.setStartTime(new Date(startTime));
        record.setEndTime(new Date(endTime));
        record.setAccountUsername(accountUsername);
        record.setCost(cost);
        record.setStatus(Constants.RecordStatus.Ready.getCode());
        record.setDescription(description);

        return recordRepository.save(record);
    }

    @Override
    public boolean deleteRecord(String openId, Long recordId) {
        ReservationRecord record = recordRepository.findByIdAndOpenId(recordId, openId);
        if (record == null) {
            throw new UnacceptableParamException();
        }
        record.setStatus(Constants.RecordStatus.Discard.getCode());
        recordRepository.save(record);
        return true;
    }

}
