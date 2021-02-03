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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static minigram.MiniGram.config;

/**
 * Helps / Handles anything to do with Annotations
 */
public class AnnotationHelper {

    // Instances
    private static final Reflections REFLECTIONS = new Reflections("minigram.endpoints", new MethodAnnotationsScanner());

    // Instance Cache
    private static final NonBlockingHashMap<Method, Object> invokeObjects = new NonBlockingHashMap<>();

    private static HashMap<String, List<Method>> getEndpoints() {
        Method[] methods = REFLECTIONS.getMethodsAnnotatedWith(Endpoint.class).toArray(new Method[0]);
        HashMap<String, List<Method>> sorted = new HashMap<>();
        for (Method method : methods) {
            if (!isValidEndpoint(method)) {
                System.err.println("Invalid endpoint at '" + method.getDeclaringClass().getName() + "'");
                continue;
            }
            if (sorted.containsKey(method.getAnnotation(Endpoint.class).endpoint())) {
                sorted.get(method.getAnnotation(Endpoint.class).endpoint()).add(method);
            } else {
                List<Method> m = new ArrayList<>();
                m.add(method);
                sorted.put(method.getAnnotation(Endpoint.class).endpoint(), m);
            }
        }
        return sorted;
    }

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
                if (face != IModel.class) {
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
        HashMap<String, List<Method>> endpoints = getEndpoints();
        for (String point : endpoints.keySet()) {
            if (config.general.debug) {
                System.out.println("Loading Endpoint '" + point + "'");
            }
            for (Method m : endpoints.get(point))
                try {
                    invokeObjects.putIfAbsent(m, m.getDeclaringClass().newInstance());
                } catch (Exception e) {
                    System.err.println("Unable to create a instance of '" + m.getDeclaringClass().getName() + "'");
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
            Object data = method.invoke(invokeObjects.get(method), params);
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
        HashMap<String, List<Method>> endpoints = getEndpoints();
        for (String endpoint : endpoints.keySet()) {
            // Adds the endpoint to the httpserver
            server.createContext(endpoint, new EndpointWrapper(endpoints.get(endpoint)));
        }
    }
}
