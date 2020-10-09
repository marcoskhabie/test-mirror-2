package facultad.logistics;

import facultad.logistics.dto.user.JwtResponseDTO;
import facultad.logistics.dto.user.LoginDTO;
import facultad.logistics.dto.user.UserCreateDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserLoginTests {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void testLogin() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        final String registerUrl = "http://localhost:" + randomServerPort + "/user";
        final String loginUrl = "http://localhost:" + randomServerPort + "/login";
        URI registerUri = new URI(registerUrl);
        URI loginUri = new URI(loginUrl);

        HttpHeaders headers = new HttpHeaders();
        UserCreateDTO userCreateDTO = new UserCreateDTO("testEmail@gmail.com","testUsername","testPassword","user");
        HttpEntity<UserCreateDTO> registerRequest = new HttpEntity<>(userCreateDTO,headers);
        restTemplate.postForEntity(registerUri, registerRequest, String.class);  // Post new user

        LoginDTO loginDTO = new LoginDTO("testEmail@gmail.com","testPassword");
        HttpEntity<LoginDTO> loginRequest = new HttpEntity<>(loginDTO,headers);

        //WHEN
        ResponseEntity<JwtResponseDTO> response = restTemplate.postForEntity(loginUri,loginRequest, JwtResponseDTO.class); // login with user credentials

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody().getToken());
    }

    @Test
    public void testLoginWithInvalidEmail() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        final String loginUrl = "http://localhost:" + randomServerPort + "/login";
        URI loginUri = new URI(loginUrl);

        HttpHeaders headers = new HttpHeaders();

        LoginDTO loginDTO = new LoginDTO("nonExistingEmail@gmail.com","testPassword");
        HttpEntity<LoginDTO> loginRequest = new HttpEntity<>(loginDTO,headers);

        try
        {
        //WHEN
            restTemplate.postForEntity(loginUri, loginRequest, JwtResponseDTO.class);  //login with non existing email and any password

        //THEN
            Assert.fail();
        }
        catch(HttpClientErrorException e)
        {
            Assert.assertEquals(404, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Requested user not found"));
        }
    }

    @Test
    public void testLoginWithInvalidPassword() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        final String registerUrl = "http://localhost:" + randomServerPort + "/user";
        final String loginUrl = "http://localhost:" + randomServerPort + "/login";

        URI registerUri = new URI(registerUrl);
        URI loginUri = new URI(loginUrl);

        HttpHeaders headers = new HttpHeaders();
        UserCreateDTO userCreateDTO = new UserCreateDTO("testEmail07@gmail.com","testUsername07","testPassword","user");
        HttpEntity<UserCreateDTO> registerRequest = new HttpEntity<>(userCreateDTO,headers);
        restTemplate.postForEntity(registerUri, registerRequest, String.class); // post new user

        LoginDTO loginDTO = new LoginDTO("testEmail07@gmail.com","invalidPassword");
        HttpEntity<LoginDTO> loginRequest = new HttpEntity<>(loginDTO,headers);

        try
        {
        //WHEN
            restTemplate.postForEntity(loginUri, loginRequest, JwtResponseDTO.class); // login with existing email and invalid password

        //THEN
            Assert.fail();
        }
        catch(HttpClientErrorException e)
        {
            Assert.assertEquals(401, e.getRawStatusCode());
        }
    }


}
