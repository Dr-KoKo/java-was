package codesquad.application.handler;

import codesquad.application.model.User;

import java.util.Optional;

public interface UserDao {
    void save(User user);

    Optional<User> findByUserId(String userId);
}