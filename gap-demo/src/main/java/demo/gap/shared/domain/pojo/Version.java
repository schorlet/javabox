package demo.gap.shared.domain.pojo;

import javax.xml.bind.annotation.XmlElement;
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

    @XmlElement
    public String getVersion() {
        return version;
    }

    @Override
    public int compareTo(final Version o) {
        return version.compareTo(o.getVersion());
    }

}
