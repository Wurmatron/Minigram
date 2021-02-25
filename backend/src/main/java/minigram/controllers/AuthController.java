package minigram.controllers;

import io.javalin.http.Handler;
import minigram.MiniGram;
import minigram.models.Account;
import minigram.models.AccountWithToken;
import minigram.utils.EncryptionUtils;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.nio.charset.StandardCharsets;

import static minigram.MiniGram.GSON;
import static minigram.utils.HttpUtils.responseData;
import static minigram.utils.HttpUtils.responseMessage;

public class AuthController {

    // Token, Account
    public static NonBlockingHashMap<String, Account> tokens = new NonBlockingHashMap<>();

    public static Handler login = ctx -> {
        String data = ctx.body();
        Account account = MiniGram.GSON.fromJson(data, Account.class);
        Account dbAccount = Account.getAccountByEmail(account.email);
        if(dbAccount == null) {
            ctx.contentType("application/json").status(404).result(responseMessage("Account does not exit"));
            return;
        }
        String salt = dbAccount.password_salt;
        String inputPassword = EncryptionUtils.hash(account.password_hash, salt.getBytes(StandardCharsets.UTF_8));
        if (inputPassword.equals(dbAccount.password_hash)) {
            String token = Account.genToken(dbAccount);
            AccountWithToken accountWithToken = new AccountWithToken(token, dbAccount);
            tokens.put(token, dbAccount); // TODO Store in DB
            ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(accountWithToken)));
            return;
        }
        ctx.contentType("application/json").status(401).result(responseMessage("Invalid credentials"));
    };

    public static Handler logout = ctx -> {
        AccountWithToken account = MiniGram.GSON.fromJson(ctx.body(), AccountWithToken.class);
        if (tokens.containsKey(account.token)) {
            tokens.remove(account.token);
            account.token = "";
            ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(account)));
            return;
        }
        ctx.contentType("application/json").status(404).result(responseMessage("Token does not exist"));

    };
}
