package minigram.models;

import minigram.utils.wrapper.IModel;

public class BaseModel implements IModel {

    protected static boolean isNum(String id) {
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
//
        }
        return false;
    }
}
