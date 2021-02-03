package minigram.models;

/**
 * Config used by the backend of MiniGram
 */
public class Config {

    public General general;
    public Preformance preformance;
    public Database database;

    // Used to set default values
    public Config() {
        this.general = new General(false, 8080);
        preformance = new Preformance(Runtime.getRuntime().availableProcessors() / 3, Runtime.getRuntime().availableProcessors() / 2);
        database = new Database("localhost", "3306", "root", "", "minigram");
    }

    // Constructor must contain all variables in the config
    public Config(General general, Preformance preformance, Database database) {
        this.general = general;
        this.preformance = preformance;
        this.database = database;
    }

    // General Category
    public class General {

        public boolean debug;
        public int port;

        public General(boolean debug, int port) {
            this.debug = debug;
            this.port = port;
        }
    }

    // Performance Category
    public class Preformance {
        public int schedule_threads;
        public int general_threads;

        public Preformance(int schedule_threads, int general_threads) {
            this.schedule_threads = schedule_threads;
            this.general_threads = general_threads;
        }
    }

    // Database Category
    public class Database {
        public String address;
        public String port;
        public String username;
        public String password;
        public String database_name;

        public Database(String address, String port, String username, String password, String database_name) {
            this.address = address;
            this.port = port;
            this.username = username;
            this.password = password;
            this.database_name = database_name;
        }
    }

}
