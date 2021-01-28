package minigram.models;

/**
 * Config used by the backend of MiniGram
 */
public class Config {

    public General general;
    public Preformance preformance;

    // Used to set default values
    public Config() {
        this.general = new General(false, 8080);
        preformance = new Preformance(Runtime.getRuntime().availableProcessors() / 3, Runtime.getRuntime().availableProcessors() / 2);
    }

    // Constructor must contain all variables in the config
    public Config(General general) {
        this.general = general;
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

}
