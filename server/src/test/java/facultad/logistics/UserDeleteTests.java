package facultad.logistics;

import facultad.logistics.dto.MessageResponseDTO;
import facultad.logistics.dto.user.*;
import facultad.logistics.repository.UserRepository;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserDeleteTests {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    public void succesfullyDeleteUserTest() throws URISyntaxException {
        //given
        final String deleteUrl = "http://localhost:" + randomServerPort + "/user";
        URI deleteUri = new URI(deleteUrl);
        RestTemplate restTemplate= new RestTemplate();
        ResponseEntity<JwtResponseDTO> responseEntity=loginUser("1@gmail.com","1");
        HttpHeaders deleteHeader= new HttpHeaders();
        deleteHeader.add("Authorization","Bearer "+responseEntity.getBody().getToken());
        HttpEntity<JwtResponseDTO> httpEntity=new HttpEntity<>(deleteHeader);
        //when
        ResponseEntity<MessageResponseDTO> responseEntity1= restTemplate.exchange(deleteUri, HttpMethod.DELETE,httpEntity, MessageResponseDTO.class);
        //then
        Assert.assertEquals(200,responseEntity1.getStatusCodeValue());
        Assert.assertTrue(responseEntity1.getBody().getMessage().contains("User Deleted"));
        Assert.assertFalse(userRepository.existsByEmail("1@gmail.com"));

    }



}
