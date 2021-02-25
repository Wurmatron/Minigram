package minigram.endpoints;

import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Context;
import minigram.MiniGram;
import minigram.controllers.AuthController;
import minigram.utils.anotations.Endpoint;

import static minigram.utils.HttpUtils.responseMessage;

public class EndpointSecurity {

    public static AuthRoles getUserRole(Context ctx) {
        if (MiniGram.config.general.debug) {
            return AuthRoles.ADMIN;
        }
        String sessionID = ctx.header("token");
        if (sessionID != null && AuthController.tokens.containsKey(sessionID)) {
            return AuthRoles.USER;
        }
        return AuthRoles.ANONYMOUS;
    }

    @Endpoint
    public void accessManager(Javalin app) {
        app.config.accessManager((handler, ctx, permittedRoles) -> {
            AuthRoles userRole = getUserRole(ctx);
            if (permittedRoles.contains(userRole)) {
                handler.handle(ctx);
            } else {
                ctx.contentType("application/json").status(401).result(responseMessage("Unauthorized"));
            }
        });
    }

    enum AuthRoles implements Role {
        USER, ANONYMOUS, ADMIN
    }
}
