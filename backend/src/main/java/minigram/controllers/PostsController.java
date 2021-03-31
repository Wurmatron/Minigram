package minigram.controllers;

import com.google.gson.JsonSyntaxException;
import io.javalin.core.validation.Validator;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import minigram.models.Account;
import minigram.models.Post;

import java.util.List;
import java.util.Map;

import static minigram.MiniGram.GSON;
import static minigram.utils.HttpUtils.*;

public class PostsController {

    @OpenApi(
            summary = "Get Posts",
            description = "Get Posts",
            queryParams = {@OpenApiParam(name = "accountID",description = "Filters posts by account")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Get posts", content = @OpenApiContent(from = Post[].class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
            },
            tags = {"Posts"}
    )
    public static Handler fetchPosts = ctx -> {
        List<Post> posts = Post.getPosts();
        // Remove Non Account ID Posts if there is accountID in the query param
        String accountID = ctx.queryParam("accountID");
        if (accountID != null && !accountID.isEmpty()) {
            for (int index = 0; index < posts.size(); index++) {
                if (!posts.get(index).posted_by_id.equals(accountID)) {
                    posts.remove(index);
                }
            }
        }
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(posts.toArray(new Post[0]))));
    };
    @OpenApi(
            summary = "Get Post by ID",
            description = "Get Post by ID",
            pathParams = {@OpenApiParam(name = "id", required = true, description = "Post ID")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Get posts", content = @OpenApiContent(from = Post.class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
                    @OpenApiResponse(status = "404", description = "Post not found"),
            },
            tags = {"Posts"}
    )
    public static Handler fetchPost = ctx -> {
        Post post = Post.getPostById(ctx.pathParam("id"));
        if (post == null) {
            ctx.contentType("application/json").status(404).result(responseMessage("Post Not Found"));
            return;
        }
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(post)));
    };

    @OpenApi(
            summary = "Update post",
            description = "Update post",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Account.class)),
            pathParams = {@OpenApiParam(name = "id", required = true, description = "Post ID")},
            responses = {
                    @OpenApiResponse(status = "201", description = "Post Found, Requested data is returned"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "404", description = "Post not found"),
                    @OpenApiResponse(status = "422", description = "Post ID and Path don't match"),
            },
            tags = {"Posts"}
    )
    public static Handler updatePost = ctx -> {
        String id = ctx.pathParam("id");
        Post post = GSON.fromJson(ctx.body(), Post.class);
        if (!post.id.equals(id)) {
            ctx.contentType("application/json").status(422).result(responseData("Post ID and Path don't match (" + id + ", " + post.id + ")"));
            return;
        }
        if (Post.updatePost(post)) {
            FeedController.propagateUpdate(post);
            ctx.contentType("application/json").status(201).result(GSON.toJson(post));
        } else {
            ctx.contentType("application/json").status(404).result(responseMessage("Updating Account failed"));
        }
    };


    @OpenApi(
            summary = "Delete post",
            description = "Delete post",
            responses = {
                    @OpenApiResponse(status = "201", description = "Delete Post", content = @OpenApiContent(from = Post.class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
                    @OpenApiResponse(status = "404", description = "Post not found"),
            },
            tags = {"Posts"}
    )
    public static Handler deletePost = ctx -> {
        String id = ctx.pathParam("id");

        Post post = Post.getPostById(id);

        if (post == null) {
            ctx.contentType("application/json").status(404).result(responseMessage("Post Not Found"));
            return;
        }

        Boolean postDeleted = Post.deletePost(id);

        if (postDeleted) {
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(post)));
        }
    };

    @OpenApi(
            summary = "Create a new post",
            description = "Create a new post",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Post.class)),
            responses = {
                    @OpenApiResponse(status = "201", description = "Post has been created", content = @OpenApiContent(from = Post.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "422", description = "Invalid Json, No Account, No Data"),
            },
            tags = {"Posts"}
    )
    public static Handler createPost = ctx -> {
//        validate
        Validator<Post> comment = ctx.bodyValidator(Post.class)
                .check(obj -> obj.text != null, "text should not be null")
                .check(obj -> obj.image != null, "image should not be null")
                .check(obj -> obj.text.length() <= 255, "comment text length should at least be between 1 and 255 characters long")
                .check(obj -> obj.likes_ids.length - 1 <= 0, "likes_ids should be empty")
                .check(obj -> obj.comments_ids.length - 1 <= 0, "comments_ids should be empty")
                .check(obj -> Integer.parseInt(obj.posted_by_id) > 0, "posted_by_id should be greater than 0")
                .check(obj -> Account.getAccountById(obj.posted_by_id) != null, "posted by account does not exist");

//        Merges all errors from all validators in the list. Empty map if no errors exist.
        Map<String, List<String>> errors = Validator.collectErrors(comment);

//        return validation errors if there is any
        if (!errors.isEmpty()) {
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

        Post post = GSON.fromJson(ctx.body(), Post.class);

        try {

            if (Post.create(post)) {
                ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(post)));
            }

        } catch (JsonSyntaxException e) {
            ctx.contentType("application/json").status(422).result(responseMessage(e.getMessage()));
        }
    };
}
