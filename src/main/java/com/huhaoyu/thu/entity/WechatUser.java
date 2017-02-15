package com.huhaoyu.thu.entity;

import com.huhaoyu.thu.common.Constants;
import com.huhaoyu.thu.widget.VisibleEntity;
import com.huhaoyu.thu.widget.VisibleField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by huhaoyu
 * Created On 2017/1/22 下午11:37.
 */

@VisibleEntity
@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_wechat_user")
public class WechatUser {

    @VisibleField(visible = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @VisibleField(visible = false)
    @Column(name = "open_id", nullable = false, unique = true)
    private String openId;

    @VisibleField
    @Column(name = "nickname", nullable = false)
    private String nickName;

    @VisibleField
    @Column(name = "gender", nullable = false)
    private Integer gender;

    @VisibleField
    @Column(name = "language")
    private String language;

    @VisibleField
    @Column(name = "city")
    private String city;

    @VisibleField
    @Column(name = "province")
    private String province;

    @VisibleField
    @Column(name = "country")
    private String country;

    @VisibleField
    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;

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

    @VisibleField(visible = false)
    @OneToMany(targetEntity = THUAccount.class, cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "tb_wechat_user_thu_accounts", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "account_id")})
    private Set<THUAccount> accounts;

    @VisibleField(visible = false)
    @OneToMany(targetEntity = ReservationGroup.class, cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "tb_wechat_user_reservation_groups", joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")})
    private Set<ReservationGroup> reservationGroups;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WechatUser that = (WechatUser) o;

        return openId != null ? openId.equals(that.openId) : that.openId == null;

    }

    @Override
    public int hashCode() {
        return openId != null ? openId.hashCode() : 0;
    }

    public THUAccount getAccountBeingUsed() {
        if (accounts == null) return null;

        for (THUAccount account : accounts) {
            if (account.getStatus().equals(Constants.AccountStatus.Using.getCode())) {
                return account;
            }
        }

        return null;
    }

    public List<ReservationGroup> getAvailableReservationGroup() {
        return reservationGroups.stream().filter(ReservationGroup::getAvailable).collect(Collectors.toList());
    }

}
