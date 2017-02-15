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
 * Created On 2017/1/23 上午11:12.
 */

@VisibleEntity
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tb_thu_account")
public class THUAccount {

    @VisibleField
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @VisibleField
    @Column(name = "student_id", nullable = false)
    private String studentId;

    @VisibleField
    @Column(name = "username", nullable = false)
    private String username;

    @VisibleField(visible = false)
    @Column(name = "password", nullable = false)
    private String password;

    @VisibleField
    @Column(name = "alias")
    private String alias;

    @VisibleField
    @Column(name = "user_type", nullable = false)
    private Integer userType;

    @VisibleField
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @VisibleField
    @Column(name = "email", nullable = false)
    private String email;

    @VisibleField
    @Column(name = "status", nullable = false)
    private Integer status;

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
    @ManyToOne(targetEntity = WechatUser.class, cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "tb_wechat_user_thu_accounts", joinColumns = {@JoinColumn(name = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private WechatUser user;

}
