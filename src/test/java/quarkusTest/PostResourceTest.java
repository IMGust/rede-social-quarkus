package quarkusTest;


import dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.Follower;
import model.Post;
import model.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.FollowersRepository;
import repository.PostRespository;
import repository.UserRepository;
import resource.PostResource;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository repository;

    @Inject
    FollowersRepository followersRepository;

    @Inject
    PostRespository postRespository;

    Integer userId;
    Integer userNotFollowerId;
    Integer userFollowerId;

    @BeforeEach
    @Transactional
    public void setUp(){

        //usuário
        var user = new User();
        user.setNome("Fulano");
        user.setIdade(18);
        repository.persist(user);
        userId = user.getId();

        //criação de post p/ o usuário
        Post post = new Post();
        post.setText("Iae");
        post.setUser(user);
        postRespository.persist(post);

        //usuário não seguidor
        var userNotFollower = new User();
        userNotFollower .setNome("Cicrano");
        userNotFollower .setIdade(33);
        repository.persist(userNotFollower );
        userNotFollowerId = userNotFollower.getId();

        // usuário seguidor
        var userFollower = new User();
        userFollower.setNome("Betrano");
        userFollower.setIdade(27);
        repository.persist(userFollower);

        // ✅
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followersRepository.persist(follower);


    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPosttest(){

        var userID = 1;

        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");
      given()
              .contentType(ContentType.JSON)
              .body(postRequest)
              .pathParams("userId", userId)
              .when()
              .post()
              .then().statusCode(201);


    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){

        var inexistentUserID = 999;
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParams("userId", inexistentUserID)
                .when()
                .post()
                .then().statusCode(404);


    }

    @Test
    @DisplayName("should return 404 when user doens't exist")
    public void listPostUserNotFoundTest(){

        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerNotFoundTest(){


        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));

    }


    @Test
    @DisplayName("should return 403 when follower doens't exist")
    public void listPostFollowerHeaderNotFoundTest(){

        var inexistentFollowerId = 999;

        given()
                .pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));

    }

    @Test
    @DisplayName("should return 403 when isn't a follower")
    public void listPostNotFollower(){

        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));


    }

    @Test
    @DisplayName("should return posts")
    public void listPostTest(){

        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));

    }

}