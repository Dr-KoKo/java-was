package codesquad.infra;

import codesquad.application.handler.ArticleDao;
import codesquad.application.model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcArticleStorage implements ArticleDao {
    @Override
    public void save(Article article) {
        String sql = "insert into articles(id, title, author_id, content, image) values(?,?,?,?,?)";
        try (Connection connection = H2ConnectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, article.getId());
            statement.setString(2, article.getTitle());
            statement.setString(3, article.getAuthorId());
            statement.setString(4, article.getContent());
            statement.setBytes(5, article.getImage());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Article> findById(String id) {
        String sql = "select * from articles where id = ?";
        try (Connection connection = H2ConnectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Article article = new Article(resultSet.getString("id"), resultSet.getString("title"), resultSet.getString("author_id"), resultSet.getString("content"), resultSet.getBytes("image"));
                return Optional.of(article);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Article> findAllArticle() {
        String sql = "select * from articles";
        List<Article> articles = new ArrayList<>();
        try (Connection connection = H2ConnectionManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Article article = new Article(resultSet.getString("id"), resultSet.getString("title"), resultSet.getString("author_id"), resultSet.getString("content"), resultSet.getBytes("image"));
                articles.add(article);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return articles;
    }
}
