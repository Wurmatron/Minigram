package minigram.models;

import minigram.utils.SQLUtils;
import minigram.utils.wrapper.IModel;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static minigram.MiniGram.dbManager;

public class Comment extends BaseModel {

    public String id;
    public String text;
    public String commented_by_id;

    public Comment(){}

    public Comment(String id, String text, String commented_by_id) {
        this.id = id;
        this.commented_by_id = commented_by_id;
        this.text = text;
    }

//    TODO: Test
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
                comments.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comments;
    }

//    TODO: Test
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


}
