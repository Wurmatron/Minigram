package minigram.controllers;

import io.javalin.core.validation.Validator;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import minigram.models.Account;
import minigram.models.Post;
import minigram.utils.EncryptionUtils;

import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.dbManager;
import static minigram.utils.HttpUtils.*;
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

        Validator<Account> new_account = ctx.bodyValidator(Account.class)
                .check(obj -> obj.email != null && !sanitize(Objects.requireNonNull(obj.email)).isEmpty(), "email should not be empty")
                .check(obj -> EMAIL_REGEX.matcher(obj.email).find(), "this email is invalid")
                .check(obj -> Account.getAccountByEmail(obj.email) == null, "an Account with that email exists already")
                .check(obj -> obj.password_hash != null && !Objects.requireNonNull(obj.password_hash).isEmpty(), "password should not be empty")
                .check(obj -> obj.following_ids.length-1 <= 0 , "following_ids should be empty")
                .check(obj -> obj.name != null && !sanitize(Objects.requireNonNull(obj.name)).isEmpty(), "name should not be empty")
                .check(obj -> Account.getAccountById(obj.name) == null, "an Account with that name exists already");

//        Merges all errors from all validators in the list. Empty map if no errors exist.
        Map<String, List<String>> errors = Validator.collectErrors(new_account);

//        check if email exists already
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

        String[] hash = EncryptionUtils.hash(account.password_hash);
        account.password_hash = hash[0];
        account.password_salt = hash[1];
//
        if (Account.create(account)){
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(account)));
        } else {
            ctx.contentType("application/json").status(422).result(responseMessage("Something went wrong, please try again!"));
        }
    };

    @OpenApi(
            summary = "Get account by id",
            description = "Get account by id",
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "200", description = "User Found, Requested data is returned", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "422", description = "Validation errors"),
                    @OpenApiResponse(status = "404", description = "Account not found"),
            },
            tags = {"User"}
    )
    public static Handler fetchAccount = ctx -> {

//        validate
        Validator<Integer> stringValidator = ctx.pathParam("id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0.")
                .check(n -> Account.getAccountById(n.toString()) != null, "Account Not Found");
//        collect errors
        Map<String, List<String>> errors = stringValidator.errors();

//        return validation errors if there is any
          if (!errors.isEmpty()){
              ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
              return;
          }

        String id = ctx.pathParam("id");

        Account account = Account.getAccountById(id);

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
        for(Account account : accounts) {
            account.password_hash = "";
            account.password_salt = "";
        }
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
                    @OpenApiResponse(status = "422", description = "Validation errors"),
            },
            tags = {"User"}
    )
    public static Handler updateAccount = ctx -> {
        //        validate
        Validator<Integer> stringValidator = ctx.pathParam("id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0.")
                .check(n -> Account.getAccountById(n.toString()) != null, "Account Not Found");

//        collect errors
        Map<String, List<String>> errors = stringValidator.errors();

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

        String id = ctx.pathParam("id");

        Account account = GSON.fromJson(ctx.body(), Account.class);

        if(!account.id.equals(id)) {
            ctx.contentType("application/json").status(422).result(responseData("Account ID and Path don't match (" + id + ", " + account.id + ")"));
            return;
        }
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
                    @OpenApiResponse(status = "422", description = "Validation errors"),
                    @OpenApiResponse(status = "404", description = "Account not found"),
            },
            tags = {"User"}
    )
    public static Handler deleteAccount = ctx -> {

//        validate
        Validator<Integer> stringValidator = ctx.pathParam("id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0.")
                .check(n -> Account.getAccountById(n.toString()) != null, "Account Not Found");

//        collect errors
        Map<String, List<String>> errors = stringValidator.errors();

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

        String id = ctx.pathParam("id");

        Account account = Account.getAccountById(id);

        if (Account.delete(id)){
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
