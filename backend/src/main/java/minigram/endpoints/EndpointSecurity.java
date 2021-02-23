package minigram.endpoints;

import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Context;
import minigram.controllers.AuthController;
import minigram.utils.anotations.Endpoint;

public class EndpointSecurity {

    public static AuthRoles getUserRole(Context ctx) {
        String sessionID = ctx.header("sessionID");
        if(sessionID != null && AuthController.tokens.containsKey(sessionID)) {
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
                ctx.status(401).result("Unauthorized");
            }
        });
    }

    enum AuthRoles implements Role {
        USER, ANONYMOUS, ADMIN
    }
}
