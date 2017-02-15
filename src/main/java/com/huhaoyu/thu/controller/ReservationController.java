package com.huhaoyu.thu.controller;

import com.huhaoyu.thu.common.Constants.Response;
import com.huhaoyu.thu.entity.ReservationCandidate;
import com.huhaoyu.thu.entity.ReservationGroup;
import com.huhaoyu.thu.service.AuthorizationService;
import com.huhaoyu.thu.service.ReservationService;
import com.huhaoyu.thu.widget.VisibleEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:42.
 */

@RestController
@RequestMapping(value = "group")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getReservationGroups(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                               @RequestParam(value = "group_id", required = false) Long groupId,
                                               @RequestParam(value = "available", required = false) Boolean available,
                                               @RequestParam(value = "name", required = false) String name) {
        List<ReservationGroup> groups = reservationService.getReservationGroupByProperties(openId, groupId, available, name);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(groups);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createReservationGroup(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "available", required = false) Boolean available,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "mail_receivers", required = false) String mailReceivers) {
        ReservationGroup group = reservationService.createReservationGroup(openId, name, available, description, mailReceivers);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(group);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateReservationGroup(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                 @RequestParam(value = "group_id") Long groupId,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "available", required = false) Boolean available,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "mail_receivers", required = false) String mailReceivers) {
        ReservationGroup group = reservationService.updateReservationGroup(openId, groupId, name, available, description, mailReceivers);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(group);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteReservationGroup(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                 @RequestParam(value = "group_id") Long groupId) {
        reservationService.deleteReservationGroup(openId, groupId);
        return ResponseEntity.ok(Response.Ok.createResponseMap());
    }

    @RequestMapping(value = "candidate", method = RequestMethod.GET)
    public ResponseEntity getReservationCandidate(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                  @RequestParam(value = "group_id") Long groupId,
                                                  @RequestParam(value = "candidate_id", required = false) Long candidateId,
                                                  @RequestParam(value = "available", required = false) Boolean available,
                                                  @RequestParam(value = "sport_type", required = false) Integer sportType) {
        List<ReservationCandidate> candidates = reservationService.getReservationCandidateByProperties(openId, groupId,
                candidateId, available, sportType);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(candidates);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(value = "candidate", method = RequestMethod.POST)
    public ResponseEntity createReservationCandidate(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                     @RequestParam(value = "group_id") Long groupId,
                                                     @RequestParam(value = "available", required = false) Boolean available,
                                                     @RequestParam(value = "sport_type") Integer sportType,
                                                     @RequestParam(value = "week") Integer week,
                                                     @RequestParam(value = "wish_start") String wishStart,
                                                     @RequestParam(value = "wish_end") String wishEnd,
                                                     @RequestParam(value = "section_start", required = false) String sectionStart,
                                                     @RequestParam(value = "section_end", required = false) String sectionEnd,
                                                     @RequestParam(value = "fixed") Boolean fixed,
                                                     @RequestParam(value = "description", required = false) String description) {
        ReservationCandidate candidate = reservationService.createReservationCandidate(openId, groupId, available,
                sportType, week, wishStart, wishEnd, sectionStart, sectionEnd, fixed, description);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(candidate);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(value = "candidate", method = RequestMethod.PUT)
    public ResponseEntity updateReservationCandidate(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                     @RequestParam(value = "group_id") Long groupId,
                                                     @RequestParam(value = "candidate_id") Long candidateId,
                                                     @RequestParam(value = "available", required = false) Boolean available,
                                                     @RequestParam(value = "sport_type", required = false) Integer sportType,
                                                     @RequestParam(value = "week", required = false) Integer week,
                                                     @RequestParam(value = "wish_start", required = false) String wishStart,
                                                     @RequestParam(value = "wish_end", required = false) String wishEnd,
                                                     @RequestParam(value = "section_start", required = false) String sectionStart,
                                                     @RequestParam(value = "section_end", required = false) String sectionEnd,
                                                     @RequestParam(value = "fixed", required = false) Boolean fixed,
                                                     @RequestParam(value = "description", required = false) String description) {
        ReservationCandidate candidate = reservationService.updateReservationCandidate(openId, groupId, candidateId,
                available, sportType, week, wishStart, wishEnd, sectionStart, sectionEnd, fixed, description);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(candidate);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(value = "candidate", method = RequestMethod.DELETE)
    public ResponseEntity deleteReservationCandidate(@RequestAttribute(value = AuthorizationService.OPEN_ID) String openId,
                                                     @RequestParam(value = "group_id") Long groupId,
                                                     @RequestParam(value = "candidate_id") Long candidateId) {
        reservationService.deleteReservationCandidate(openId, groupId, candidateId);
        return ResponseEntity.ok(Response.Ok.createResponseMap());
    }

}
