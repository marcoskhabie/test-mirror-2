package facultad.logistics.dto.user;

public class JwtResponseDTO {

    private static final String TYPE = "Bearer";
    private String token;

    public JwtResponseDTO(String token) {
        this.token = token;
    }

    public JwtResponseDTO() {
    }

    public String getType() {
        return TYPE;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
