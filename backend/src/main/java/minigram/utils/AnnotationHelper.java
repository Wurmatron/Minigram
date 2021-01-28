package minigram.utils;

import com.sun.net.httpserver.HttpServer;
import minigram.utils.reflection.Endpoint;
import minigram.utils.wrapper.EndpointData;
import minigram.utils.wrapper.EndpointWrapper;
import minigram.utils.wrapper.IModel;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.reflections8.Reflections;
import org.reflections8.scanners.MethodAnnotationsScanner;


import java.lang.reflect.Method;
import java.util.Arrays;

import static minigram.MiniGram.config;

/**
 * Helps / Handles anything to do with Annotations
 */
public class AnnotationHelper {

    // Instances
    private static final Reflections REFLECTIONS = new Reflections("minigram.endpoints", new MethodAnnotationsScanner());

    // Instance Cache
    private static final NonBlockingHashMap<String, Object> invokeObjects = new NonBlockingHashMap<>();

    private static Method[] getEndpoints() {
        return REFLECTIONS.getMethodsAnnotatedWith(Endpoint.class).toArray(new Method[0]);
    }

    /**
     * Returns a list of endpoints along with its function
     *
     * @return Endpoint (URL), Endpoint Method
     */
    public static NonBlockingHashMap<String, Method> getEndPoints() {
        NonBlockingHashMap<String, Method> endpoints = new NonBlockingHashMap<>();
        Method[] locatedEndpoints = getEndpoints();
        for (Method method : locatedEndpoints) {
            // Make sure its a valid
            if (method.getAnnotation(Endpoint.class) != null && method.getAnnotation(Endpoint.class).endpoint().length() > 0 && isValidEndpoint(method)) {
                endpoints.put(method.getAnnotation(Endpoint.class).endpoint(), method);
            } else {
                System.out.println("Error loading endpoint '" + method.getName() + "' within '" + method.getDeclaringClass().getName() + "'");
            }
        }
        return endpoints;
    }

    // TODO Implement checks

    /**
     * Checks if a given endpoint function is a valid endpoint
     *
     * @param method Method to check if its valid
     * @return If a method can become a valid endpoint
     */
    private static boolean isValidEndpoint(Method method) {
        if (method.getReturnType() != EndpointData.class) {
            return false;
        }
        if (method.getParameterCount() == 0) {
            return true;
        }
        for (Class<?> clazz : method.getParameterTypes()) {
            for (Class<?> face : clazz.getInterfaces()) {
                if(face != IModel.class) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates instances of each endpoints class, to allow for invocation later
     */
    public static void setup() {
        invokeObjects.clear();
        NonBlockingHashMap<String, Method> endpoints = getEndPoints();
        for (Method method : endpoints.values()) {
            if (config.general.debug) {
                System.out.println("Loading Endpoint '" + method.getAnnotation(Endpoint.class).endpoint() + "' from '" + method.getDeclaringClass().getName() + "'");
            }
            try {
                invokeObjects.putIfAbsent(method.getDeclaringClass().getName(), method.getDeclaringClass().newInstance());
            } catch (Exception e) {
                System.err.println("Unable to create a instance of '" + method.getDeclaringClass().getName() + "'");
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs a given endpoint with the given parameters
     *
     * @param method Endpoint function
     * @param params Endpoint Parameters / Values
     * @return The return data from the given endpoint
     */
    public static EndpointData invokeEndpoint(Method method, Object... params) {
        // Make sure the instances have been setup
        if (invokeObjects.size() == 0) {
            setup();
        }
        try {
            // Try running the given endpoint method
            Object data = method.invoke(invokeObjects.get(method.getDeclaringClass().getName()), params);
            if (data instanceof EndpointData) {
                return (EndpointData) data;
            } else {
                System.err.println("Endpoint '" + method.getAnnotation(Endpoint.class) + "' has returned invalid data!");
            }
        } catch (Exception e) {
            System.err.println("Unable to run endpoint '" + method.getAnnotation(Endpoint.class).endpoint() + "' with [" + Arrays.toString(params) + "]");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds the endpoints to the given HttpServer
     *
     * @param server Endpoint Http Server
     */
    public static void setupEndpoints(HttpServer server) {
        NonBlockingHashMap<String, Method> endpoints = getEndPoints();
        for (String endpoint : endpoints.keySet()) {
            // Adds the endpoint to the httpserver
            server.createContext(endpoint, new EndpointWrapper(endpoints.get(endpoint)));
        }
    }
}
