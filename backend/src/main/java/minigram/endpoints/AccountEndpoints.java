package minigram.endpoints;

import minigram.models.Account;
import minigram.utils.reflection.Endpoint;
import minigram.utils.reflection.RequestType;
import minigram.utils.wrapper.EndpointData;

import static minigram.MiniGram.GSON;

public class AccountEndpoints {

    // TODO Temp Example
    @Endpoint(endpoint = "/api/account", type = RequestType.GET)
    public EndpointData getUser(Account requestData) {
        Account account = new Account(requestData.id, "TestGet", "test@minigram.com", "", "");
        return new EndpointData(200, GSON.toJson(account));
    }

    // TODO Temp Example
    @Endpoint(endpoint = "/api/account", type = RequestType.POST)
    public EndpointData postUser(Account requestData) {
        Account account = new Account(requestData.id, "TestPost", "test@minigram.com", "", "");
        return new EndpointData(200, GSON.toJson(account));
    }
}
