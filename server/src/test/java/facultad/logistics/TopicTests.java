package facultad.logistics;

import facultad.logistics.dto.*;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopicTests {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    TopicRepository topicRepository;

    @Test
    public void testDeleteTopic() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");
        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        TopicCreateDTO topic = new TopicCreateDTO("Topic4", "description");
        HttpEntity<TopicCreateDTO> topicEntity = new HttpEntity<>(topic, headers);

        final String topicsUrl = "http://localhost:" + randomServerPort + "/topic";
        URI topicsUri = new URI(topicsUrl);

        ResponseEntity<TopicResponseDTO> response = restTemplate.postForEntity(topicsUri, topicEntity, TopicResponseDTO.class);
        Long topicId = response.getBody().getId();

        HttpEntity<TopicCreateDTO> entity = new HttpEntity<>(headers);

        final String deleteTopicUrl = String.format("http://localhost:%d/topic/%d", randomServerPort, topicId);
        URI deleteTopicUri = new URI(deleteTopicUrl);

        //WHEN
        ResponseEntity<MessageResponseDTO> deleteResponse = restTemplate.exchange(deleteTopicUri, HttpMethod.DELETE, entity, MessageResponseDTO.class);

        //THEN
        Assert.assertEquals(200, deleteResponse.getStatusCodeValue());
        Assert.assertTrue(topicRepository.getTopicById(topicId).isDeleted());

        ResponseEntity<List<TopicResponseDTO>> topicsResponse = restTemplate.exchange(topicsUri, HttpMethod.GET, entity, new ParameterizedTypeReference<List<TopicResponseDTO>>() {});

        topicsResponse.getBody().forEach(topicResponseDTO -> Assert.assertNotEquals(topicId, topicResponseDTO.getId())); // assert get topics doesn't return the deleted topic
    }

    @Test
    public void testDeleteInvalidTopic() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        Long topicId = Long.MAX_VALUE;
        HttpEntity<TopicCreateDTO> entity = new HttpEntity<>(headers);

        final String deleteTopicUrl = String.format("http://localhost:%d/topic/%d", randomServerPort, topicId);
        URI deleteTopicUri = new URI(deleteTopicUrl);

        try {
            //WHEN
            restTemplate.exchange(deleteTopicUri, HttpMethod.DELETE, entity, MessageResponseDTO.class);

            //THEN
            Assert.fail();
        } catch (HttpClientErrorException e){
            Assert.assertEquals(404,e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Requested topic not found"));
        }
    }

    @Test
    public void testGetPopularTopics() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");
        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        //post new topics
        TopicCreateDTO topic1 = new TopicCreateDTO("T1", "description");
        TopicCreateDTO topic2 = new TopicCreateDTO("T2", "description");
        TopicCreateDTO topic3 = new TopicCreateDTO("T3", "description");

        HttpEntity<TopicCreateDTO> topicEntity1 = new HttpEntity<>(topic1, headers);
        HttpEntity<TopicCreateDTO> topicEntity2 = new HttpEntity<>(topic2, headers);
        HttpEntity<TopicCreateDTO> topicEntity3 = new HttpEntity<>(topic3, headers);

        final String topicsUrl = "http://localhost:" + randomServerPort + "/topic";
        URI topicsUri = new URI(topicsUrl);

        ResponseEntity<TopicResponseDTO> response1 = restTemplate.postForEntity(topicsUri, topicEntity1, TopicResponseDTO.class);
        ResponseEntity<TopicResponseDTO> response2 = restTemplate.postForEntity(topicsUri, topicEntity2, TopicResponseDTO.class);
        ResponseEntity<TopicResponseDTO> response3 = restTemplate.postForEntity(topicsUri, topicEntity3, TopicResponseDTO.class);

        //add different amount of posts to each topic

        int topic1Amount = 15;
        int topic2Amount = 14;
        int topic3Amount = 13;

        //15 posts por topic#3
        for (int i = 0; i < topic1Amount; i++) {
            addPostToTopic(new PostCreateDTO("P" + i, "description", "testurl", response3.getBody().getId()),jwtToken);
        }

        //14 posts for topic#1
        for (int i = topic1Amount; i < topic1Amount + topic2Amount; i++) {
            addPostToTopic(new PostCreateDTO("P" + i, "description", "testurl", response1.getBody().getId()),jwtToken);
        }

        //13 posts for topic#2
        for (int i = topic1Amount + topic2Amount; i < topic1Amount + topic2Amount + topic3Amount; i++) {
            addPostToTopic(new PostCreateDTO("P" + i, "description", "testurl", response2.getBody().getId()),jwtToken);
        }

        HttpEntity<TopicCreateDTO> entity = new HttpEntity<>(headers);
        //WHEN
        ResponseEntity<List<TopicResponseDTO>> response = restTemplate.exchange(topicsUri, HttpMethod.GET, entity, new ParameterizedTypeReference<List<TopicResponseDTO>>() {});
        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());

        Assert.assertEquals("T3", response.getBody().get(0).getTitle()); //topic#3 with 15 posts
        Assert.assertEquals("T1", response.getBody().get(1).getTitle()); //topic#1 with 14 posts
        Assert.assertEquals("T2", response.getBody().get(2).getTitle()); //topic#2 with 13 posts

    }

    @Test
    public void testTopicCreation() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        TopicCreateDTO body = new TopicCreateDTO("TestTitle", "Test Description");

        HttpEntity<TopicCreateDTO> entity = new HttpEntity<>(body, headers);
        final String createTopicUrl = "http://localhost:" + randomServerPort + "/topic";
        URI createTopicUri = new URI(createTopicUrl);

        //WHEN
        ResponseEntity<TopicResponseDTO> response = restTemplate.postForEntity(createTopicUri, entity, TopicResponseDTO.class);

        //THEN
        Assert.assertEquals(201, response.getStatusCodeValue());
        Assert.assertEquals("TestTitle", response.getBody().getTitle());
        Assert.assertEquals("Test Description", response.getBody().getDescription());
    }

    @Test
    public void testInvalidTitle() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        TopicCreateDTO body = new TopicCreateDTO("TestTitle1", "Test Description");

        HttpEntity<TopicCreateDTO> entity = new HttpEntity<>(body, headers);
        final String createTopicUrl = "http://localhost:" + randomServerPort + "/topic";
        URI createTopicUri = new URI(createTopicUrl);

        restTemplate.postForEntity(createTopicUri, entity, TopicResponseDTO.class); // post 1st topic

        TopicCreateDTO body2 = new TopicCreateDTO("TestTitle1", "Description");

        HttpEntity<TopicCreateDTO> entity2 = new HttpEntity<>(body2, headers);

        try {
        //WHEN
            restTemplate.postForEntity(createTopicUri, entity2, TopicResponseDTO.class); // post 2nd topic with same title as first

        //THEN
            Assert.fail();
        } catch (HttpClientErrorException e){
            Assert.assertEquals(409,e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Title TestTitle1 already in use"));
        }
    }

  @Test
  public void testGetPostsByTopic() throws URISyntaxException {
    //GIVEN
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");
    String jwtToken = loginResponse.getBody().getToken();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + jwtToken);

    //post new topics
    TopicCreateDTO topic1 = new TopicCreateDTO("Topic1", "description");


    HttpEntity<TopicCreateDTO> topicEntity1 = new HttpEntity<>(topic1, headers);

    final String topicsUrl = "http://localhost:" + randomServerPort + "/topic";
    URI topicsUri = new URI(topicsUrl);

    ResponseEntity<TopicResponseDTO> response1 = restTemplate.postForEntity(topicsUri, topicEntity1, TopicResponseDTO.class);



    // posts for topic#1
    PostCreateDTO post1 = new PostCreateDTO("Post1","description","testUrl",response1.getBody().getId());
    PostCreateDTO post2 = new PostCreateDTO("Post2","description","testUrl",response1.getBody().getId());
    PostCreateDTO post3 = new PostCreateDTO("Post3","description","testUrl",response1.getBody().getId());

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

    Assert.assertEquals("Post1", response.getBody().get(0).getTitle());
    Assert.assertEquals("Post2", response.getBody().get(1).getTitle());
    Assert.assertEquals("Post3", response.getBody().get(2).getTitle());

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

    private void addPostToTopic(PostCreateDTO post, String jwt) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        HttpEntity<PostCreateDTO> postEntity = new HttpEntity<>(post, headers);

        final String postsUrl = "http://localhost:" + randomServerPort + "/post";
        URI postsUri = new URI(postsUrl);

        restTemplate.postForEntity(postsUri,postEntity, PostResponseDTO.class);
    }
}
