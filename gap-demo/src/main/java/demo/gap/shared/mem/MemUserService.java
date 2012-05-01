package demo.gap.shared.mem;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.gap.shared.domain.pojo.User;
import demo.gap.shared.domain.service.UserService;

public class MemUserService implements UserService {
    static final Logger logger = LoggerFactory.getLogger(MemUserService.class);

    private final Set<User> users = new HashSet<User>();

    @Override
    public boolean isEmpty() {
        logger.trace("isEmpty");
        return users.isEmpty();
    }

    @Override
    public void addAll(final Set<User> set) {
        logger.trace("addAll {}", set);
        users.addAll(set);
    }

    @Override
    public void clear() {
        logger.trace("clear");
        users.clear();
    }

    @Override
    public Set<User> getUsers() {
        logger.trace("getUsers");
        return new HashSet<User>(users);
    }
}
