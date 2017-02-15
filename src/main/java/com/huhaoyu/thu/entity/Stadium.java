package com.huhaoyu.thu.entity;

import com.huhaoyu.thu.widget.VisibleEntity;
import com.huhaoyu.thu.widget.VisibleField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午2:51.
 */

@VisibleEntity
@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_stadium")
public class Stadium {

    @VisibleField
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @VisibleField
    @Column(name = "name", nullable = false)
    private String name;

    @VisibleField
    @Column(name = "stadium_code", nullable = false)
    private String stadiumCode;

    @VisibleField
    @Column(name = "sport_type", nullable = false)
    private Integer sportType;

    @VisibleField
    @Column(name = "site_code", nullable = false)
    private String siteCode;

    @VisibleField
    @Column(name = "exceptions")
    private String exceptions;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stadium stadium = (Stadium) o;

        if (stadiumCode != null ? !stadiumCode.equals(stadium.stadiumCode) : stadium.stadiumCode != null) return false;
        if (sportType != null ? !sportType.equals(stadium.sportType) : stadium.sportType != null) return false;
        return siteCode != null ? siteCode.equals(stadium.siteCode) : stadium.siteCode == null;

    }

    @Override
    public int hashCode() {
        int result = stadiumCode != null ? stadiumCode.hashCode() : 0;
        result = 31 * result + (sportType != null ? sportType.hashCode() : 0);
        result = 31 * result + (siteCode != null ? siteCode.hashCode() : 0);
        return result;
    }

}
