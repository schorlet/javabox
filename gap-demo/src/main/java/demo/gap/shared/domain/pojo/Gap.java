package demo.gap.shared.domain.pojo;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.jersey.server.linking.Ref;
import com.sun.jersey.server.linking.Ref.Style;

import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;

/**
 * Gap
 */
@XmlRootElement(name = "gap")
public class Gap implements ActivityService, Iterable<Activity> {
    private String id;
    private String version;
    private String description;
    private final Set<Activity> activities;

    @Ref(value = "gap/${instance.id}", style = Style.ABSOLUTE_PATH)
    URI link;

    @XmlElement
    public URI getLink() {
        return link;
    }

    public Gap(final String id, final String version, final String description) {
        assert id != null : "id must not be null";
        this.id = id;
        this.version = version;
        this.description = description;
        this.activities = new LinkedHashSet<Activity>();
    }

    public Gap(final String id, final String version, final String description,
        final Set<Activity> activities) {

        this(id, version, description);
        this.addAll(activities);
    }

    Gap() {
        this.activities = new LinkedHashSet<Activity>();
    }

    @XmlID
    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    @XmlAttribute(required = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @XmlAttribute
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
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
        final Gap other = (Gap) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Gap [id=").append(id).append(", version=").append(version).append("]");
        return builder.toString();
    }

    @Override
    public Iterator<Activity> iterator() {
        return activities.iterator();
    }

    @Override
    @XmlIDREF
    @XmlElement
    public Set<Activity> getActivities() {
        return new HashSet<Activity>(activities);
    }

    @Override
    public boolean isEmpty() {
        return activities.isEmpty();
    }

    @Override
    public Activity getById(final String id) {
        final Set<Activity> activities = getByFilter(new Filter().byId(id));

        if (activities.isEmpty()) return null;
        else {
            assert activities.size() == 1 : "getById should return only one Activity";
            return activities.iterator().next();
        }
    }

    @Override
    public Set<Activity> getByGapId(final String gapid) {
        final Set<Activity> copy = new HashSet<Activity>();
        if (id.equals(gapid)) {
            copy.addAll(activities);
        }
        return copy;
    }

    @Override
    public Set<Activity> getByFilter(final Filter filter) {
        final Set<Activity> copy = new HashSet<Activity>();

        for (final Activity activity : activities) {
            final boolean add = filter.apply(activity);

            if (add) {
                copy.add(activity);
            }
        }

        return copy;
    }

    @Override
    public void addAll(final Set<Activity> activities) {
        for (final Activity activity : activities) {
            add(activity);
        }
    }

    @Override
    public void add(final Activity activity) {
        activity.setGap(this);
        activities.add(activity);
    }

    @Override
    public boolean remove(final Activity activity) {
        return activities.remove(activity);
    }

    @Override
    public void clear() {
        activities.clear();
    }

}
