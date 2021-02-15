package minigram.endpoints;

import io.javalin.Javalin;
import minigram.controllers.AccountController;
import minigram.controllers.AuthController;
import minigram.utils.anotations.Endpoint;

public class Routes {

    @Endpoint
    public void authentication(Javalin app) {
        app.post("account/register", AccountController.registerNewAccount);
        app.post("login", AuthController.login);
        app.post("logout", AuthController.logout);
//        app.post("reset-password", AuthController.reset_password);
    }

    @Endpoint
    public void account(Javalin app) {
        app.get("/account/id", AccountController.fetchAccount);
//        app.get("/account", AccountController.fetchAccounts);
//        app.put("/account/update/", AccountController.updateAccount);
//        app.delete("/account/delete", AccountController.deleteAccount);
    }
}
