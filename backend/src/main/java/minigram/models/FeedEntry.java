package minigram.models;

public class FeedEntry {

    public String postID;
    public String[] likes_ids;
    public Comment[] comments;
    public String text;
    public String image;
    public String posted_by_id;

    public FeedEntry(Post post, Comment[] comments) {
        this.postID = post.id;
        this.likes_ids = post.likes_ids;
        this.text = post.text;
        this.image = post.image;
        this.posted_by_id = post.posted_by_id;
        this.comments = comments;
    }

    public FeedEntry(String postID, String[] likes_ids, Comment[] comments, String text, String image, String posted_by_id) {
        this.postID = postID;
        this.likes_ids = likes_ids;
        this.comments = comments;
        this.text = text;
        this.image = image;
        this.posted_by_id = posted_by_id;
    }
}
