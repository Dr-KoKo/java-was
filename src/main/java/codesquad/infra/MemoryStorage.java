package codesquad.infra;

import codesquad.application.handler.UserDao;
import codesquad.application.model.User;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryStorage implements UserDao {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return Optional.ofNullable(users.get(userId));
    }
}
