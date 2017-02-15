package com.huhaoyu.thu.entity;

import com.huhaoyu.thu.common.CommonUtil;
import com.huhaoyu.thu.widget.VisibleEntity;
import com.huhaoyu.thu.widget.VisibleField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 上午11:20.
 */

@VisibleEntity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tb_reservation_group")
public class ReservationGroup {

    public static final String MAIL_SEPARATOR = CommonUtil.SEMICOLON_SEPARATOR;

    @VisibleField
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @VisibleField
    @Column(name = "name", nullable = false)
    private String name;

    @VisibleField
    @Column(name = "available", nullable = false)
    private Boolean available = Boolean.TRUE;

    @VisibleField
    @Column(name = "description")
    private String description;

    @VisibleField
    @Column(name = "email_receivers")
    private String receivers;

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

    @VisibleField
    @OneToMany(targetEntity = ReservationCandidate.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "tb_reservation_group_candidates", joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "candidate_id")})
    private Set<ReservationCandidate> candidates;

    @VisibleField(visible = false)
    @ManyToOne(targetEntity = WechatUser.class, cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "tb_wechat_user_reservation_groups", joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private WechatUser user;

    public List<String> getReceiverList() {
        return CommonUtil.splitStringBySeparator(this.receivers, MAIL_SEPARATOR);
    }

    public List<ReservationCandidate> getAvailableReservationCandidate() {
        return candidates.stream().filter(ReservationCandidate::getAvailable).collect(Collectors.toList());
    }

}
