package com.huhaoyu.thu.controller;

import com.huhaoyu.thu.common.Constants.Response;
import com.huhaoyu.thu.entity.ReservationRecord;
import com.huhaoyu.thu.service.AuthorizationService;
import com.huhaoyu.thu.service.RecordService;
import com.huhaoyu.thu.widget.VisibleEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/1/26 下午4:40.
 */

@RestController
@RequestMapping(value = "record")
public class ReservationRecordController {

    @Autowired
    private RecordService recordService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getReservationRecords(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                @RequestParam(value = "record_id", required = false) Long recordId,
                                                @RequestParam(value = "stadium_name", required = false) String stadiumName,
                                                @RequestParam(value = "site_name", required = false) String siteName,
                                                @RequestParam(value = "start_time", required = false) Long startTime,
                                                @RequestParam(value = "end_time", required = false) Long endTime,
                                                @RequestParam(value = "account_username", required = false) String accountUsername,
                                                @RequestParam(value = "created_before", required = false) Long createdBefore,
                                                @RequestParam(value = "created_after", required = false) Long createdAfter,
                                                @RequestParam(value = "limit", required = false) Integer limit,
                                                @RequestParam(value = "status", required = false) Integer status) {
        List<ReservationRecord> records = recordService.getRecordByProperties(openId, recordId, stadiumName, siteName,
                startTime, endTime, accountUsername, createdBefore, createdAfter, limit, status);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(records);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteReservationRecord(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                  @RequestParam(value = "record_id") Long recordId) {
        recordService.deleteRecord(openId, recordId);
        return ResponseEntity.ok(Response.Ok.createResponseMap());
    }

}
