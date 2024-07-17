package codesquad.application.handler;

import codesquad.application.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleDao {
    void save(Article article);

    Optional<Article> findById(String id);

    List<Article> findAllArticle();
}
