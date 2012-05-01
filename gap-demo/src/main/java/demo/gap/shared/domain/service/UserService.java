package demo.gap.shared.domain.service;

import java.util.Set;

import demo.gap.shared.domain.pojo.User;

/**
 * UserService
 */
public interface UserService {

    /**
     * test users list
     */
    boolean isEmpty();

    /**
     * get all users
     */
    Set<User> getUsers();

    /**
     * addAll users
     * @param users
     */
    void addAll(final Set<User> users);

    /**
     * clear all users
     */
    void clear();

}
