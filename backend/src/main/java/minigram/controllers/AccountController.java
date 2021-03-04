package minigram.controllers;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import minigram.models.Account;
import minigram.utils.EncryptionUtils;
import minigram.utils.SQLUtils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Pattern;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.dbManager;
import static minigram.utils.HttpUtils.responseData;
import static minigram.utils.HttpUtils.responseMessage;
import static minigram.utils.SQLUtils.sanitize;

public class AccountController {

    public static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private AccountController() {
    }

    @OpenApi(
            summary = "Register / Create a user account",
            description = "Register / Create a user account",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Account.class)),
            responses = {
                    @OpenApiResponse(status = "201", description = "User has been created", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "422", description = "Invalid Json, Account Exists"),
            },
            tags = {"User"}
    )
    public static Handler registerNewAccount = ctx -> {
        Account account = GSON.fromJson(ctx.body(), Account.class);
//        check if email exists already
        if (Account.getAccountByEmail(account.email) != null){
            ctx.contentType("application/json").status(422).result(responseMessage("An Account with that email exists already"));
            return;
        }

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
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(account)));
        } else {
            ctx.contentType("application/json").status(422).result(responseMessage("Invalid data"));
        }
    };

    @OpenApi(
            summary = "Get account by id",
            description = "Get account by id",
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "200", description = "User Found, Requested data is returned", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "404", description = "Account not found"),
            },
            tags = {"User"}
    )
    public static Handler fetchAccount = ctx -> {
        String id = ctx.pathParam("id");
        Account account = new Account();

        account = Account.getAccountById(id);

        if (account == null){
            ctx.contentType("application/json").status(404).result(responseMessage("Account Not Found"));
        }

        ctx.contentType("application/json").status(200).result(GSON.toJson(account));
    };

    @OpenApi(
            summary = "Get all accounts",
            description = "Get all accounts",
            responses = {
                    @OpenApiResponse(status = "200", description = "User Found, Requested data is returned",content = @OpenApiContent(from = Account[].class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
            },
            tags = {"User"}
    )
    public static Handler fetchAccounts = ctx -> {
        List<Account> accounts;
        accounts = Account.getAccounts();
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(accounts.toArray(new Account[0]))));
    };

    @OpenApi(
            summary = "Update account",
            description = "Update account",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Account.class)),
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "201", description = "User Found, Requested data is returned"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "404", description = "Account not found"),
            },
            tags = {"User"}
    )
    public static Handler updateAccount = ctx -> {
        String id = ctx.pathParam("id");
        Account account = GSON.fromJson(ctx.body(), Account.class);
        // TODO ID not used
        if (Account.updateAccount(account)) {
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(account)));
        } else {
            ctx.contentType("application/json").status(404).result(responseMessage("Updating Account failed"));
        }
    };

    @OpenApi(
            summary = "Delete account",
            description = "Delete account",
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "201", description = "User Deleted", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "404", description = "Account not found"),
            },
            tags = {"User"}
    )
    public static Handler deleteAccount = ctx -> {
        String id = ctx.pathParam("id");

        Account account = Account.getAccountById(id);

        Boolean accountDeleted = Account.delete(id);

        if (accountDeleted){
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(account)));
            return;
        }

        ctx.contentType("application/json").status(404).result(responseMessage("Account Not Found"));
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
