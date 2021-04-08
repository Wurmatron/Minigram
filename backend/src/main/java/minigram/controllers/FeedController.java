package minigram.controllers;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import minigram.models.Account;
import minigram.models.Comment;
import minigram.models.FeedEntry;
import minigram.models.Post;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static minigram.MiniGram.GSON;
import static minigram.utils.HttpUtils.responseMessage;

public class FeedController {

    public static final int MAX_POST_COUNT = 50;

    // User Token, Sorted Feed
    public static NonBlockingHashMap<String, FeedEntry[]> sortedFeed = new NonBlockingHashMap<>();

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
                    @OpenApiParam(name = "followers", type = Boolean.class, description = "disableFollowers")
            },
            tags = {"Feed"}
    )
    public static Handler feed = ctx -> {
        String token = ctx.header("token");
        Account account = null;
        boolean followers = true;
        if (!ctx.queryParam("followers", "").isEmpty())
            followers = Boolean.parseBoolean(ctx.queryParam("followers"));
        if (token != null && !token.isEmpty())
            account = AuthController.tokens.getOrDefault(token, null);
        if (account != null) {
            // Set query defaults
            int startPos = 0;
            int endPos = MAX_POST_COUNT;
            // Check for query params
            try {
                startPos = ctx.queryParam("start", Integer.class).get();
                endPos = ctx.queryParam("end", Integer.class).get();
            } catch (BadRequestResponse e) {
            } // Not an error, has defaults
            // Validate start and end points
            if (startPos > endPos || endPos - startPos > MAX_POST_COUNT) {
                ctx.contentType("application/json").status(422).result(responseMessage("Invalid Query Param " + ((endPos - startPos > MAX_POST_COUNT)
                        ? "(Too many posts, You: " + (endPos - startPos) + ", Max: " + MAX_POST_COUNT + ")" : "")));
                return;
            }
            ctx.contentType("application/json").status(201).result(GSON.toJson(getFeed(token, startPos, endPos, followers)));
        } else {
            ctx.contentType("application/json").status(401).result(responseMessage("No Account associated with the given token!"));
        }
    };

    public static List<FeedEntry> getFeed(String userToken, int start, int end, boolean followers) {
        // Check cache
        if (sortedFeed.containsKey(userToken) && sortedFeed.get(userToken).length >= (end - start)) {
            try {
                FeedEntry[] userFeed = sortedFeed.get(userToken);
                FeedEntry startEntry = userFeed[start];
                FeedEntry endEntry = userFeed[end];
                if (startEntry != null && endEntry != null) {
                    List<FeedEntry> entries = new ArrayList<>();
                    for (int index = start; index < end; index++) {
                        entries.add(userFeed[index]);
                    }
                    return entries;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        // Generate / update the sorted list / cache
        FeedEntry[] userFeed = generateOrUpdateFeed(userToken, followers);
        sortedFeed.remove(userToken);
        sortedFeed.put(userToken, userFeed);
        List<FeedEntry> feed = new ArrayList<>();
        for (int index = start; index < end; index++) {
            if (index < userFeed.length)
                feed.add(userFeed[index]);
        }
        return feed;
    }

    public static FeedEntry[] generateOrUpdateFeed(String userToken, boolean followersEnabled) {
        Account account = AuthController.tokens.get(userToken);
        List<FeedEntry> feed = new ArrayList<>();
        // TODO If have extra time, cache posts, keep updated log, lowers SQL requests a ton
        List<Post> posts = Post.getPosts();  // Not the best design, but ill work for a small project
        List<Post> userPosts = new ArrayList<>();
        for (int index = 0; index < posts.size(); index++) {
            Post post = posts.get(index);
            if (post.posted_by_id.equals(account.id))
                userPosts.add(post);
            else if (followersEnabled && isFollowing(account, post)) {
                userPosts.add(post);
            }
        }
        try {
            posts.sort(Comparator.comparingLong(e -> Long.parseLong(e.timestamp)));
            for (Post post : posts) {
                feed.add(createEntry(post));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed.toArray(new FeedEntry[0]);
    }

    private static boolean isFollowing(Account account, Post post) {
        for (String follow : account.following_ids) {
            if (follow.equals(post.posted_by_id))
                return true;
        }
        return false;
    }

    public static FeedEntry createEntry(Post post) {
        List<Comment> postComments = new ArrayList<>();
        for (String commentID : post.comments_ids) {
            if (commentID != null && !commentID.isEmpty())
                postComments.add(Comment.getCommentById(commentID));
        }
        return new FeedEntry(post, postComments.toArray(new Comment[0]));
    }

    public static void propagateUpdate(Post post) {
        for (String token : AuthController.tokens.keySet()) {
            Account account = AuthController.tokens.get(token);
            if (post.posted_by_id.equals(account.id)) {  // Update own user's feed
                sortedFeed.remove(token);   // Removing key will force update, upon request
                // Update follower's
                if (FollowingsController.followingCache.containsKey(account.id)) {
                    for (Account acc : FollowingsController.followingCache.get(account.id)) {
                        for (String loadedToken : AuthController.tokens.keySet()) {
                            if (acc.id.equals(AuthController.tokens.get(loadedToken).id)) {
                                sortedFeed.remove(loadedToken);
                                break;
                            }
                        }
                    }
                } else {
                    FollowingsController.updateFollowing(post.posted_by_id);
                }
            }
        }
    }
}
