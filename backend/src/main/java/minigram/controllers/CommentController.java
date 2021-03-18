package minigram.controllers;

import com.google.gson.JsonSyntaxException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import joptsimple.internal.Strings;
import minigram.models.Account;
import minigram.models.Comment;
import minigram.models.Post;
import minigram.utils.SQLUtils;

import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.dbManager;
import static minigram.utils.HttpUtils.responseData;
import static minigram.utils.HttpUtils.responseMessage;

public class CommentController {

    public CommentController() {

    }

    @OpenApi(
            summary = "Get Comments",
            description = "Get Comments",
            responses = {
                    @OpenApiResponse(status = "200", description = "Get comments", content = @OpenApiContent(from = Comment[].class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
            },
            tags = {"Comments"}
    )

//    TODO: Implement
    public static Handler  fetchPostComments = ctx -> {
        List<Comment> comments;
        comments = Comment.getComments();
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(comments.toArray(new Comment[0]))));
    };

    @OpenApi(
            summary = "Get Comment by ID",
            description = "Get Comment by ID",
            pathParams = {@OpenApiParam(name = "id", required = true, description = "Comment ID")},
            responses = {
                    @OpenApiResponse(status = "200", description = "Get comments", content = @OpenApiContent(from = Comment.class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
                    @OpenApiResponse(status = "404", description = "Comment not found"),
            },
            tags = {"Comments"}
    )

    public static Handler fetchPostComment = ctx -> {
        Comment comment = Comment.getCommentById(ctx.pathParam("id"));
        if (comment == null) {
            ctx.contentType("application/json").status(404).result(responseMessage("Comment Not Found"));
            return;
        }
        ctx.contentType("application/json").status(200).result(responseData(GSON.toJson(comment)));
    };

    @OpenApi(
            summary = "Delete comment",
            description = "Delete comment",
            responses = {
                    @OpenApiResponse(status = "201", description = "Delete Comment", content = @OpenApiContent(from = Comment.class)),
                    @OpenApiResponse(status = "401", description = "Invalid Session Key"),
                    @OpenApiResponse(status = "404", description = "Comment not found"),
            },
            tags = {"Comments"}
    )
    public static Handler deleteComment = ctx -> {
        String id = ctx.pathParam("id");

        Comment comment = Comment.getCommentById(id);

        if (comment == null) {
            ctx.contentType("application/json").status(404).result(responseMessage("Comment Not Found"));
            return;
        }

        Boolean commentDeleted = Comment.deleteComment(id);

        if (commentDeleted) {
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(comment)));
        }
    };

    @OpenApi(
            summary = "Create a new comment",
            description = "Create a new comment",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Comment.class)),
            responses = {
                    @OpenApiResponse(status = "201", description = "Comment has been created", content = @OpenApiContent(from = Comment.class)),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Session"),
                    @OpenApiResponse(status = "422", description = "Invalid Json, No Account, No Data"),
            },
            tags = {"Comments"}
    )
    public static Handler createComment = ctx -> {
        try {
            Comment comment = GSON.fromJson(ctx.body(), Comment.class);
            comment.id = null;
            comment.text = SQLUtils.sanitizeText(comment.text);
            Account account = Account.getAccountById(comment.commented_by_id);
            if (account != null) {
                if (!comment.text.isEmpty()) {
                    String query = "INSERT INTO comments (`text`, `commented_id`, `likes_ids`, `timestamp`) VALUES ('%txt%', '%commented_id%', '%likes_id%', '%TIMESTAMP%');"
                            .replaceAll("%txt%", comment.text)
                            .replaceAll("%commented_id%", comment.commented_by_id)
                            .replaceAll("%likes_ids%", comment.likes_ids != null && comment.likes_ids.length > 0 ? Strings.join(comment.likes_ids, " "): "")
                            .replaceAll("%TIMESTAMP%", comment.timestamp == null || comment.timestamp.isEmpty() ? "" + Instant.now().getEpochSecond() : comment.timestamp);
                    Statement statement = dbManager.getConnection().createStatement();
                    try {
                        statement.execute(query);
                        //FeedController.propagateUpdate(comment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ctx.contentType("application/json").status(201).result(GSON.toJson(comment));
                    return;
                } else {
                    ctx.contentType("application/json").status(422).result(responseMessage("Comment has no data!"));
                }
                ctx.contentType("application/json").status(201).result(GSON.toJson(account));
            } else {
                ctx.contentType("application/json").status(422).result(responseMessage("User does not exist!"));
            }
        } catch (JsonSyntaxException e) {
            ctx.contentType("application/json").status(422).result(responseMessage("Invalid Json!"));
        }
    };
}
