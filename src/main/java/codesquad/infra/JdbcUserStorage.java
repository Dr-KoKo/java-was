package codesquad.infra;

import codesquad.application.handler.UserDao;
import codesquad.application.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserStorage implements UserDao {
    @Override
    public void save(User user) {
        String sql = "insert into users (user_id, nickname, password) values (?,?,?)";
        try (Connection connection = H2ConnectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUserId());
            statement.setString(2, user.getNickname());
            statement.setString(3, user.getPassword());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        String sql = "select * from users where user_id = ?";
        try (Connection connection = H2ConnectionManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User(resultSet.getString("user_id"), resultSet.getString("password"), resultSet.getString("nickname"));
                return Optional.of(user);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from users";
        List<User> users = new ArrayList<>();
        try (Connection connection = H2ConnectionManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User(resultSet.getString("user_id"), resultSet.getString("password"), resultSet.getString("nickname"));
                users.add(user);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}
