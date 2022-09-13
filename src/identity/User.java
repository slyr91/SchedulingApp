package identity;

public class User {

    private final Integer userID;
    private final String username;

    public User(Integer userID, String username) {
        this.userID = userID;
        this.username = username;

    }

    public String getUsername() {
        return username;
    }

    public Integer getUserID() {
        return userID;
    }
}
