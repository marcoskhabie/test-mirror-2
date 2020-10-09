package facultad.logistics;

import facultad.logistics.dto.post.PostCreateDTO;
import facultad.logistics.dto.post.PostEditDTO;
import facultad.logistics.dto.post.PostResponseDTO;
import facultad.logistics.dto.topic.TopicCreateDTO;
import facultad.logistics.dto.topic.TopicResponseDTO;
import facultad.logistics.dto.user.JwtResponseDTO;
import facultad.logistics.dto.user.LoginDTO;
import facultad.logistics.model.Post;
import facultad.logistics.repository.PostRepository;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostTests {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    PostRepository postRepository;

    @Test
    public void testPostCreation() throws URISyntaxException {
        //GIVEN
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();

        //post new Topic
        ResponseEntity<TopicResponseDTO> topicResponse = postTopic(jwtToken, "testTopic4", "test description");

        //WHEN
        ResponseEntity<PostResponseDTO> postResponse = postPost(jwtToken, "testTitle4", "test description", "testLink.com", topicResponse.getBody().getId());

        //THEN
        Assert.assertEquals(201, postResponse.getStatusCodeValue());
        Assert.assertEquals("testTitle4", postResponse.getBody().getTitle());
        Assert.assertEquals("test description", postResponse.getBody().getDescription());
        Assert.assertEquals("testLink.com", postResponse.getBody().getLink());

        Optional<Post> post = postRepository.findById(postResponse.getBody().getId());
        Assert.assertTrue(post.isPresent());
        Assert.assertEquals("testTitle4", post.get().getTitle());
        Assert.assertEquals("test description", post.get().getDescription());
        Assert.assertEquals("testLink.com", post.get().getLink());
    }

    @Test
    public void testPostCreationWithInvalidTitle() throws URISyntaxException {
        //GIVEN
        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();

        //post new Topic
        ResponseEntity<TopicResponseDTO> topicResponse = postTopic(jwtToken, "testTopic5", "test description");

        //post new post
        postPost(jwtToken, "usedPostTitle", "test description", "testLink.com", topicResponse.getBody().getId());
        Long bodyId = topicResponse.getBody().getId();

        try {
            //WHEN trying to create a new post with an already used title
            postPost(jwtToken, "usedPostTitle", "test description", "testLink.com", bodyId);

            //THEN
            Assert.fail();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(409, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Title usedPostTitle already in use"));
        }
    }

    public void testPostEdit() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();

        //post new Topic
        ResponseEntity<TopicResponseDTO> topicResponse = postTopic(jwtToken, "testTopic", "test description");

        //post new Post
        ResponseEntity<PostResponseDTO> postResponse = postPost(jwtToken, "testTitle", "test description", "testLink.com", topicResponse.getBody().getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        PostEditDTO editedPost = new PostEditDTO("newTitle", "New description", "newLink.com");
        HttpEntity<PostEditDTO> postEditEntity = new HttpEntity<>(editedPost, headers);
        final String postEditUrl = String.format("http://localhost:%d/post/%d", randomServerPort, postResponse.getBody().getId());
        URI postEditUri = new URI(postEditUrl);

        //WHEN
        ResponseEntity<PostResponseDTO> response = restTemplate.exchange(postEditUri, HttpMethod.PUT, postEditEntity, PostResponseDTO.class);

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(postResponse.getBody().getId(), response.getBody().getId()); //keeps same id as original post
        Assert.assertEquals("newTitle", response.getBody().getTitle()); //title is edited to new title
        Assert.assertEquals("New description", response.getBody().getDescription()); // description is edited to new description
        Assert.assertEquals("newLink.com", response.getBody().getLink()); // link is edited to new link

        Optional<Post> post = postRepository.findById(response.getBody().getId()); // Post also updated on db
        Assert.assertTrue(post.isPresent());
        Assert.assertEquals("newTitle", post.get().getTitle());
        Assert.assertEquals("New description", post.get().getDescription());
        Assert.assertEquals("newLink.com", post.get().getLink());
    }

    @Test
    public void testPostEditSingleValue() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();

        //post new Topic
        ResponseEntity<TopicResponseDTO> topicResponse = postTopic(jwtToken, "testTopic2", "test description");

        //post new Post
        ResponseEntity<PostResponseDTO> postResponse = postPost(jwtToken, "testTitle2", "test description", "testLink.com", topicResponse.getBody().getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        PostEditDTO editedPost = new PostEditDTO("newTitle2", null, null);
        HttpEntity<PostEditDTO> postEditEntity = new HttpEntity<>(editedPost, headers);
        final String postEditUrl = String.format("http://localhost:%d/post/%d", randomServerPort, postResponse.getBody().getId());
        URI postEditUri = new URI(postEditUrl);

        //WHEN
        ResponseEntity<PostResponseDTO> response = restTemplate.exchange(postEditUri, HttpMethod.PUT, postEditEntity, PostResponseDTO.class);

        //THEN
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(postResponse.getBody().getId(), response.getBody().getId()); //keeps same id as original post
        Assert.assertEquals("newTitle2", response.getBody().getTitle()); //title is edited to new title
        Assert.assertEquals("test description", response.getBody().getDescription()); // description remains unchanged
        Assert.assertEquals("testLink.com", response.getBody().getLink()); // link remains unchanged

        Optional<Post> post = postRepository.findById(response.getBody().getId()); // Post also updated on db
        Assert.assertTrue(post.isPresent());
        Assert.assertEquals("newTitle2", post.get().getTitle());
        Assert.assertEquals("test description", post.get().getDescription());
        Assert.assertEquals("testLink.com", post.get().getLink());
    }

    @Test
    public void testPostEditWithInvalidTitle() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();

        //post new Topic
        ResponseEntity<TopicResponseDTO> topicResponse = postTopic(jwtToken, "testTopic3", "test description");

        //post new Post
        ResponseEntity<PostResponseDTO> postResponse = postPost(jwtToken, "testTitle3", "test description", "testLink.com", topicResponse.getBody().getId());
        //post another Post
        postPost(jwtToken, "usedTestTitle", "test description", "testLink.com", topicResponse.getBody().getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        PostEditDTO editedPost = new PostEditDTO("usedTestTitle", "description", "link");
        HttpEntity<PostEditDTO> postEditEntity = new HttpEntity<>(editedPost, headers);
        final String postEditUrl = String.format("http://localhost:%d/post/%d", randomServerPort, postResponse.getBody().getId());
        URI postEditUri = new URI(postEditUrl);

        try {
        //WHEN editing post with an already used title
            restTemplate.exchange(postEditUri, HttpMethod.PUT, postEditEntity, PostResponseDTO.class);
        //THEN
            Assert.fail();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(409, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Title usedTestTitle already in use"));
        }
    }

    @Test
    public void testPostEditWithInvalidId() throws URISyntaxException {
        //GIVEN
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JwtResponseDTO> loginResponse = loginUser("admin@gmail.com", "admin");

        String jwtToken = loginResponse.getBody().getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        PostEditDTO editedPost = new PostEditDTO("newTitle", "description", "link");
        HttpEntity<PostEditDTO> postEditEntity = new HttpEntity<>(editedPost, headers);
        final String postEditUrl = String.format("http://localhost:%d/post/%d", randomServerPort, Long.MAX_VALUE);
        URI postEditUri = new URI(postEditUrl);

        try {
        //WHEN
            restTemplate.exchange(postEditUri, HttpMethod.PUT, postEditEntity, PostResponseDTO.class);
        //THEN
            Assert.fail();
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(404, e.getRawStatusCode());
            Assert.assertTrue(e.getResponseBodyAsString().contains("Requested post not found"));
        }
    }

    private ResponseEntity<TopicResponseDTO> postTopic(String jwt, String title, String description) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        TopicCreateDTO topic = new TopicCreateDTO(title, description);
        HttpEntity<TopicCreateDTO> topicEntity = new HttpEntity<>(topic, headers);

        final String url = "http://localhost:" + randomServerPort + "/topic";
        URI uri = new URI(url);

        return restTemplate.postForEntity(uri, topicEntity, TopicResponseDTO.class);
    }

    private ResponseEntity<PostResponseDTO> postPost(String jwt, String title, String description, String link, Long topicId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);

        PostCreateDTO topic = new PostCreateDTO(title, description, link, topicId);
        HttpEntity<PostCreateDTO> topicEntity = new HttpEntity<>(topic, headers);

        final String url = "http://localhost:" + randomServerPort + "/post";
        URI uri = new URI(url);

        return restTemplate.postForEntity(uri, topicEntity, PostResponseDTO.class);
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
