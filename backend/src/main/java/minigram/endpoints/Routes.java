package minigram.endpoints;

import io.javalin.Javalin;
import minigram.controllers.*;
import minigram.endpoints.EndpointSecurity.AuthRoles;
import minigram.utils.anotations.Endpoint;

import static io.javalin.core.security.SecurityUtil.roles;

public class Routes {

    @Endpoint
    public void authentication(Javalin app) {
        app.post("register", AccountController.registerNewAccount, roles(AuthRoles.ANONYMOUS, AuthRoles.ADMIN));
        app.post("login", AuthController.login, roles(AuthRoles.ANONYMOUS, AuthRoles.ADMIN));
        app.post("logout", AuthController.logout, roles(AuthRoles.USER, AuthRoles.ADMIN));
//        app.post("reset-password", AuthController.reset_password, roles(AuthRoles.USER));
    }

    @Endpoint
    public void account(Javalin app) {
        app.get("/account/:id", AccountController.fetchAccount, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.get("/account", AccountController.fetchAccounts, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.put("/account/:id", AccountController.updateAccount, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.delete("/account/:id", AccountController.deleteAccount, roles(AuthRoles.USER, AuthRoles.ADMIN));
    }

    @Endpoint
    public void post(Javalin app) {
        app.get("/posts/:id", PostsController.fetchPost, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.get("posts", PostsController.fetchPosts, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.put("/posts/:id", PostsController.updatePost, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.delete("/posts/:id", PostsController.deletePost, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.post("/posts", PostsController.createPost, roles(AuthRoles.USER, AuthRoles.ADMIN));
    }

    @Endpoint
    public void postComments(Javalin app) {
        app.get("/comments/:id", CommentController.fetchPostComment,roles(AuthRoles.USER,AuthRoles.ADMIN));
        app.get("/comments/posts/:id", CommentController.fetchPostComments,roles(AuthRoles.USER,AuthRoles.ADMIN));
        app.post("/comments", CommentController.createComment,roles(AuthRoles.USER,AuthRoles.ADMIN));
        app.delete("/comments/:id", CommentController.deletePostComment,roles(AuthRoles.USER,AuthRoles.ADMIN));
    }

    @Endpoint
    public void followings(Javalin app) {
        app.get("/accounts/:id/followers", FollowingsController.fetchAccountFollowers, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.get("/accounts/:id/following", FollowingsController.fetchAccountFollowing, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.post("/accounts/follow/:follow_id", FollowingsController.followAccount, roles(AuthRoles.USER, AuthRoles.ADMIN));
        app.post("/accounts/unfollow/:unfollow_id", FollowingsController.unfollowAccount, roles(AuthRoles.USER, AuthRoles.ADMIN));
    }

    @Endpoint
    public void feed(Javalin app) {
        app.get("/feed", FeedController.feed, roles(AuthRoles.USER, AuthRoles.ADMIN));
    }
}
