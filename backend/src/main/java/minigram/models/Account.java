package minigram.models;

import minigram.utils.wrapper.IModel;

public class Account implements IModel {

    public String id;
    public String name;
    public String email;
    public String password_hash;
    public String password_salt;

    public Account(String id, String name, String email, String password_hash, String password_salt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password_hash = password_hash;
        this.password_salt = password_salt;
    }
}
