package minigram.utils.wrapper;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import minigram.utils.AnnotationHelper;
import minigram.utils.reflection.Endpoint;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static minigram.MiniGram.GSON;

/**
 * Wrapper used by Endpoints to run, and parse the data
 */
public class EndpointWrapper implements HttpHandler {

    // Endpoint
    private final Method method;

    /**
     * @param method Endpoint method
     */
    public EndpointWrapper(Method method) {
        this.method = method;
    }


    /**
     * Runs a given request for the given endpoint
     *
     * @param exchange request from httpserver
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (isValid(exchange, exchange.getResponseHeaders())) {
            EndpointData data = AnnotationHelper.invokeEndpoint(method, formatForEndpoint(method, exchange));
            if (data != null) {
                exchange.sendResponseHeaders(data.responseCode, data.data.getBytes().length);
                exchange.getResponseBody().write(data.data.getBytes());
                exchange.getResponseBody().close();
            } else {
                System.err.println("Unable to run '" + method.getAnnotation(Endpoint.class).endpoint() + "' because it has a null response!");
                exchange.sendResponseHeaders(500, 0);
            }
        }
    }

    // TODO Implement Verification / Validation
    public static boolean isValid(HttpExchange exchange, Headers headers) {
        return true;
    }

    /**
     * Formats the given data into an array for use with the endpoint function
     * RequestData -> Function Input Array
     *
     * @param method   Endpoint function / method
     * @param exchange HttpServer Request
     * @return array of objects formated to the given endpoint's function
     */
    public static Object[] formatForEndpoint(Method method, HttpExchange exchange) {
        List<Object> data = new ArrayList<>();
        try {
            String inputData = IOUtils.toString(exchange.getRequestBody());
            if (method.getParameterCount() == 1) {
                Object temp = GSON.fromJson(inputData, method.getParameterTypes()[0]);
                data.add(temp);
            } else if (method.getParameterCount() != 0) {
                // TODO Implement mult-parameter functions
                System.out.println("Unimplemented!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0];
        }
        return data.toArray(new Object[0]);
    }
}
