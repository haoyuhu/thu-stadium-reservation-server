package com.huhaoyu.thu.service;

import com.huhaoyu.thu.entity.ReservationCandidate;
import com.huhaoyu.thu.entity.ReservationGroup;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:48.
 */

public interface ReservationService {

    List<ReservationGroup> getReservationGroupByProperties(String openId, Long groupId, Boolean available, String name);

    ReservationGroup createReservationGroup(String openId, String name, Boolean available, String description,
                                            String mailReceivers);

    ReservationGroup updateReservationGroup(String openId, Long groupId, String name, Boolean available,
                                            String description, String mailReceivers);

    boolean deleteReservationGroup(String openId, Long groupId);

    List<ReservationCandidate> getReservationCandidateByProperties(String openId, Long groupId, Long candidateId,
                                                                   Boolean available, Integer sportType);

    ReservationCandidate createReservationCandidate(String openId, Long groupId, Boolean available, Integer sportType,
                                                    Integer week, String wishStart, String wishEnd, String sectionStart,
                                                    String sectionEnd, Boolean fixed, String description);

    ReservationCandidate updateReservationCandidate(String openId, Long groupId, Long candidateId, Boolean available,
                                                    Integer sportType, Integer week, String wishStart, String wishEnd,
                                                    String sectionStart, String sectionEnd, Boolean fixed, String description);

    boolean deleteReservationCandidate(String openId, Long groupId, Long candidateId);

}
