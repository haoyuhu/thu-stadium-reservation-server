package com.huhaoyu.thu.controller;

import com.huhaoyu.thu.common.Constants.Response;
import com.huhaoyu.thu.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:45.
 */

@RestController
@RequestMapping(value = "task")
public class ScheduledTaskController {

    @Autowired
    private ScheduledTaskService taskService;

    @RequestMapping(value = "notify", method = RequestMethod.POST)
    public ResponseEntity notifyReceiversByMail(@RequestParam(value = "secret_id") String secretId,
                                                @RequestParam(value = "open_id") String openId,
                                                @RequestParam(value = "group_id") Long groupId,
                                                @RequestParam(value = "encrypted") String encrypted,
                                                @RequestParam(value = "signature") String signature) {
        boolean success = taskService.notifyReceiversByMail(secretId, openId, groupId, encrypted, signature);
        return ResponseEntity.ok(success ? Response.Ok.createResponseMap() : Response.ServerError.createResponseMap());
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity getEncryptedReservationList() {
        String encrypted = taskService.getEncryptedReservationListString();
        return ResponseEntity.ok(encrypted == null ?
                Response.ServerError.createResponseMap() : Response.Ok.createResponseMap(encrypted));
    }

}
