package facultad.trendz;

import facultad.trendz.dto.user.UserCreateDTO;
import facultad.trendz.dto.user.UserResponseDTO;
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
import org.springframework.http.HttpStatus;
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
public class UserRegisterTests {

	@LocalServerPort
	int randomServerPort;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@Test
	public void testPostNewUserSuccessfulResponse() throws URISyntaxException {
		//GIVEN
		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "http://localhost:" + randomServerPort + "/user";
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		UserCreateDTO userCreateDTO = new UserCreateDTO("testEmail01@gmail.com","testUsername01","testPassword","user");
		HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO,headers);

		//WHEN
		ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity(uri,request,UserResponseDTO.class);

		//THEN
		Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
	}

	@Test
	public void testPostNewUser() throws URISyntaxException {
		//GIVEN
		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "http://localhost:" + randomServerPort + "/user";
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		UserCreateDTO userCreateDTO = new UserCreateDTO("testEmail02@gmail.com","testUsername02","testPassword","user");
		HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO,headers);

		//WHEN
		ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity(uri,request,UserResponseDTO.class);

		//THEN
		Assert.assertNotNull(response.getBody().getId());									//assert id is created
		Optional<User> user = userRepository.findById(response.getBody().getId());

		Assert.assertTrue(user.isPresent());											//assert user is present in db

		Assert.assertEquals("testEmail02@gmail.com",user.get().getEmail());
		Assert.assertEquals("testUsername02",user.get().getUsername());
		Assert.assertTrue(passwordEncoder.matches("testPassword",user.get().getPassword())); //assert password is encrypted
		Assert.assertEquals(ERole.ROLE_USER,user.get().getRole().getEnumRole());
	}

	@Test
	public void testUserDTOValidation() throws URISyntaxException {
		//GIVEN
		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "http://localhost:" + randomServerPort + "/user";
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		UserCreateDTO userCreateDTO = new UserCreateDTO(null,null,"pass","user");
		HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO,headers);

		try
		{
		//WHEN
			restTemplate.postForEntity(uri, request, String.class);

		//THEN
			Assert.fail();
		}
		catch(HttpClientErrorException e)
		{
			Assert.assertEquals(400, e.getRawStatusCode());
			Assert.assertTrue(e.getResponseBodyAsString().contains("Email cannot be empty") &&
					e.getResponseBodyAsString().contains("Password must be at least 8 characters long") &&
					e.getResponseBodyAsString().contains("Username cannot be empty"));
		}
	}

	@Test
	public void testUsernameExistsException() throws URISyntaxException {
		//GIVEN
		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "http://localhost:" + randomServerPort + "/user";
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		UserCreateDTO testUser01 = new UserCreateDTO("testEmail04@gmail.com","testUsername04","password","user");
		UserCreateDTO testUser02 = new UserCreateDTO("testEmail05@gmail.com","testUsername04","password","user");
		HttpEntity<UserCreateDTO> request01 = new HttpEntity<>(testUser01,headers);
		HttpEntity<UserCreateDTO> request02 = new HttpEntity<>(testUser02,headers);
		restTemplate.postForEntity(uri, request01, String.class);

		try {
		//WHEN
			restTemplate.postForEntity(uri, request02, String.class);

		//THEN
			Assert.fail();
		}
		catch (HttpClientErrorException e){
			Assert.assertEquals(409,e.getRawStatusCode());
			Assert.assertTrue(e.getResponseBodyAsString().contains("Username testUsername04 already taken"));
		}
	}

	@Test
	public void testEmailExistsException() throws URISyntaxException {
		//GIVEN
		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "http://localhost:" + randomServerPort + "/user";
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		UserCreateDTO testUser01 = new UserCreateDTO("testEmail06@gmail.com","testUsername06","password","user");
		UserCreateDTO testUser02 = new UserCreateDTO("testEmail06@gmail.com","testUsername07","password","user");
		HttpEntity<UserCreateDTO> request01 = new HttpEntity<>(testUser01,headers);
		HttpEntity<UserCreateDTO> request02 = new HttpEntity<>(testUser02,headers);
		restTemplate.postForEntity(uri, request01, String.class);

		try {
		//WHEN
			restTemplate.postForEntity(uri, request02, String.class);

		//THEN
			Assert.fail();
		}
		catch (HttpClientErrorException e){
			Assert.assertEquals(409,e.getRawStatusCode());
			Assert.assertTrue(e.getResponseBodyAsString().contains("Email testEmail06@gmail.com already taken"));
		}
	}

	/*@Test
	public void testDeleteUserSuccessfulResponse() throws URISyntaxException {
		//GIVEN
		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "http://localhost:" + randomServerPort + "/user";
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUsername(), loginDTO.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		JwtResponseDTO body = new JwtResponseDTO(jwtUtils.generateJwtToken(authentication));
		HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO,headers);

		//WHEN
		ResponseEntity<UserResponseDTO> response = restTemplate.postForEntity(uri,request,UserResponseDTO.class);

		//THEN
		Assert.assertEquals(HttpStatus.CREATED,response.getStatusCode());
	}*/
}
