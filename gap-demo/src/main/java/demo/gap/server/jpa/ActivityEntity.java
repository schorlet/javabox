package demo.gap.server.jpa;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Range;

/**
 * ActivityEntity
 */
@Entity
class ActivityEntity {
    @Id
    String id;

    @ManyToOne(optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    GapEntity gap;

    @ManyToOne(optional = false, cascade = { CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    UserEntity user;

    @Basic(optional = false)
    @Temporal(TemporalType.DATE)
    Date day;

    @Basic(optional = false)
    @Range(min = 0, max = 1)
    Float time;

    ActivityEntity() {}

    ActivityEntity(final String id, final GapEntity gap, final UserEntity user, final Date day,
        final Float time) {
        assert id != null : "id must not be null";
        assert gap != null : "gap must not be null";
        assert user != null : "user must not be null";
        assert day != null : "day must not be null";
        assert time != null : "time must not be null";

        this.id = id;
        this.gap = gap;
        this.user = user;
        this.day = day;
        this.time = time;
    }

    String getId() {
        return id;
    }

    GapEntity getGap() {
        return gap;
    }

    void setGap(final GapEntity gap) {
        this.gap = gap;
    }

    UserEntity getUser() {
        return user;
    }

    void setUser(final UserEntity user) {
        assert user != null : "user must not be null";
        this.user = user;
    }

    Date getDay() {
        return day;
    }

    void setDay(final Date day) {
        assert day != null : "day must not be null";
        this.day = day;
    }

    Float getTime() {
        return time;
    }

    void setTime(final Float time) {
        assert time != null : "time must not be null";
        this.time = time;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ActivityEntity other = (ActivityEntity) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Activity [ id=").append(id).append(",  user=").append(user)
            .append(", day=").append(day).append(", time=").append(time).append("]");
        return builder.toString();
    }

}
