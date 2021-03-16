package minigram.controllers;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import minigram.models.Account;
import minigram.models.FeedEntry;

import static minigram.utils.HttpUtils.responseMessage;

public class FeedController {

    public static final int MAX_POST_COUNT = 50;

    @OpenApi(
            summary = "Get the sorted main feed (per user)",
            description = "Get the latest messages for a given user",
            responses = {
                    @OpenApiResponse(status = "200", description = "Has followers, A list of accounts is returned", content = @OpenApiContent(from = FeedEntry[].class)),
                    @OpenApiResponse(status = "422", description = "Invalid Start, End Param"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Token"),
            },
            queryParams = {
                    @OpenApiParam(name = "start", type = Integer.class, description = "Starting message"),
                    @OpenApiParam(name = "end", type = Integer.class, description = "Ending message"),
            },
            tags = {"Feed"}
    )
    public static Handler feed = ctx -> {
        String token = ctx.header("token");
        Account account = null;
        if (token != null && !token.isEmpty())
            account = AuthController.tokens.getOrDefault(token, null);
        if (account == null) {
            // Set query defaults
            int startPos = 0;
            int endPos = MAX_POST_COUNT;
            // Check for query params
            try {
                startPos = ctx.queryParam("start", Integer.class).get();
                endPos = ctx.queryParam("end", Integer.class).get();
            } catch (BadRequestResponse e) {
            } // Not an error
            // Validate start and end points
            if (startPos > endPos || endPos - startPos > MAX_POST_COUNT) {
                ctx.contentType("application/json").status(422).result(responseMessage("Invalid Query Param " + ((endPos - startPos > MAX_POST_COUNT)
                        ? "(Too many posts, You: " + (endPos - startPos) + ", Max: " + MAX_POST_COUNT + ")" : "")));
                return;
            }
            // TODO Implement
            ctx.contentType("application/json").status(201).result("{}");
        } else {
            ctx.contentType("application/json").status(401).result(responseMessage("No Account associated with the given token!"));
        }
    };
}
