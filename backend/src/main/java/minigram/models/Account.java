package minigram.models;

import minigram.utils.wrapper.IModel;

public class Account implements IModel {

    public String id;
    public String name;
    public String profile_pic;
    public String email;
    public String password_hash;
    public String password_salt;
    public String[] following_ids;

    public Account(String id, String name, String profile_pic, String email, String password_hash, String password_salt, String[] following_ids) {
        this.id = id;
        this.name = name;
        this.profile_pic = profile_pic;
        this.email = email;
        this.password_hash = password_hash;
        this.password_salt = password_salt;
        this.following_ids = following_ids;
    }
}
