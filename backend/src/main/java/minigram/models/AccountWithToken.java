package minigram.models;

public class AccountWithToken extends Account {

    public String token;

    public AccountWithToken(String token, Account account) {
        super(account.id, account.name, account.profile_pic, account.email, "", "", account.following_ids);
        this.token = token;
    }

}
