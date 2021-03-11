package minigram;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.util.RedirectToLowercasePathPlugin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import minigram.endpoints.EndpointSecurity;
import minigram.models.Config;
import minigram.sql.DatabaseManager;
import minigram.utils.anotations.Endpoint;
import org.reflections8.Reflections;
import org.reflections8.scanners.MethodAnnotationsScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static io.javalin.core.security.SecurityUtil.roles;

/**
 * Main program for the backend of MiniGram
 */
public class MiniGram {

    // Program vars
    public static Config config;
    public static Javalin server;
    public static DatabaseManager dbManager;

    // Global Instances
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Reflections REFLECTIONS = new Reflections("minigram", new MethodAnnotationsScanner());
    public static ScheduledExecutorService SCHEDULED_EXEC;
    public static ExecutorService EXEC;

    public static void main(String[] args) {
        // Load Config and Initialize Config Settings
        config = loadConfig();
        if (config == null) {
            System.err.println("Unable to load config file!");
            System.exit(-2);
        }
        SCHEDULED_EXEC = Executors.newScheduledThreadPool(config.preformance.schedule_threads);
        EXEC = Executors.newFixedThreadPool(config.preformance.general_threads);

        // Setup Http Server
        server = Javalin.create(conf -> {
            conf.registerPlugin(new OpenApiPlugin(new OpenApiOptions(new Info().version("1.0.0").description("MiniGram Rest API")).path("/swagger-docs").swagger(new SwaggerOptions("/swagger").title("MiniGram Swagger")).roles(Collections.singleton(EndpointSecurity.AuthRoles.ADMIN))));
            conf.registerPlugin(new RedirectToLowercasePathPlugin());
            conf.enableCorsForAllOrigins();
        });
        server.start(config.general.port);
        registerEndpoints();    // Locate and register all endpoints
        server.get("/", ctx -> ctx.result("Hello World"));
        // Startup DB
        dbManager = new DatabaseManager(config.database);
    }

    public static Config loadConfig() {
        File configFile = new File("backend.json");
        System.out.println("Loading config from '" + configFile.getAbsolutePath() + "'");
        if (configFile.exists()) {
            try {
                // Attempt to read config file
                String fileData = new String(Files.readAllBytes(configFile.toPath()));
                return GSON.fromJson(fileData, Config.class);
            } catch (IOException e) {   // No Perms, File Lock, Etc..
                System.err.println("Failed to load config file '" + configFile.getAbsolutePath() + "'");
                e.printStackTrace();
                System.exit(-2);
            } catch (JsonSyntaxException e) {   // Invalid Json Syntax
                System.err.println("Failed to parse config file '" + configFile.getAbsolutePath() + "'");
                e.printStackTrace();
                System.exit(-2);
            }
        } else {    // New Config
            Config config = new Config();
            try {
                Files.write(configFile.toPath(), GSON.toJson(config).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                System.err.println("Failed to write config file '" + configFile.getAbsolutePath() + "'");
                e.printStackTrace();
                System.exit(-2);
            }
            return config;
        }
        return null;
    }

    private static void registerEndpoints() {
        Set<Method> endpoints = REFLECTIONS.getMethodsAnnotatedWith(Endpoint.class);
        for (Method method : endpoints) {
            if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Javalin.class)
                try {
                    method.invoke(method.getDeclaringClass().newInstance(), server);
                    if (config.general.debug) {
                        System.out.println("Registering endpoint '" + method.getName() + "@" + method.getDeclaringClass().getName() + "'");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to register endpoint '" + method.getName() + "@" + method.getDeclaringClass().getName() + "'");
                }
        }
    }
}
