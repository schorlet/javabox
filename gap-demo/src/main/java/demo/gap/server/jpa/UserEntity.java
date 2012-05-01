package demo.gap.server.jpa;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * UserEntity
 */
@Entity
@Cacheable
public class UserEntity {
    @Id
    String user;

    @Column(length = 20)
    String firstname;

    @Column(length = 40)
    String lastname;

    UserEntity() {}

    public UserEntity(final String user, final String firstname, final String lastname) {
        assert user != null : "user must not be null";
        this.user = user;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public UserEntity(final String user) {
        this(user, null, null);
    }

    public String getUser() {
        return user;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (user == null ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final UserEntity other = (UserEntity) obj;
        if (user == null) {
            if (other.user != null) return false;
        } else if (!user.equals(other.user)) return false;
        return true;
    }

    @Override
    public String toString() {
        return user;
    }

}
