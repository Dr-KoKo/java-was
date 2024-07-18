package codesquad.application.handler;

import codesquad.application.model.Article;

import java.util.List;
import java.util.Optional;

public class MockArticleDao implements ArticleDao {
    private Article article;
    private int countSave = 0;
    private int countFindByUserId = 0;

    public int getCountSave() {
        return countSave;
    }

    public int getCountFindByUserId() {
        return countFindByUserId;
    }

    public void stub(Article article) {
        this.article = article;
    }

    @Override
    public void save(Article article) {
        countSave++;
    }

    @Override
    public Optional<Article> findById(String id) {
        countFindByUserId++;
        return Optional.ofNullable(article);
    }

    @Override
    public List<Article> findAllArticle() {
        return List.of();
    }
}