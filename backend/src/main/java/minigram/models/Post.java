package minigram.models;

public class Post {

    public String id;
    public String[] likes_ids;
    public String[] comments_ids;
    public String text;
    public String image;
    public String posted_by_id;

    public Post(String id, String[] likes_ids, String[] comments_ids, String text, String image, String posted_by_id) {
        this.id = id;
        this.likes_ids = likes_ids;
        this.comments_ids = comments_ids;
        this.text = text;
        this.image = image;
        this.posted_by_id = posted_by_id;
    }
}
