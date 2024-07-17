package codesquad.infra;

import codesquad.application.handler.ArticleDao;
import codesquad.application.model.Article;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryArticleStorage implements ArticleDao {
    private final ConcurrentHashMap<String, Article> articles = new ConcurrentHashMap<>();

    @Override
    public void save(Article article) {
        articles.put(article.getId(), article);
    }

    @Override
    public Optional<Article> findById(String id) {
        return Optional.ofNullable(articles.get(id));
    }

    @Override
    public List<Article> findAllArticle() {
        return articles.values().stream().toList();
    }
}
