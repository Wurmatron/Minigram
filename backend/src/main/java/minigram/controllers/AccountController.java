package minigram.controllers;

import io.javalin.http.Handler;
import minigram.models.Account;
import minigram.utils.EncryptionUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.dbManager;
import static minigram.utils.SQLUtils.sanitize;

public class AccountController {

    public static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


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
                    .replaceAll("%profile_pic%", sanitize(account.profile_pic))
                    .replaceAll("%email%", sanitize(account.email))
                    .replaceAll("%password_hash%", sanitize(account.password_hash))
                    .replaceAll("%password_salt%", sanitize(account.password_salt))
                    .replaceAll("%following_ids%", account.following_ids != null && account.following_ids.length > 0 ? sanitize(String.join(", ", account.following_ids)) : "");
            Statement statement = dbManager.getConnection().createStatement();
            try {
                statement.execute(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ctx.status(201).result(GSON.toJson(account));
        } else {
            ctx.status(422).result("Invalid data");
        }
    };

    public static Handler fetchAccount = ctx -> {
        Account account = GSON.fromJson(ctx.body(), Account.class);
        String id = account.id;
        Statement statement = dbManager.getConnection().createStatement();
        String query = "SELECT * FROM accounts WHERE id='%id%' LIMIT 1;".replaceAll("%id%", id);
        try {
            ResultSet set = statement.executeQuery(query);
            set.next();
            account.id = set.getString("id");
            account.name = set.getString("name");
            account.following_ids = set.getString("following_ids").split(", ");
            account.email = set.getString("email");
            account.password_hash = set.getString("password_hash");
            account.password_salt = set.getString("password_salt");
            ctx.status(200).result(GSON.toJson(account));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.status(404).result("Account does not exist");
    };

    // TODO Implement
    public static Handler fetchAccounts = ctx -> {

    };

    // TODO Implement
    public static Handler updateAccount = ctx -> {

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
        Account acc = Account.getAccountByName(sanitize(account.name));
        if(acc == null)
            return false;
        return !account.password_hash.isEmpty() && !account.password_salt.isEmpty();
    }

}
