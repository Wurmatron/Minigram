package minigram.endpoints;

import io.javalin.Javalin;
import minigram.models.Account;
import minigram.utils.anotations.Endpoint;

import static minigram.MiniGram.GSON;

public class AccountRoutes {

    // TODO Example
    @Endpoint
    public void accountInfo(Javalin app) {
        app.get("/account/:id", ctx -> {
            String id = ctx.pathParam("id");
            ctx.result(GSON.toJson(new Account(id, "Test", "test@test.com", "B109F3BBBC244EB82441917ED06D618B9008DD09B3BEFD1B5E07394C706A8BB980B1D7785E5976EC049B46DF5F1326AF5A2EA6D103FD07C95385FFAB0CACBC86", "d^5#%^a2")));
        });
        app.post("/account/:id", ctx -> {
            String id = ctx.pathParam("id");
            System.out.println("Creating new user '" + id + "'");
            ctx.result(GSON.toJson(new Account(id, "Test", "test@test.com", "B109F3BBBC244EB82441917ED06D618B9008DD09B3BEFD1B5E07394C706A8BB980B1D7785E5976EC049B46DF5F1326AF5A2EA6D103FD07C95385FFAB0CACBC86", "d^5#%^a2")));
        });
    }
}
