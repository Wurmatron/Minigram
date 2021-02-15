package minigram.models;

import minigram.utils.SQLUtils;
import minigram.utils.wrapper.IModel;

import java.sql.ResultSet;
import java.sql.Statement;

import static minigram.MiniGram.dbManager;

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


    public static Account getAccountByName(String name) {
        Account account = new Account("", name, "", "", "", "", new String[0]);
        String query = "SELECT * FROM accounts WHERE name='%name%' LIMIT 1;".replaceAll("%name%", SQLUtils.sanitize(name));
        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);
            set.next();
            account.name = set.getString("name");
            account.name = set.getString("name");
            account.following_ids = set.getString("following_ids").split(", ");
            account.email = set.getString("email");
            account.password_hash = set.getString("password_hash");
            account.password_salt = set.getString("password_salt");
            return account;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
