package demo.gap.shared.domain.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User
 */
@XmlRootElement(name = "user")
public class User {
    String user;

    String firstname;

    String lastname;

    User() {}

    public User(final String user) {
        assert user != null : "user must not be null";
        this.user = user;
    }

    public User(final String user, final String firstname, final String lastname) {
        this(user);
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @XmlAttribute(required = true)
    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    @XmlAttribute
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    @XmlAttribute
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
        final User other = (User) obj;
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
