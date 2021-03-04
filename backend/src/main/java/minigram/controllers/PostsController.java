package minigram.controllers;

import io.javalin.http.Handler;
import minigram.models.Account;
import minigram.models.Post;

import java.util.List;

import static minigram.MiniGram.GSON;
import static minigram.utils.HttpUtils.responseData;
import static minigram.utils.HttpUtils.responseMessage;

public class PostsController {

    public void PostController() {

    }

    public static Handler fetchPosts = ctx -> {
        List<Post> posts;
        posts = Post.getPosts();
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(posts.toArray(new Post[0]))));
    };

    public static Handler fetchPost = ctx -> {
        Post post = Post.getPostById(ctx.pathParam("id"));
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(post)));
    };

    //    TODO: Implement
    public static Handler updatePost = ctx -> {

    };

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
