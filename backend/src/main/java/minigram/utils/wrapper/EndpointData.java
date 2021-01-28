package minigram.utils.wrapper;

public class EndpointData {

    // HTTP Response Code
    public int responseCode;

    // Json Data to return in the request
    public String data;

    /**
     * Used by all endpoints to return data to the client / httpserver
     *
     * @param responseCode HTTP Response Code
     * @param data         Json Response Data
     * @see minigram.utils.reflection.Endpoint
     * @see EndpointWrapper
     */
    public EndpointData(int responseCode, String data) {
        this.responseCode = responseCode;
        this.data = data;
    }
}
