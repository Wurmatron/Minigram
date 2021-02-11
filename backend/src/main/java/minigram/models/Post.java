package minigram.models;

import minigram.utils.wrapper.IModel;

public class Post implements IModel {

//    TODO Add all the fields
    public String id;
    public String[] Likes_ids;
    public String[] Comments_ids;
    public String Text;
    public String Image;
    public String Posted_by_id;

    public Post(String id, String[] Likes_ids, String[] Comments_ids, String Text, String Image, String Posted_by_id) {
        this.id = id;
        this.Likes_ids = Likes_ids;
        this.Comments_ids = Comments_ids;
        this.Text = Text;
        this.Image = Image;
        this.Posted_by_id = Posted_by_id;
    }


}
