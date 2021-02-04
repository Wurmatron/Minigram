package minigram;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import minigram.models.Config;
import minigram.sql.DatabaseController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Main program for the backend of MiniGram
 */
public class MiniGram {

    // Program vars
    public static Config config;
    public static Javalin server;
    public static DatabaseController controller;

    // Global Instances
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
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
        server = Javalin.create().start(config.general.port);
        server.get("/", ctx -> ctx.result("Hello World"));
        // Startup DB
        controller = new DatabaseController(config.database);
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
}
