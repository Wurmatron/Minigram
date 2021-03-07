package minigram.controllers;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import minigram.models.Account;

import java.util.ArrayList;
import java.util.List;

import static minigram.MiniGram.GSON;
import static minigram.utils.HttpUtils.responseData;

public class FollowingsController {

    //    TODO: implement
    public static Handler fetchAccountFollowers = ctx -> {

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
            tags = {"User"}
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

    //    TODO: implement
    public static Handler followAccount = ctx -> {

    };

    //    TODO: implement
    public static Handler unfollowAccount = ctx -> {

    };
}
