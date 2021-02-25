package minigram.controllers;

import io.javalin.http.Handler;
import minigram.models.Account;
import minigram.utils.EncryptionUtils;
import minigram.utils.SQLUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
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
        Account account = new Account();

        account = Account.getAccountById(id);

        if (account == null){
            ctx.contentType("application/json").status(404).result("{\"message\": \"Account Not Found!\"}");
        }

        ctx.contentType("application/json").status(200).result(GSON.toJson(account));
    };


    public static Handler fetchAccounts = ctx -> {
        List<Account> accounts;
        accounts = Account.getAccounts();
        ctx.contentType("application/json").status(200).result(GSON.toJson(accounts.toArray(new Account[0])));
    };

    public static Handler updateAccount = ctx -> {
        Account account = GSON.fromJson(ctx.body(), Account.class);
        StringBuilder query = new StringBuilder();
        query.append("UPDATE accounts SET ");
        for (String type : Account.ACCOUNT_COLUMNS) {
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


    public static Handler deleteAccount = ctx -> {
        String id = ctx.pathParam("id");

        Account account = Account.getAccountById(id);

        Boolean accountDeleted = Account.delete(id);

        if (accountDeleted){
            ctx.contentType("application/json").status(201).result("{\"data\":"+ GSON.toJson(account) +"}");
        }

        ctx.contentType("application/json").status(404).result("{\"message\": \"Account Not Found!\"}");
    };

    private static boolean isValidAccount(Account account) {
        if (sanitize(account.name).isEmpty()) {
            return false;
        }
        if (sanitize(account.email).isEmpty() || !EMAIL_REGEX.matcher(account.email).find()) {
            return false;
        }
        try {
            Account acc = Account.getAccountById(account.name);
            if(acc.id == null) {
               return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !account.password_hash.isEmpty() && !account.password_salt.isEmpty();
    }
}
