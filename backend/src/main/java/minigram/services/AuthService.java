package minigram.services;

import minigram.controllers.AuthController;
import minigram.models.Account;

public class AuthService {

    public static Account authAccount(String token){
        if (AuthController.tokens.isEmpty()){
            return null;
        }

        return AuthController.tokens.get(token);
    }

}
