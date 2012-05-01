package demo.gap.server.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * GapEntity
 */
@Entity
public class GapEntity {
    @Id
    String id;

    @Basic(optional = false)
    String version;

    @Basic(optional = false)
    @Column(length = 20)
    String description;

    GapEntity() {}

    public GapEntity(final String id, final String version) {
        this(id, version, null);
    }

    public GapEntity(final String id, final String version, final String description) {
        assert id != null : "id must not be null";
        assert version != null : "version must not be null";

        this.id = id;
        this.version = version;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        assert version != null : "version must not be null";
        this.version = version;
    }

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
        final GapEntity other = (GapEntity) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Gap [id=").append(id).append(", version=").append(version)
            .append(", description=").append(description).append("]");
        return builder.toString();
    }

}
