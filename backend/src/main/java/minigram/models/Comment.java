package minigram.models;

import minigram.utils.wrapper.IModel;

public class Comment implements IModel {

    public String id;
    public String text;
    public String commented_by_id;

    public Comment(String id, String text, String commented_by_id) {
        this.id = id;
        this.commented_by_id = commented_by_id;
        this.text = text;
    }
}
