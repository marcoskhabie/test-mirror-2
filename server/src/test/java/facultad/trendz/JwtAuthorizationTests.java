package facultad.trendz;

import facultad.trendz.dto.user.JwtResponseDTO;
import facultad.trendz.dto.user.LoginDTO;
import facultad.trendz.dto.user.UserCreateDTO;
import facultad.trendz.dto.user.UserResponseDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtAuthorizationTests {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void testRequestWithoutJwt() throws URISyntaxException { // accessing endpoint without jwt
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        final String getUserUrl = "http://localhost:" + randomServerPort + "/user/3";
        URI getUserUri = new URI(getUserUrl);

        try {
        //WHEN
            restTemplate.getForEntity(getUserUri, String.class);

        //THEN
            Assert.fail();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(401, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Full authentication is required to access this resource"));
        }
    }

    @Test
    public void testRequestWithInvalidRole() throws URISyntaxException { // accessing admin exclusive endpoint with regular user jwt
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        registerUser("testEmail11@gmail.com", "testUsername11", "testPassword", "user");

        ResponseEntity<JwtResponseDTO> response = loginUser("testEmail11@gmail.com", "testPassword");

        String jwt = response.getBody().getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        final String adminUrl = "http://localhost:" + randomServerPort + "/admin";
        URI adminUri = new URI(adminUrl);

        try {
        //WHEN
            restTemplate.exchange(adminUri, HttpMethod.GET, entity, String.class);

        //THEN
            Assert.fail();

        } catch (HttpClientErrorException e) {
            Assert.assertEquals(401, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Unauthorized access"));
        }
    }

    @Test
    public void testAdminExclusiveRequest() throws URISyntaxException { // accessing admin exclusive endpoint with admin jwt
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        registerUser("testEmail10@gmail.com", "testUsername10", "testPassword", "admin");

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail10@gmail.com", "testPassword");

        String jwt = loginResponse.getBody().getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        final String adminUrl = "http://localhost:" + randomServerPort + "/admin";
        URI adminUri = new URI(adminUrl);

        //WHEN
        ResponseEntity<String> response = restTemplate.exchange(adminUri, HttpMethod.GET, entity, String.class);

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("Admin Content", response.getBody());

    }


    private ResponseEntity<UserResponseDTO> registerUser(String email, String username, String password, String role) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        final String registerUrl = "http://localhost:" + randomServerPort + "/user";
        URI registerUri = new URI(registerUrl);
        HttpHeaders registerHeaders = new HttpHeaders();
        UserCreateDTO userCreateDTO = new UserCreateDTO(email, username, password, role);
        HttpEntity<UserCreateDTO> registerRequest = new HttpEntity<>(userCreateDTO, registerHeaders);
        return restTemplate.postForEntity(registerUri, registerRequest, UserResponseDTO.class);
    }

    private ResponseEntity<JwtResponseDTO> loginUser(String email, String password) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        final String loginUrl = "http://localhost:" + randomServerPort + "/login";
        URI loginUri = new URI(loginUrl);
        HttpHeaders loginHeaders = new HttpHeaders();
        LoginDTO loginDTO = new LoginDTO(email, password);
        HttpEntity<LoginDTO> loginRequest = new HttpEntity<>(loginDTO, loginHeaders);
        return restTemplate.postForEntity(loginUri, loginRequest, JwtResponseDTO.class);
    }
}
