package minigram.controllers;

import io.javalin.http.Handler;
import minigram.MiniGram;
import minigram.models.Account;
import minigram.models.AccountWithToken;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class AuthController {

    // Token, Account
    public static NonBlockingHashMap<String, Account> tokens = new NonBlockingHashMap<>();

    public static Handler login = ctx -> {
        String data = ctx.body();
        Account account = MiniGram.GSON.fromJson(data, Account.class);
        Account dbAccount = Account.getAccountByName(account.name);
        if (dbAccount != null && account.password_hash.equals(dbAccount.password_hash)) {
            String token = Account.genToken(dbAccount);
            AccountWithToken accountWithToken = new AccountWithToken(token, dbAccount);
            tokens.put(token, dbAccount); // TODO Store in DB
            ctx.status(200).result(MiniGram.GSON.toJson(accountWithToken));
        }
        ctx.status(404).result("Account does not exit");
    };

    public static Handler logout = ctx -> {
        AccountWithToken account = MiniGram.GSON.fromJson(ctx.body(), AccountWithToken.class);
        if (tokens.containsKey(account.token)) {
            tokens.remove(account.token);
            account.token = "";
            ctx.status(200).result(MiniGram.GSON.toJson(account));
        }
        ctx.status(404).result("Token does not exist");

    };
}
