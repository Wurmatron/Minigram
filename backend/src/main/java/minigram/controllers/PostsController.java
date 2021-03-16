package minigram.controllers;

import com.google.gson.JsonSyntaxException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import joptsimple.internal.Strings;
import minigram.models.Account;
import minigram.models.Post;
import minigram.utils.SQLUtils;

import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.dbManager;
import static minigram.utils.HttpUtils.responseData;
import static minigram.utils.HttpUtils.responseMessage;

public class PostsController {

    @OpenApi(
            summary = "Get Posts",
            description = "Get Posts",
            responses = {
                    @OpenApiResponse(status = "200", description = "Get posts", content = @OpenApiContent(from = Post[].class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
            },
            tags = {"Posts"}
    )
    public static Handler fetchPosts = ctx -> {
        List<Post> posts;
        posts = Post.getPosts();
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
            pathParams = {@OpenApiParam(name = "id", required = true,description = "Post ID")},
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
        if(!post.id.equals(id)) {
            ctx.contentType("application/json").status(422).result(responseData("Post ID and Path don't match (" + id + ", " + post.id + ")"));
            return;
        }
        if (Post.updatePost(post)) {
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
        try {
            Post post = GSON.fromJson(ctx.body(), Post.class);
            post.id = null;
            post.text = SQLUtils.sanitizeText(post.text);
            Account account = Account.getAccountById(post.posted_by_id);
            if (account != null) {
                if (!post.text.isEmpty() || !post.image.isEmpty()) {
                    String query = "INSERT INTO posts (`likes_ids`,`comment_ids`, `text`, `image`, `posted_id`, `timestamp`) VALUES ('%likes_ids%','%comment_ids%', '%txt%', '%image%', '%posted_id%', '%TIMESTAMP%');"
                            .replaceAll("%likes_ids%", Strings.join(post.likes_ids, " "))
                            .replaceAll("%comment_ids%", Strings.join(post.comments_ids, " "))
                            .replaceAll("%txt%", post.text)
                            .replaceAll("%image%", post.image)
                            .replaceAll("%posted_id%", post.posted_by_id)
                            .replaceAll("%TIMESTAMP%", post.timestamp.isEmpty() ? "" + Instant.EPOCH.getEpochSecond() : post.timestamp);
                    Statement statement = dbManager.getConnection().createStatement();
                    try {
                        statement.execute(query);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ctx.contentType("application/json").status(201).result(GSON.toJson(post));
                    return;
                } else {
                    ctx.contentType("application/json").status(422).result(responseMessage("Post has no data!"));
                }
                ctx.contentType("application/json").status(201).result(GSON.toJson(account));
            } else {
                ctx.contentType("application/json").status(422).result(responseMessage("User does not exist!"));
            }
        } catch (JsonSyntaxException e) {
            ctx.contentType("application/json").status(422).result(responseMessage("Invalid Json!"));
        }
    };

//    TODO: Implement
    public static Handler fetchLatestFeeds = ctx -> {

    };
}
