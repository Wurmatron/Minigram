package minigram.controllers;

import com.google.gson.JsonSyntaxException;
import io.javalin.core.validation.Validator;
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
import java.util.Map;

import static minigram.MiniGram.GSON;
import static minigram.MiniGram.dbManager;
import static minigram.utils.HttpUtils.*;

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

//        validate
        Validator<Integer> id = ctx.pathParam("id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0");

//        collect errors
        Map<String, List<String>> errors = id.errors();

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

        Comment comment = Comment.getCommentById(id.getValue().toString());

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
    public static Handler deletePostComment = ctx -> {
//        String id = ctx.pathParam("id");

//        validate
        Validator<Integer> id = ctx.pathParam("id", Integer.class)
                .check(n -> n > 0, "id should be greater than 0");

        //        collect errors
        Map<String, List<String>> errors = id.errors();

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

        Comment comment = Comment.getCommentById(id.getValue().toString());

        if (comment == null) {
            ctx.contentType("application/json").status(404).result(responseMessage("Comment Not Found"));
            return;
        }

        Boolean commentDeleted = Comment.deleteComment(id.getValue().toString());

        if (commentDeleted) {
//            TODO: Remove comment id from array of referenced comments


//            respond with the deleted comment object
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
//        validate
        Validator<Comment> comment = ctx.bodyValidator(Comment.class)
                .check(obj -> obj.text.length() - 1 <= 255, "comment text should not be more than 255 characters long")
                .check(obj -> Integer.parseInt(obj.commented_by_id)  > 0, "id should be greater than 0")
                .check(obj -> Account.getAccountById(obj.commented_by_id) != null, "Account does not exist");

//        Merges all errors from all validators in the list. Empty map if no errors exist.
        Map<String, List<String>> errors = Validator.collectErrors(comment);

//        return validation errors if there is any
        if (!errors.isEmpty()){
            ctx.contentType("application/json").status(422).result(validationErrors(GSON.toJson(errors)));
            return;
        }

        try {
            Comment new_comment = comment.getValue();
            new_comment.text = SQLUtils.sanitizeText(new_comment.text);
            Account account = Account.getAccountById(new_comment.commented_by_id);

            String query = "INSERT INTO comments (`text`, `commented_id`, `likes_ids`, `timestamp`) VALUES ('%txt%', '%commented_by_id%', '%likes_id%', '%TIMESTAMP%');"
                    .replaceAll("%txt%", new_comment.text)
                    .replaceAll("%commented_by_id%", new_comment.commented_by_id)
                    .replaceAll("%likes_ids%", new_comment.likes_ids != null && new_comment.likes_ids.length > 0 ? Strings.join(new_comment.likes_ids, " "): "")
                    .replaceAll("%TIMESTAMP%", new_comment.timestamp == null || new_comment.timestamp.isEmpty() ? "" + Instant.now().getEpochSecond() : new_comment.timestamp);
            Statement statement = dbManager.getConnection().createStatement();
            try {
                statement.execute(query);
                //FeedController.propagateUpdate(new_comment);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.contentType("application/json").status(500).result(responseMessage(e.getMessage()));
            }
            ctx.contentType("application/json").status(201).result(responseData(GSON.toJson(new_comment)));

        } catch (JsonSyntaxException e) {
            ctx.contentType("application/json").status(422).result(responseMessage("Invalid Json!"));
        }
    };
}
