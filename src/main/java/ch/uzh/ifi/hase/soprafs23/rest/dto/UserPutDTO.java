package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class UserPutDTO {

    private String username;
    private long id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
