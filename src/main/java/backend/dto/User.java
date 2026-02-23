package backend.dto;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String email;
    private String passwordHash;
    private String createdAt;

    public User() {}

    public User(String userId, String email, String passwordHash, String createdAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
