package minigram.controllers;

import io.javalin.http.Handler;
import minigram.models.Account;
import minigram.utils.EncryptionUtils;

import java.sql.ResultSet;
import java.sql.Statement;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.controller;
import static minigram.utils.SQLUtils.santize;

public class AccountController {

    private AccountController(){ }

    public static Handler registerNewAccount = ctx -> {
        Account account = GSON.fromJson(ctx.body(), Account.class);
        String[] hash = EncryptionUtils.hash(account.password_hash);
        account.password_hash = hash[0];
        account.password_salt = hash[1];
        if (isValidAccount(account)) {
            String query = "INSERT INTO accounts (name, profile_pic, email, password_hash, password_salt, following_ids) VALUES ('%name%', '%profile_pic%', '%email%', '%password_hash%', '%password_salt%', '%following_ids%');"
                    .replaceAll("%name%", santize(account.name))
                    .replaceAll("%profile_pic%", santize(account.profile_pic))
                    .replaceAll("%email%", santize(account.email))
                    .replaceAll("%password_hash%", santize(account.password_hash))
                    .replaceAll("%password_salt%", santize(account.password_salt))
                    .replaceAll("%following_ids%", account.following_ids != null && account.following_ids.length > 0 ? santize(String.join(", ", account.following_ids)) : "");
            Statement statement = controller.getConnection().createStatement();
            try {
                statement.execute(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ctx.status(201);
        } else {
            ctx.status(422);
        }
    };

    public static Handler fetchAccount = ctx -> {
        Account account = GSON.fromJson(ctx.body(), Account.class);
        String id = account.id;
        Statement statement = controller.getConnection().createStatement();
        String query = "SELECT * FROM accounts WHERE id='%id%' LIMIT 1;".replaceAll("%id%", id);
        try {
            ResultSet set = statement.executeQuery(query);
            set.next();
            account.name = set.getString("name");
            account.name = set.getString("name");
            account.following_ids = set.getString("following_ids").split(", ");
            account.email = set.getString("email");
            account.password_hash = set.getString("password_hash");
            account.password_salt = set.getString("password_salt");
            ctx.result(GSON.toJson(account));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // TODO Implement
    private static boolean isValidAccount(Account account) {
        return true;
    }

}