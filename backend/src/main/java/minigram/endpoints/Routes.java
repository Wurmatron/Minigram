package minigram.endpoints;

import io.javalin.Javalin;
import minigram.controllers.AccountController;
import minigram.controllers.AuthController;
import minigram.endpoints.EndpointSecurity.AuthRoles;
import minigram.utils.anotations.Endpoint;

import static io.javalin.core.security.SecurityUtil.roles;

public class Routes {

    @Endpoint
    public void authentication(Javalin app) {
        app.post("register", AccountController.registerNewAccount, roles(AuthRoles.ANONYMOUS, AuthRoles.ADMIN));
        app.post("login", AuthController.login, roles(AuthRoles.ANONYMOUS,AuthRoles.ADMIN));
        app.post("logout", AuthController.logout, roles(AuthRoles.USER,AuthRoles.ADMIN));
//        app.post("reset-password", AuthController.reset_password, roles(AuthRoles.USER));
    }

    @Endpoint
    public void account(Javalin app) {
        app.get("/account/:id", AccountController.fetchAccount, roles(AuthRoles.USER,AuthRoles.ADMIN));
        app.get("/account", AccountController.fetchAccounts, roles(AuthRoles.USER,AuthRoles.ADMIN));
        app.put("/account", AccountController.updateAccount, roles(AuthRoles.USER,AuthRoles.ADMIN));
        app.delete("/account", AccountController.deleteAccount, roles(AuthRoles.USER,AuthRoles.ADMIN));
    }

    @Endpoint
    public void post(Javalin app) {
//        app.get("/posts/:id", PostsController.fetchPost,roles(AuthRoles.USER,AuthRoles.ADMIN));
//        app.get("/posts/", PostsController.fetchPosts,roles(AuthRoles.USER,AuthRoles.ADMIN));
//        app.put("/posts/:id", PostsController.updatePost,roles(AuthRoles.USER,AuthRoles.ADMIN));
//        app.delete("/posts/:id", PostsController.deletePost,roles(AuthRoles.USER,AuthRoles.ADMIN));
    }

    @Endpoint
    public void postComments(Javalin app) {
//        app.get("/posts/:id/comments/:id", CommentController.fetchPostComment,roles(AuthRoles.USER,AuthRoles.ADMIN));
//        app.get("/posts/:id/comments", CommentController.fetchPostComments,roles(AuthRoles.USER,AuthRoles.ADMIN));
//        app.put("/posts/:id/comments/:id", CommentController.updatePostComment,roles(AuthRoles.USER,AuthRoles.ADMIN));
//        app.delete("/posts/:id/comments/:id", CommentController.deletePostComment,roles(AuthRoles.USER,AuthRoles.ADMIN));
    }
}
