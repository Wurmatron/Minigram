package minigram.controllers;

import io.javalin.core.validation.Validator;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import minigram.models.Account;
import minigram.models.AuthService;
import minigram.utils.CrudDataStructure;

import java.util.*;

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
            description = "Follow a specific account",
            headers = {@OpenApiParam(name = "token", required = true, description = "Authentication Token")},
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
        Validator<String> token = ctx.header("token", String.class)
                .check(n -> AuthService.authAccount(n) != null, "Invalid Token")
                .check(n -> AuthService.authAccount(n) .following_ids.length-1 >= 0, "Account has 0 followings");

        Validator<Integer> friend_id = ctx.pathParam("follow_id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0")
                .check(n -> Account.getAccountById(n.toString()) != null, "Account does not exist")
//                TODO: Test
                .check(n -> (new CrudDataStructure(AuthService.authAccount(token.getValue()).following_ids).search(n.toString())) >= 0 , "This Account is followed already");

//        Merges all errors from all validators in the list. Empty map if no errors exist.
        Map<String, List<String>> errors = Validator.collectErrors(token, friend_id);

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

//        add followed account to logged in user's array of followed users
        Account authAccount = AuthService.authAccount(ctx.header("token"));

        CrudDataStructure following = null;
        if (authAccount != null) {
            following = new CrudDataStructure(authAccount.following_ids);
        }

        Objects.requireNonNull(following).add(Objects.requireNonNull(friend_id.getValue()).toString());

        Objects.requireNonNull(authAccount).following_ids = following.arr.toArray(new String[0]);

//      follow/update account
        Account.updateAccount(authAccount);

        ctx.contentType("application/json").status(201).result(responseMessage("Account with id "+ friend_id.getValue() + " followed"));

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
                .check(n -> Account.getAccountById(n.toString()).following_ids.length-1 >= 0, "Account has 0 followings");

        Validator<Integer> unfollow_validator = ctx.pathParam("unfollow_id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0")
                .check(n -> Account.getAccountById(n.toString()) != null, "Account does not exist")
//                TODO: test
                .check(n -> (new CrudDataStructure(Account.getAccountById(auth_validator.getValue().toString()).following_ids)).search(n.toString()) > -1, "This Account does not follow me");


        // Merges all errors from all validators in the list. Empty map if no errors exist.
        Map<String, List<String>> errors = Validator.collectErrors(auth_validator, unfollow_validator);

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

//      remove account account id from followings
        Account auth_account = Account.getAccountById(auth_validator.getValue().toString());

        if (auth_account != null){
            System.out.println("Before: "+ (auth_account.following_ids.length-1));
           for (int i = 0; i < auth_account.following_ids.length - 1; i++){
                if (auth_account.following_ids[i].equals(unfollow_validator.getValue().toString())){
                    auth_account.following_ids = removeElement(auth_account.following_ids, i);
                    break;
                }
           }
        }

//      unfollow/update account
        if (Account.updateAccount(auth_account)){
            ctx.contentType("application/json").status(201).result(responseMessage("Account with id "+ unfollow_validator.getValue().toString() + " unfollowed"));
            return;
        };

        ctx.contentType("application/json").status(500).result(responseMessage("Something went wring, please try again"));
    };

}
