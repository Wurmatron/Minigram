package minigram.models;

import minigram.utils.wrapper.IModel;

public class Post implements IModel {

    public String id;
    public String Text;
    public String Commented_by_id;

    public Post(String id, String Text, String Commented_by_id) {
        this.id = id;
        this.Commented_by_id = Commented_by_id;
        this.Text = Text;
    }
}
