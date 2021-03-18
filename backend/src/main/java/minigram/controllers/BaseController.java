package minigram.controllers;

public class BaseController {

    static String[] removeElement(String[] array, int i) {
        for(int j = i; i < array.length-1; i++){
            array[i] = array[i + 1];
        }
        return array;
    }
}
