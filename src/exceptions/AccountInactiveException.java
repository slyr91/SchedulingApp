package exceptions;

public class AccountInactiveException extends Throwable {
    private final String username;

    public AccountInactiveException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
