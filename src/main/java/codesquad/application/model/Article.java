package codesquad.application.model;

public class Article {
    private String id;
    private String title;
    private String authorId;
    private String content;

    public Article(String id, String title, String authorId, String content) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }
}
