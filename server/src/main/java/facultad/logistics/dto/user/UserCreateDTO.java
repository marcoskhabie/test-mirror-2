package facultad.logistics.dto.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class UserCreateDTO {

    @NotNull(message = "Email cannot be empty")
    private String email;

    @NotNull(message = "Username cannot be empty")
    private String username;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 8,message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Role cannot be empty")
    private String role;

    public UserCreateDTO(String email,String username,String password, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
