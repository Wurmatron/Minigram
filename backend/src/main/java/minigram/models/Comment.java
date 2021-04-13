package minigram.models;

import minigram.utils.SQLUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static minigram.MiniGram.dbManager;

public class Comment extends BaseModel {

    public String id;
    public String text;
    public String commented_by_id;
    public String[] likes_ids;
    public String timestamp;

    public Comment(){}

    public Comment(String id, String text, String commented_by_id, String timestamp) {
        this.id = id;
        this.text = text;
        this.commented_by_id = commented_by_id;
        this.timestamp = timestamp;
    }

    public static List<Comment> getComments(){
        String query = "SELECT * FROM comments";
        List<Comment> comments = new ArrayList<>();
        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                Comment comment = new Comment();
                comment.id = set.getString("id");
                comment.commented_by_id = set.getString("commented_by_id");
                comment.text = set.getString("text");
                comment.text = set.getString("timestamp");
                comments.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static Comment getCommentById(String id){

        Comment comment = new Comment();
        String query  = "SELECT * FROM comments WHERE id='%id%' LIMIT 1;".replaceAll("%id%", SQLUtils.sanitize(id));

        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);
            set.next();
            comment.id = set.getString("id");
            comment.commented_by_id = set.getString("commented_by_id");
            comment.text = set.getString("text");
            return comment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean deleteComment(String id){
        String query  = "DELETE FROM comments WHERE id='%id%' LIMIT 1;".replaceAll("%id%", SQLUtils.sanitize(id));

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
