package minigram.models;

import minigram.controllers.AuthController;

public class AuthService {

    public static Account authAccount(String token){
        if (AuthController.tokens.isEmpty()){
            return null;
        }

        return AuthController.tokens.get(token);
    }

}
