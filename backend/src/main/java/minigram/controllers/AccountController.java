package minigram.controllers;

import io.javalin.http.Handler;
import minigram.models.Account;
import minigram.utils.EncryptionUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.dbManager;
import static minigram.utils.SQLUtils.sanitize;

public class AccountController {

    public static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final String[] ACCOUNT_COLUMNS = new String[]{"id", "name", "profile_pic", "email", "password_hash", "password_salt", "following_ids"};

    private AccountController() {
    }

    public static Handler registerNewAccount = ctx -> {
        Account account = GSON.fromJson(ctx.body(), Account.class);
        String[] hash = EncryptionUtils.hash(account.password_hash);
        account.password_hash = hash[0];
        account.password_salt = hash[1];
        if (isValidAccount(account)) {
            String query = "INSERT INTO accounts (name, profile_pic, email, password_hash, password_salt, following_ids) VALUES ('%name%', '%profile_pic%', '%email%', '%password_hash%', '%password_salt%', '%following_ids%');"
                    .replaceAll("%name%", sanitize(account.name))
                    .replaceAll("%profile_pic%", account.profile_pic)
                    .replaceAll("%email%", account.email)
                    .replaceAll("%password_hash%", account.password_hash)
                    .replaceAll("%password_salt%", account.password_salt)
                    .replaceAll("%following_ids%", account.following_ids != null && account.following_ids.length > 0 ? sanitize(String.join(", ", account.following_ids)) : "");
            Statement statement = dbManager.getConnection().createStatement();
            try {
                statement.execute(query);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ctx.contentType("application/json").status(201).result(GSON.toJson(account));
        } else {
            ctx.contentType("application/json").status(422).result("{\"message\": \"Invalid data\"}");
        }
    };

    public static Handler fetchAccount = ctx -> {
        String id = ctx.pathParam("id");
        String query = "";
        if (isNum(id)) {
            query = "SELECT * FROM accounts WHERE id='%id%' LIMIT 1;".replaceAll("%id%", id);
        } else {
            query = "SELECT * FROM accounts WHERE name='%name%' LIMIT 1;".replaceAll("%name%", sanitize(id));
        }
        try {
            Statement statement = dbManager.getConnection().createStatement();
            Account account = new Account();
            ResultSet set = statement.executeQuery(query);
            set.next();
            account.id = set.getString("id");
            account.name = set.getString("name");
            account.following_ids = set.getString("following_ids").split(", ");
            account.email = set.getString("email");
            account.password_hash = set.getString("password_hash");
            account.password_salt = set.getString("password_salt");
            ctx.contentType("application/json").status(200).result(GSON.toJson(account));
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.contentType("application/json").status(404).result("{\"message\": \"Account Not Found!\"}");
    };

    private static boolean isNum(String id) {
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public static Handler fetchAccounts = ctx -> {
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
            ctx.contentType("application/json").status(200).result(GSON.toJson(accounts.toArray(new Account[0])));
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public static Handler updateAccount = ctx -> {
        Account account = GSON.fromJson(ctx.body(), Account.class);
        StringBuilder query = new StringBuilder();
        query.append("UPDATE accounts SET ");
        for (String type : ACCOUNT_COLUMNS) {
            if (type.equals("id"))
                continue;
            query.append("'%type%' = '%value%', ".replaceAll("%type%", type).replaceAll("%value%", type.equalsIgnoreCase("following_ids") ?
                    String.join(",", (String[]) account.getClass().getDeclaredField(type).get(account)) :
                    (String) account.getClass().getDeclaredField(type).get(account)));
        }
        query.append(" WHERE id='%id%';".replaceAll("%id", account.id));
        try {
            Statement statement = dbManager.getConnection().createStatement();
            ResultSet set = statement.executeQuery(query.toString());
            set.next();
            ctx.contentType("application/json").status(200).result(GSON.toJson(account));
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    // TODO Implement
    public static Handler deleteAccount = ctx -> {

    };

    private static boolean isValidAccount(Account account) {
        if (sanitize(account.name).isEmpty()) {
            return false;
        }
        if (sanitize(account.email).isEmpty() || !EMAIL_REGEX.matcher(account.email).find()) {
            return false;
        }
        try {
            Account acc = Account.getAccountByName(sanitize(account.name));
            if(acc == null) {
               return true;
            }
        } catch (Exception e) {
            // TODO
        }
        return !account.password_hash.isEmpty() && !account.password_salt.isEmpty();
    }
}
