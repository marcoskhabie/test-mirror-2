package facultad.trendz;

import facultad.trendz.dto.*;
import facultad.trendz.dto.user.*;
import facultad.trendz.model.ERole;
import facultad.trendz.model.User;
import facultad.trendz.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserProfileTests {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void testUserProfileData() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<UserResponseDTO> registerResponse = registerUser(
                "testUsername08", "testEmail08@gmail.com", "testPassword", "user"); // Post new user

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail08@gmail.com", "testPassword"); // Login with new User to get JWT

        String jwtToken = loginResponse.getBody().getToken();
        long userId = registerResponse.getBody().getId();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        final String getProfileUrl = String.format("http://localhost:%d/user/%d", randomServerPort, userId);
        URI getProfileUri = new URI(getProfileUrl);

        //WHEN
        ResponseEntity<UserResponseDTO> response = restTemplate.exchange(getProfileUri, HttpMethod.GET, entity, UserResponseDTO.class); // get new user profile data, used exchange() method to add Authorization header

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertEquals("testEmail08@gmail.com", response.getBody().getEmail());
        Assert.assertEquals("testUsername08", response.getBody().getUsername());
        Assert.assertEquals(ERole.ROLE_USER, response.getBody().getRole().getEnumRole());

    }

    @Test
    public void testInvalidUserProfileData() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        registerUser("testUsername09", "testEmail09@gmail.com", "testPassword", "user"); // Post new user

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail09@gmail.com", "testPassword"); // Login with new User to get JWT

        String jwtToken = loginResponse.getBody().getToken();
        Long userId = Long.MAX_VALUE; // unused id

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        final String getProfileUrl = String.format("http://localhost:%d/user/%d", randomServerPort, userId);
        URI getProfileUri = new URI(getProfileUrl);

        try {
        //WHEN
            restTemplate.exchange(getProfileUri, HttpMethod.GET, entity, UserResponseDTO.class);// getting profile data for invalid userId

        //THEN
            Assert.fail();
        } catch (HttpClientErrorException e) {

            Assert.assertEquals(404, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Requested user not found"));
        }

    }

    @Test
    public void testProfileEdit() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<UserResponseDTO> registerResponse = registerUser("testUsername12", "testEmail12@gmail.com", "testPassword", "user");
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail12@gmail.com", "testPassword");

        Long userId = registerResponse.getBody().getId();
        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        ProfileEditDTO body = new ProfileEditDTO("newUsername","testPassword","newPassword");

        HttpEntity<ProfileEditDTO> entity = new HttpEntity<>(body, headers);

        final String profileEditUrl = String.format("http://localhost:%d/user", randomServerPort);
        URI profileEditUri = new URI(profileEditUrl);

        //WHEN
        ResponseEntity<MessageResponseDTO> response = restTemplate.exchange(profileEditUri, HttpMethod.PUT, entity, MessageResponseDTO.class);

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().getMessage().contains("Profiled edited successfully"));

        Optional<User> user = userRepository.findById(userId);
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals("newUsername",user.get().getUsername());
        Assert.assertTrue(passwordEncoder.matches("newPassword",user.get().getPassword()));
    }

    @Test
    public void testUsernameEdit() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<UserResponseDTO> registerResponse = registerUser("testUsername13", "testEmail13@gmail.com", "testPassword", "user");
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail13@gmail.com", "testPassword");

        Long userId = registerResponse.getBody().getId();
        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        ProfileEditDTO body = new ProfileEditDTO("newUsername02", null, null);

        HttpEntity<ProfileEditDTO> entity = new HttpEntity<>(body, headers);

        final String profileEditUrl = String.format("http://localhost:%d/user", randomServerPort);
        URI profileEditUri = new URI(profileEditUrl);

        //WHEN
        ResponseEntity<MessageResponseDTO> response = restTemplate.exchange(profileEditUri, HttpMethod.PUT, entity, MessageResponseDTO.class);

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().getMessage().contains("Profiled edited successfully"));

        Optional<User> user = userRepository.findById(userId);
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals("newUsername02",user.get().getUsername());
        Assert.assertTrue(passwordEncoder.matches("testPassword",user.get().getPassword())); // password remains unchanged
    }

    @Test
    public void testPasswordEdit() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<UserResponseDTO> registerResponse = registerUser("testUsername14", "testEmail14@gmail.com", "testPassword", "user");
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail14@gmail.com", "testPassword");

        Long userId = registerResponse.getBody().getId();
        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        ProfileEditDTO body = new ProfileEditDTO(null, "testPassword", "newPassword");

        HttpEntity<ProfileEditDTO> entity = new HttpEntity<>(body, headers);

        final String profileEditUrl = String.format("http://localhost:%d/user", randomServerPort);
        URI profileEditUri = new URI(profileEditUrl);

        //WHEN
        ResponseEntity<MessageResponseDTO> response = restTemplate.exchange(profileEditUri, HttpMethod.PUT, entity, MessageResponseDTO.class);

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertTrue(response.getBody().getMessage().contains("Profiled edited successfully"));

        Optional<User> user = userRepository.findById(userId);
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals("testUsername14",user.get().getUsername()); // username remains unchanged
        Assert.assertTrue(passwordEncoder.matches("newPassword",user.get().getPassword()));
    }

    @Test
    public void testInvalidUsernameEdit() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        registerUser("testUsername15", "testEmail15@gmail.com", "testPassword", "user");
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail15@gmail.com", "testPassword");

        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        ProfileEditDTO body = new ProfileEditDTO("1", "testPassword", "newPassword"); // username '1' already in use

        HttpEntity<ProfileEditDTO> entity = new HttpEntity<>(body, headers);

        final String profileEditUrl = String.format("http://localhost:%d/user", randomServerPort);
        URI profileEditUri = new URI(profileEditUrl);

        try {

        //WHEN
            restTemplate.exchange(profileEditUri, HttpMethod.PUT, entity, MessageResponseDTO.class);

        //THEN
            Assert.fail();
        } catch (HttpClientErrorException e) {

            Assert.assertEquals(409, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Username 1 already taken"));
        }
    }

    @Test
    public void testInvalidPasswordEdit() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        registerUser("testUsername16", "testEmail16@gmail.com", "testPassword", "user");
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("testEmail16@gmail.com", "testPassword");

        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        ProfileEditDTO body = new ProfileEditDTO("newUsername", "InvalidPassword", "newPassword"); //oldPassword doesn't match original password

        HttpEntity<ProfileEditDTO> entity = new HttpEntity<>(body, headers);

        final String profileEditUrl = String.format("http://localhost:%d/user", randomServerPort);
        URI profileEditUri = new URI(profileEditUrl);

        try {

        //WHEN
            restTemplate.exchange(profileEditUri, HttpMethod.PUT, entity, MessageResponseDTO.class);

        //THEN
            Assert.fail();
        } catch (HttpClientErrorException e) {

            Assert.assertEquals(401, e.getRawStatusCode());
        }
    }

    private ResponseEntity<UserResponseDTO> registerUser(String username, String email, String password, String role) throws URISyntaxException {
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
