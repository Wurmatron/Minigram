package minigram.utils;

public class HttpUtils {

    public static String responseData(String data){
        return "{\"data\": \""+ data +"\"}";
    }

    public static String responseMessage(String message){
        return "{\"message\": \""+ message +"\"}";
    }
}
