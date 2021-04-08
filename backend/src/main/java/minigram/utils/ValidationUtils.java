package minigram.utils;

import minigram.models.Account;

import static minigram.controllers.AccountController.EMAIL_REGEX;
import static minigram.utils.SQLUtils.sanitize;

public class ValidationUtils {


//    Account validation function moved, NB: not used at the moment
    private static boolean isValidAccount(Account account) {
        if (sanitize(account.name).isEmpty()) {
            return false;
        }
        if (sanitize(account.email).isEmpty() || !EMAIL_REGEX.matcher(account.email).find()) {
            return false;
        }
        try {
            Account acc = Account.getAccountById(account.name);
            if(acc.id == null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !account.password_hash.isEmpty() && !account.password_salt.isEmpty();
    }
}
