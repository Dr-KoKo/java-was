package codesquad.application.handler;

import codesquad.application.model.User;

import java.util.List;
import java.util.Optional;

public class MockUserDao implements UserDao {
    private User user;
    private int countSave = 0;
    private int countFindByUserId = 0;

    public MockUserDao() {
    }

    @Override
    public void save(User user) {
        countSave++;
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        countFindByUserId++;
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return List.of(user);
    }

    public int getCountSave() {
        return countSave;
    }

    public int getCountFindByUserId() {
        return countFindByUserId;
    }

    public void stub(User user) {
        this.user = user;
    }
}
