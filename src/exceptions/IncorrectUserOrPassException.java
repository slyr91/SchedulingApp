package exceptions;

public class IncorrectUserOrPassException extends RuntimeException {

    private final String username;

    public IncorrectUserOrPassException(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
