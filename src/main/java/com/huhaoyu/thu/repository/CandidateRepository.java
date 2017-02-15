package com.huhaoyu.thu.repository;

import com.huhaoyu.thu.entity.ReservationCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:46.
 */

public interface CandidateRepository extends JpaRepository<ReservationCandidate, Long> {
}
