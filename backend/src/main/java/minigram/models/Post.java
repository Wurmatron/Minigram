package minigram.models;

import minigram.utils.SQLUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static minigram.MiniGram.dbManager;

public class Post extends BaseModel {

    public static final String[] POST_COLUMNS = new String[]{"id", "likes_ids", "comments_ids", "text", "image", "posted_by_id"};

    public String id;
    public String[] likes_ids;
    public String[] comments_ids;
    public String text;
    public String image;
    public String posted_by_id;

    public Post() { }

    public Post(String id, String[] likes_ids, String[] comments_ids, String text, String image, String posted_by_id) {
        this.id = id;
        this.likes_ids = likes_ids;
        this.comments_ids = comments_ids;
        this.text = text;
        this.image = image;
        this.posted_by_id = posted_by_id;
    }


    public static List<Post> getPosts(){
        String query = "SELECT * FROM posts";
        List<Post> posts = new ArrayList<>();
        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                Post post = new Post();
                post.id = set.getString("id");
                post.likes_ids = set.getString("likes_ids").split(", ");
                post.comments_ids = set.getString("comments_id").split(", ");
                post.text = set.getString("text");
                post.image = set.getString("image");
                post.posted_by_id = set.getString("posted_by_id");
                posts.add(post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    public static Post getPostById(String id){

        Post post = new Post();
        String query  = "SELECT * FROM posts WHERE id='%id%' LIMIT 1;".replaceAll("%id%", SQLUtils.sanitize(id));

        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);

            if (set.next()){
                post.id = set.getString("id");
                post.likes_ids = set.getString("likes_ids").split(", ");
                post.comments_ids = set.getString("comments_id").split(", ");
                post.text = set.getString("text");
                post.image = set.getString("image");
                post.posted_by_id = set.getString("posted_by_id");
            } else  {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return post;
    }

//    TODO: Test
    public static Boolean deletePost(String id){
        String query  = "DELETE FROM posts WHERE id='%id%' LIMIT 1;".replaceAll("%id%", SQLUtils.sanitize(id));

        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);
            set.next();

//          check if query was successful
            if (set.rowDeleted()){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
