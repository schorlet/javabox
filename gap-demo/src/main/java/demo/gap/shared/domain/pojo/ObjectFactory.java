package demo.gap.shared.domain.pojo;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
class ObjectFactory {

    public Gap createGap() {
        return new Gap();
    }

    public Activity createActivity() {
        return new Activity();
    }

    public User createUser() {
        return new User();
    }
}
