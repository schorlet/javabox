package demo.hello.shared;

import java.sql.Timestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * SuperEntity
 */
@MappedSuperclass
public abstract class SuperEntity {

    @Id
    @GeneratedValue
    protected Integer identifier;

    public Integer getId() {
        return identifier;
    }

    @Version
    protected Timestamp version;

    public Timestamp getVersion() {
        return version;
    }
}
