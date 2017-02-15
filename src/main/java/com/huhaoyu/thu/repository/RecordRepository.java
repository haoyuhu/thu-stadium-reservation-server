package com.huhaoyu.thu.repository;

import com.huhaoyu.thu.entity.ReservationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by huhaoyu
 * Created On 2017/2/8 下午11:46.
 */

public interface RecordRepository extends JpaRepository<ReservationRecord, Long> {

    ReservationRecord findByIdAndOpenId(Long recordId, String openId);

}
