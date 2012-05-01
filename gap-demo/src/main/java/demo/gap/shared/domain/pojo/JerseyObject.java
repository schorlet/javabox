package demo.gap.shared.domain.pojo;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JerseyObject
 */
@XmlRootElement(name = "response")
public class JerseyObject {

    Set<Gap> gaps = null;

    Set<Activity> activities = null;

    Set<User> users = null;

    Set<Version> versions = null;

    public JerseyObject() {}

    public JerseyObject(final Set<Gap> gaps) {
        this.gaps = gaps;
    }

    public JerseyObject(final Set<Gap> gaps, final Set<Activity> activities) {
        this.gaps = gaps;
        this.activities = activities;
    }

    @XmlElement(nillable = true)
    public Set<Gap> getGaps() {
        return gaps;
    }

    public void setGaps(final Set<Gap> gaps) {
        this.gaps = gaps;
    }

    @XmlElement(nillable = true)
    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(final Set<Activity> activities) {
        this.activities = activities;
    }

    @XmlElement(nillable = true)
    public Set<User> getUsers() {
        return users;
    }

    public JerseyObject setUsers(final Set<User> users) {
        this.users = users;
        return this;
    }

    @XmlElement(nillable = true)
    public Set<Version> getVersions() {
        return versions;
    }

    public JerseyObject setVersions(final Set<String> versions) {
        this.versions = new TreeSet<Version>();

        for (final String version : versions) {
            this.versions.add(new Version(version));
        }

        return this;
    }

}
