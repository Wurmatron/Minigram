package minigram.controllers;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import minigram.models.Post;

import java.util.List;

import static minigram.MiniGram.GSON;
import static minigram.utils.HttpUtils.responseData;
import static minigram.utils.HttpUtils.responseMessage;

public class PostsController {

    public void PostController() {

    }

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
            pathParams = {@OpenApiParam(name = "id",required = true,description = "Post ID")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Get posts", content = @OpenApiContent(from = Post.class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
                    @OpenApiResponse(status = "404", description = "Post not found"),
            },
            tags = {"Posts"}
    )
    public static Handler fetchPost = ctx -> {
        Post post = Post.getPostById(ctx.pathParam("id"));
        // TODO Error, post == null?
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(post)));
    };

    //    TODO: Implement
    public static Handler updatePost = ctx -> {

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

        if (post == null){
            ctx.contentType("application/json").status(404).result(responseMessage("Post Not Found"));
            return;
        }

        Boolean postDeleted = Post.deletePost(id);

        if (postDeleted){
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(post)));
        }
    };

    //    TODO: Implement
    public static Handler createPost = ctx -> {

    };
}
