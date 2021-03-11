package minigram.controllers;

import io.javalin.core.validation.Validator;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import minigram.models.Account;
import minigram.utils.CrudDataStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static minigram.MiniGram.GSON;
import static minigram.utils.HttpUtils.*;

public class FollowingsController extends BaseController{

    @OpenApi(
            summary = "Get an accounts' followers",
            description = "Get all the accounts that follow this account",
            pathParams = {@OpenApiParam(name = "id", required = true, description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Has followers, A list of accounts is returned", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "200", description = "No followers, An empty list is returned", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
            },
            tags = {"Followings"}
    )
    //    TODO: implement
    public static Handler fetchAccountFollowers = ctx -> {
        String id = ctx.pathParam("id");


    };

    @OpenApi(
            summary = "Get an accounts' followings",
            description = "Get all the accounts followed by this account",
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Has followers, A list of accounts is returned", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "200", description = "No followers, An empty list is returned", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
            },
            tags = {"Followings"}
    )
    public static Handler fetchAccountFollowing = ctx -> {
        String id = ctx.pathParam("id");

        Account account = Account.getAccountById(id);

        List<Account> accounts = new ArrayList<>();

        if (account != null) {
            for (String account_id: account.following_ids
            ) {
                accounts.add(Account.getAccountById(account_id));
            }
        }

        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(accounts)));
    };

    @OpenApi(
            summary = "Follow an account",
            description = "Follow this account",
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "201", description = "Adds user to the logged in user's list of followings", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "422", description = "Unprocessable entity, validation failed"),
            },
            tags = {"Followings"}
    )
    //    TODO: implement
    public static Handler followAccount = ctx -> {
        //        validate
        Validator<Integer> auth_validator = ctx.pathParam("auth_id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0")
                .check(n -> Account.getAccountById(n.toString()) != null, "Auth account does not exist");

        Validator<Integer> follow_validator = ctx.pathParam("follow_id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0")
                .check(n -> Account.getAccountById(n.toString()) != null, "Account does not exist");

// Merges all errors from all validators in the list. Empty map if no errors exist.
        Map<String, List<String>> errors = Validator.collectErrors(auth_validator, follow_validator);

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

//        add followed account to logged in user's array of followed users
        Account auth_account = Account.getAccountById(auth_validator.get().toString());

        CrudDataStructure following = null;
        if (auth_account != null) {
            following = new CrudDataStructure(auth_account.following_ids);
        }

        following.add(follow_validator.get().toString());

        auth_account.following_ids = following.arr.toArray(new String[0]);

//      follow/update account
        Account.updateAccount(auth_account);

        ctx.contentType("application/json").status(201).result(responseMessage("Account with id "+ follow_validator.get() + " followed"));

    };

    @OpenApi(
            summary = "Unfollow an account",
            description = "Unfollow this user account",
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Account ID")},
            responses = {
                    @OpenApiResponse(status = "201", description = "Removes user from user's list of followings", content = @OpenApiContent(from = Account.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "422", description = "Unprocessable entity, validation failed"),
            },
            tags = {"Followings"}
    )

    //    TODO: Test
    public static Handler unfollowAccount = ctx -> {

//        validate
        Validator<Integer> auth_validator = ctx.pathParam("auth_id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0")
                .check(n -> Account.getAccountById(n.toString()) != null, "Auth account does not exist")
                .check(n -> Account.getAccountById(n.toString()).following_ids.length-1 != 0, "Account has 0 followings");

        Validator<Integer> unfollow_validator = ctx.pathParam("follow_id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0")
                .check(n -> Account.getAccountById(n.toString()) != null, "Account does not exist");


        // Merges all errors from all validators in the list. Empty map if no errors exist.
        Map<String, List<String>> errors = Validator.collectErrors(auth_validator, unfollow_validator);

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

//      remove account account id from followings
        Account auth_account = Account.getAccountById(auth_validator.get().toString());

        if (auth_account != null){
            System.out.println("Before: "+ (auth_account.following_ids.length-1));
           for (int i = 0; i < auth_account.following_ids.length - 1; i++){
                if (auth_account.following_ids[i].equals(unfollow_validator.get().toString())){
                    removeElement(auth_account.following_ids, i);
                }
           }
        }

        System.out.println("After: "+ (auth_account.following_ids.length-1));

//      unfollow/update account
        Account.updateAccount(auth_account);

        ctx.contentType("application/json").status(201).result(responseMessage("Account with id "+ unfollow_validator.get() + " unfollowed"));
    };

}
