package com.huhaoyu.thu.entity;

import com.huhaoyu.thu.widget.VisibleEntity;
import com.huhaoyu.thu.widget.VisibleField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午12:16.
 */

@VisibleEntity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tb_reservation_candidate")
public class ReservationCandidate {

    @VisibleField
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @VisibleField
    @Column(name = "available", nullable = false)
    private Boolean available = Boolean.TRUE;

    @VisibleField
    @Column(name = "sport_type", nullable = false)
    private Integer sportType;

    @VisibleField
    @Column(name = "week", nullable = false)
    private Integer week;

    @VisibleField
    @Column(name = "wish_start", nullable = false)
    private String wishStartTime;

    @VisibleField
    @Column(name = "wish_end", nullable = false)
    private String wishEndTime;

    @VisibleField
    @Column(name = "section_start")
    private String sectionStartTime;

    @VisibleField
    @Column(name = "section_end")
    private String sectionEndTime;

    @VisibleField
    @Column(name = "fixed", nullable = false)
    private Boolean fixed = Boolean.FALSE;

    @VisibleField
    @Column(name = "description")
    private String description;

    @VisibleField(visible = false)
    @Column(name = "created_time", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;

    @VisibleField(visible = false)
    @Column(name = "updated_time", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedTime;

}
