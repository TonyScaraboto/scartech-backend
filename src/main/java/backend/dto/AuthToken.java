package backend.dto;

import java.io.Serializable;

public class AuthToken implements Serializable {
    private String token;
    private String userId;
    private String email;
    private Long expiresIn;

    public AuthToken(String token, String userId, String email, Long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}
