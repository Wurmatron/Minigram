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
        Account dbAccount = AccountController.getAccountByName(account.name);
        if (dbAccount != null) {
            if (account.password_hash.equals(dbAccount.password_hash)) {
                String token = genToken(dbAccount);
                AccountWithToken accountWithToken = new AccountWithToken(token, dbAccount);
                tokens.put(token, dbAccount); // TODO Store in DB
                ctx.result(MiniGram.GSON.toJson(accountWithToken));
            }
        }
        ctx.status(404);
    };

    public static Handler logout = ctx -> {
        AccountWithToken account = MiniGram.GSON.fromJson(ctx.body(), AccountWithToken.class);
        if (tokens.containsKey(account.token)) {
            tokens.remove(account.token);
            account.token = "";
            ctx.result(MiniGram.GSON.toJson(account));
        } else {
            ctx.status(404);
        }
    };

    // TODO Token Generation
    public static String genToken(Account account) {
        return "randomToken";
    }
}
