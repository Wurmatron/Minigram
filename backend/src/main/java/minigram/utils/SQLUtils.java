package minigram.utils;

public class SQLUtils {

    public static String sanitize(String data) {
        return data.replaceAll("[^0-9a-zA-Z |\\-_@~.]", "").
                replaceAll("DROP", "").
                replaceAll("SELECT", "").
                replaceAll("FROM", "").
                replaceAll("CHAR", "");
    }

    public static String sanitizeText(String text) {
        return text.replaceAll("'", "").replaceAll(";", "");
    }
}
