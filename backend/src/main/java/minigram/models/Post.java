package minigram.models;

import joptsimple.internal.Strings;
import minigram.controllers.FeedController;
import minigram.utils.SQLUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static minigram.MiniGram.dbManager;

public class Post extends BaseModel {

    public static final String[] POST_COLUMNS = new String[]{"id", "likes_ids", "comments_ids", "text", "image", "posted_by_id", "timestamp"};

    public String id;
    public String[] likes_ids;
    public String[] comments_ids;
    public String text;
    public String image;
    public String posted_by_id;
    public String timestamp;

    public Post() { }

    public Post(String id, String[] likes_ids, String[] comments_ids, String text, String image, String posted_by_id, String timestamp) {
        this.id = id;
        this.likes_ids = likes_ids;
        this.comments_ids = comments_ids;
        this.text = text;
        this.image = image;
        this.posted_by_id = posted_by_id;
        this.timestamp = timestamp;
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
                post.comments_ids = set.getString("comment_ids").split(", ");
                post.text = set.getString("text");
                post.image = set.getString("image");
                post.posted_by_id = set.getString("posted_id");
                post.timestamp = set.getString("timestamp");
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
                post.timestamp = set.getString("timestmap");
            } else  {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return post;
    }

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

    public static Boolean updatePost(Post post) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE posts SET ");
        for (String type : Post.POST_COLUMNS) {
            if (type.equalsIgnoreCase("id") || type.equalsIgnoreCase("posted_by_id"))
                continue;
            if(type.equalsIgnoreCase("likes_ids") || type.equalsIgnoreCase("comments_ids")) {
                String[] data = (String[]) post.getClass().getDeclaredField(type).get(post);
                if(data == null || data.length == 0)
                    continue;
            } else {
                String data = (String) post.getClass().getDeclaredField(type).get(post);
                if(data == null || data.isEmpty())
                    continue;
            }
            // TODO Test for leading / trailing , in String[] data types
            query.append("`%type%`='%value%', ".replaceAll("%type%", type).replaceAll("%value%", type.equalsIgnoreCase("likes_ids") ||  type.equalsIgnoreCase("comments_ids") ?
                    String.join(" ", (String[]) post.getClass().getDeclaredField(type).get(post)) :
                    (String) post.getClass().getDeclaredField(type).get(post)));
        }
        String type = query.toString();
        type = type.substring(0,type.length() - 2);
        query = new StringBuilder();
        query.append(type);
        query.append(" WHERE id='%id%';".replaceAll("%id%", post.id));
        try {
            Statement statement = dbManager.getConnection().createStatement();
            int set = statement.executeUpdate(query.toString());

            if (set >= 1){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Boolean create(Post post) {
        post.id = null;
        String query = "INSERT INTO posts (`likes_ids`,`comment_ids`, `text`, `image`, `posted_id`, `timestamp`) VALUES ('%likes_ids%','%comment_ids%', '%txt%', '%image%', '%posted_id%', '%TIMESTAMP%');"
                .replaceAll("%likes_ids%", post.likes_ids != null && post.likes_ids.length > 0 ? Strings.join(post.likes_ids, " "): "")
                .replaceAll("%comment_ids%", post.comments_ids != null && post.comments_ids.length > 0 ? Strings.join(post.comments_ids, " "): "")
                .replaceAll("%txt%", SQLUtils.sanitizeText(post.text))
                .replaceAll("%image%", post.image)
                .replaceAll("%posted_id%", post.posted_by_id)
                .replaceAll("%TIMESTAMP%", post.timestamp == null || post.timestamp.isEmpty() ? "" + Instant.now().getEpochSecond() : post.timestamp);
        Statement statement = null;
        try {
            statement = dbManager.getConnection().createStatement();
            statement.execute(query);

            FeedController.propagateUpdate(post);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
