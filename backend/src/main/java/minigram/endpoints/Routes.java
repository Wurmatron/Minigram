package minigram.endpoints;

import io.javalin.Javalin;
import minigram.controllers.AccountController;
import minigram.controllers.AuthController;
import minigram.controllers.CommentController;
import minigram.controllers.PostsController;
import minigram.utils.anotations.Endpoint;

public class Routes {

    @Endpoint
    public void authentication(Javalin app) {
        app.post("register", AccountController.registerNewAccount);
        app.post("login", AuthController.login);
        app.post("logout", AuthController.logout);
//        app.post("reset-password", AuthController.reset_password);
    }

    @Endpoint
    public void account(Javalin app) {
        app.get("/account/:id", AccountController.fetchAccount);
        app.get("/account", AccountController.fetchAccounts);
        app.put("/account", AccountController.updateAccount);
//        app.delete("/account/delete", AccountController.deleteAccount);
    }

    @Endpoint
    public void post(Javalin app){
//        app.get("/posts/:id", PostsController.fetchPost);
//        app.get("/posts/", PostsController.fetchPosts);
//        app.put("/posts/:id", PostsController.updatePost);
//        app.delete("/posts/:id", PostsController.deletePost);
    }

    @Endpoint
    public void postComments(Javalin app){
//        app.get("/posts/:id/comments", CommentController.fetchPostComments);
    }
}
