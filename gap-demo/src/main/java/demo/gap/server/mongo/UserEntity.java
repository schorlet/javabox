package demo.gap.server.mongo;

import javax.persistence.Id;

/**
 * UserEntity
 */
class UserEntity {
    @Id
    String user;

    String firstname;

    String lastname;

    UserEntity() {}

    UserEntity(final String user, final String firstname, final String lastname) {
        assert user != null : "user must not be null";
        this.user = user;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    UserEntity(final String user) {
        this(user, null, null);
    }

    String getUser() {
        return user;
    }

    String getFirstname() {
        return firstname;
    }

    void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    String getLastname() {
        return lastname;
    }

    void setLastname(final String lastname) {
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
