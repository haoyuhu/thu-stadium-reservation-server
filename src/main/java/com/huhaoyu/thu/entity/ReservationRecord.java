package com.huhaoyu.thu.entity;

import com.huhaoyu.thu.widget.VisibleEntity;
import com.huhaoyu.thu.widget.VisibleField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by huhaoyu
 * Created On 2017/1/25 下午5:55.
 */

@VisibleEntity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tb_reservation_record")
public class ReservationRecord {

    @VisibleField
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @VisibleField(visible = false)
    @Column(name = "open_id", nullable = false)
    private String openId;

    @VisibleField
    @Column(name = "stadium_name", nullable = false)
    private String stadiumName;

    @VisibleField
    @Column(name = "site_name", nullable = false)
    private String siteName;

    @VisibleField
    @Column(name = "start_time", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startTime;

    @VisibleField
    @Column(name = "end_time", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date endTime;

    @VisibleField
    @Column(name = "status", nullable = false)
    private Integer status;

    @VisibleField
    @Column(name = "account_username", nullable = false)
    private String accountUsername;

    @VisibleField
    @Column(name = "cost", nullable = false)
    private Double cost;

    @VisibleField
    @Column(name = "description")
    private String description;

    @VisibleField
    @Column(name = "created_time", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdTime;

    @VisibleField(visible = false)
    @Column(name = "updated_time", nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date updatedTime;

}
