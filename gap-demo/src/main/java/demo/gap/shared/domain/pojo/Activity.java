package demo.gap.shared.domain.pojo;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.sun.jersey.server.linking.Ref;
import com.sun.jersey.server.linking.Ref.Style;

/**
 * Activity
 */
@XmlRootElement(name = "activity")
public class Activity {
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    String id;
    Gap gap;
    String user;
    Date day;
    Float time;

    @Ref(value = "activity/${instance.id}", style = Style.ABSOLUTE_PATH)
    URI link;

    @XmlElement
    public URI getLink() {
        return link;
    }

    public Activity(final String id, final Gap gap) {
        assert id != null : "id must not be null";
        assert gap != null : "gap must not be null";
        this.id = id;
        this.gap = gap;
    }

    public Activity(final String id, final Gap gap, final String user, final Date day,
        final Float time) {

        this(id, gap);

        this.user = user;
        this.day = day;
        this.time = time;
    }

    Activity() {}

    @XmlID
    @XmlAttribute
    public String getId() {
        return id;
    }

    @XmlIDREF
    @XmlAttribute
    public Gap getGap() {
        return gap;
    }

    public void setGap(final Gap gap) {
        this.gap = gap;
    }

    @XmlAttribute
    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(DateAdapter.class)
    public Date getDay() {
        return day;
    }

    public void setDay(final Date day) {
        this.day = day;
    }

    public void setDay(final String day2) throws ParseException {
        this.day = format.parse(day2);
    }

    @XmlAttribute
    public Float getTime() {
        return time;
    }

    public void setTime(final Float time) {
        this.time = time;
    }

    public void setTime(final String time2) {
        this.time = Float.valueOf(time2);
    }

    @XmlAttribute
    public String getVersion() {
        return gap.getVersion();
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
        final Activity other = (Activity) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Activity [id=").append(id).append(", gap=").append(gap).append(", user=")
            .append(user).append(", day=").append(format.format(day)).append(", time=")
            .append(time).append("]");
        return builder.toString();
    }

}
