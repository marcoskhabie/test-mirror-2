package facultad.logistics;

import facultad.logistics.dto.post.PostCreateDTO;
import facultad.logistics.dto.post.PostGetDTO;
import facultad.logistics.dto.post.PostResponseDTO;
import facultad.logistics.dto.topic.TopicCreateDTO;
import facultad.logistics.dto.topic.TopicResponseDTO;
import facultad.logistics.dto.user.*;
import facultad.logistics.dto.user.JwtResponseDTO;
import facultad.logistics.repository.TopicRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestGetPostsFromTopic {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    TopicRepository topicRepository;

    @Test
    public void testGetPostsByTopic() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");
        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        //post new topics
        TopicCreateDTO topic1 = new TopicCreateDTO("Topic 1", "description");


        HttpEntity<TopicCreateDTO> topicEntity1 = new HttpEntity<>(topic1, headers);

        final String topicsUrl = "http://localhost:" + randomServerPort + "/topic";
        URI topicsUri = new URI(topicsUrl);

        ResponseEntity<TopicResponseDTO> response1 = restTemplate.postForEntity(topicsUri, topicEntity1, TopicResponseDTO.class);



        // posts for topic#1
        PostCreateDTO post1 = new PostCreateDTO("Post 1","description","testUrl",response1.getBody().getId());
        PostCreateDTO post2 = new PostCreateDTO("Post 2","description","testUrl",response1.getBody().getId());
        PostCreateDTO post3 = new PostCreateDTO("Post 3","description","testUrl",response1.getBody().getId());

        HttpEntity<PostCreateDTO> postEntity1 = new HttpEntity<>(post1, headers);
        HttpEntity<PostCreateDTO> postEntity2 = new HttpEntity<>(post2, headers);
        HttpEntity<PostCreateDTO> postEntity3 = new HttpEntity<>(post3, headers);

        final String postsUrl = "http://localhost:" + randomServerPort + "/post";
        URI postsUri = new URI(postsUrl);

        restTemplate.postForEntity(postsUri,postEntity1,PostResponseDTO.class);
        restTemplate.postForEntity(postsUri,postEntity2,PostResponseDTO.class);
        restTemplate.postForEntity(postsUri,postEntity3,PostResponseDTO.class);

        final String topicsUrl2 = "http://localhost:" + randomServerPort + "/topicposts/" +response1.getBody().getId();
        URI topicsUri2 = new URI(topicsUrl2);

        HttpEntity<TopicCreateDTO> entity = new HttpEntity<>(headers);
        //WHEN
        ResponseEntity<List<PostGetDTO>> response = restTemplate.exchange(topicsUri2, HttpMethod.GET, entity, new ParameterizedTypeReference<List<PostGetDTO>>() {});
        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());

        Assert.assertEquals("Post 1", response.getBody().get(0).getTitle());
        Assert.assertEquals("Post 2", response.getBody().get(1).getTitle());
        Assert.assertEquals("Post 3", response.getBody().get(2).getTitle());

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
