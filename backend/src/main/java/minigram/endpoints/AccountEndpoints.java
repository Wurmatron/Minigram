package minigram.endpoints;

import minigram.models.Account;
import minigram.utils.reflection.Endpoint;
import minigram.utils.wrapper.EndpointData;

import static minigram.MiniGram.GSON;

public class AccountEndpoints {

    // TODO Temp Example
    @Endpoint(endpoint = "/api/accountByID")
    public EndpointData getUserByID(Account requestData) {
        Account account = new Account(requestData.id, "Test", "test@minigram.com", "", "");
        return new EndpointData(200, GSON.toJson(account));
    }
}
