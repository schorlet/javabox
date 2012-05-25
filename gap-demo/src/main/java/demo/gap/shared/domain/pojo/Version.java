package demo.gap.shared.domain.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Version
 */
@XmlRootElement(name = "version")
public class Version implements Comparable<Version> {
    String version;

    Version() {}

    public Version(final String version) {
        this.version = version;
    }

    @XmlAttribute(required = true)
    public String getVersion() {
        return version;
    }

    @Override
    public int compareTo(final Version o) {
        return version.compareTo(o.getVersion());
    }

}
