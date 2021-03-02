package minigram.endpoints;

import minigram.utils.reflection.Endpoint;
import minigram.utils.wrapper.EndpointData;

public class HealthEndpoints {

    @Endpoint(endpoint = "/api/health")
    public EndpointData health() {
        return new EndpointData(201, "{\"health\": \"good\"}");
    }
}
