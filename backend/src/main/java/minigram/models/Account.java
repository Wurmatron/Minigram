package minigram.models;

import minigram.utils.EncryptionUtils;
import minigram.utils.SQLUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static minigram.MiniGram.dbManager;
import static minigram.utils.SQLUtils.sanitize;


public class Account extends BaseModel {

    public static final String[] ACCOUNT_COLUMNS = new String[]{"id", "name", "profile_pic", "email", "password_hash", "password_salt", "following_ids"};

    public String id;
    public String name;
    public String profile_pic;
    public String email;
    public String password_hash;
    public String password_salt;
    public String[] following_ids;

    public Account() {
    }

    public Account(String id, String name, String profile_pic, String email, String password_hash, String password_salt, String[] following_ids) {
        this.id = id;
        this.name = name;
        this.profile_pic = profile_pic;
        this.email = email;
        this.password_hash = password_hash;
        this.password_salt = password_salt;
        this.following_ids = following_ids;
    }

    public static Account getAccountById(String id) {
        String query = "";
        Account account = new Account();
        if (isNum(id)) {
            query = "SELECT * FROM accounts WHERE id='%id%' LIMIT 1;".replaceAll("%id%", id);
        } else {
            query = "SELECT * FROM accounts WHERE name='%name%' LIMIT 1;".replaceAll("%name%", SQLUtils.sanitize(id));
        }
        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);

            if (!set.next()) {
                return null;
            }

            account.id = set.getString("id");
            account.name = set.getString("name");
            account.following_ids = set.getString("following_ids").split(", ");
            account.email = set.getString("email");
            account.password_hash = set.getString("password_hash");
            account.password_salt = set.getString("password_salt");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    public static Account getAccountByEmail(String email) {
        String query = "";
        Account account = new Account();

        query = "SELECT * FROM accounts WHERE email='%email%' LIMIT 1;".replaceAll("%email%", SQLUtils.sanitize(email));

        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);

            if (!set.next()) {
                return null;
            }

            account.id = set.getString("id");
            account.name = set.getString("name");
            account.following_ids = set.getString("following_ids").split(", ");
            account.email = set.getString("email");
            account.password_hash = set.getString("password_hash");
            account.password_salt = set.getString("password_salt");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return account;
    }

    public static List<Account> getAccounts() {
        String query = "SELECT * FROM accounts";
        List<Account> accounts = new ArrayList<>();
        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                Account account = new Account();
                account.id = set.getString("id");
                account.name = set.getString("name");
                account.following_ids = set.getString("following_ids").split(", ");
                account.email = set.getString("email");
                account.password_hash = set.getString("password_hash");
                account.password_salt = set.getString("password_salt");
                accounts.add(account);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public static Boolean updateAccount(Account account) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE accounts SET ");
        for (String type : Account.ACCOUNT_COLUMNS) {
            if (type.equals("id"))
                continue;
            try {
                String data = "";
                if (type.equalsIgnoreCase("following_ids")) {
                    String[] dataType = (String[]) account.getClass().getDeclaredField(type).get(account);
                    List<String> temp = new ArrayList<>();
                    Collections.addAll(temp, dataType);
                    List<String> temp2 = new ArrayList<>();
                    for (String t : temp) {
                        temp2.add(t.trim().replaceAll(",", ""));
                    }
                    if (temp2.size() != 0)
                        data = String.join(",", temp2.toArray(new String[0]));
                } else {
                    data = (String) account.getClass().getDeclaredField(type).get(account);
                }
                // Make sure its not null or empty
                if (data == null || data.isEmpty()) {
                    continue;
                }
                query.append("`%type%` = '%value%', ".replaceAll("%type%", type).replaceAll("%value%", data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String temp = query.toString();
        temp = temp.substring(0, temp.lastIndexOf(","));
        temp = temp + " WHERE id='%id%';".replaceAll("%id%", account.id);
        try {
            Statement statement = dbManager.getConnection().createStatement();
            int set = statement.executeUpdate(temp);

            if (set >= 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static Boolean delete(String id) {

        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM accounts WHERE id=%id%;".replaceAll("%id%", SQLUtils.sanitize(id)));
        try {
            Statement statement = dbManager.getConnection().createStatement();
            int set = statement.executeUpdate(query.toString());

            // check if query was successful
            if (set >= 1) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String genToken(Account account) {
        return new String(EncryptionUtils.generateSalt(64));
    }

    public static Boolean create(Account account) {

        try {
            String query = "INSERT INTO accounts (name, profile_pic, email, password_hash, password_salt, following_ids) VALUES ('%name%', '%profile_pic%', '%email%', '%password_hash%', '%password_salt%', '%following_ids%');"
                    .replaceAll("%name%", sanitize(account.name))
                    .replaceAll("%profile_pic%", account.profile_pic == null ? "" : account.profile_pic)
                    .replaceAll("%email%", account.email)
                    .replaceAll("%password_hash%", account.password_hash)
                    .replaceAll("%password_salt%", account.password_salt)
                    .replaceAll("%following_ids%", account.following_ids != null && account.following_ids.length > 0 ? sanitize(String.join(", ", account.following_ids)) : "");
            Statement statement = dbManager.getConnection().createStatement();
            statement.execute(query);

            return true;
        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
