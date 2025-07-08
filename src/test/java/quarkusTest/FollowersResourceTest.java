package quarkusTest;

import dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import model.Follower;
import model.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import repository.FollowersRepository;
import repository.UserRepository;
import resource.FollowersResource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowersResource.class)
class FollowersResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowersRepository followersRepository;

    Long userId;
    Long followId;

    @BeforeEach
    @Transactional
    public void setUp() {
        //usuário
        var user = new User();
        user.setNome("Fulano");
        user.setIdade(18);
        userRepository.persist(user);
        userId = (long) user.getId();

        //o seguidor
        var userFollower = new User();
        userFollower.setNome("Ciclano");
        userFollower.setIdade(36);
        userRepository.persist(userFollower);
        followId = (long) userFollower.getId();

        //cria um follower
        var followerCreate = new Follower();
        followerCreate.setFollower(userFollower);
        followerCreate.setUser(user);
        followersRepository.persist(followerCreate);


    }

    @Test
    @DisplayName("should return 409 when Follower Id is equal to User id")
    public void sameUserAsFolllowerTest(){

            var body = new FollowerRequest();
            body.setFollowersId(Long.valueOf(userId));

         given()
                 .contentType(ContentType.JSON)
                 .body(body)
                 .pathParam("userId", userId)
                 .when()
                 .put()
                 .then().statusCode(Response.Status.CONFLICT.getStatusCode())
                 .body(Matchers.is("You can't follow yourself"));

    }

    @Test
    @DisplayName("should return  404 on a follower user when User id doen't exist")
    public void userNotFoundWhenTryToFollowTest(){

        var body = new FollowerRequest();
        body.setFollowersId(Long.valueOf(userId));
        var inexistentUserid = 999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserid)
                .when()
                .put()
                .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }

    @Test
    @DisplayName("should follow a user")
    public void FollowUserTest(){

        var body = new FollowerRequest();
        body.setFollowersId(Long.valueOf(followId));

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }

    @Test
    @DisplayName("should return  404 on list user  followers and user when User id doen't exist")
    public void userNotFoundWhenListingFollowTest(){

        var inexistentUserid = 999;

        given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", inexistentUserid)
                        .when()
                        .get()
                        .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }


    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest(){

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
                .when()
                .get()
                .then().extract().response();

                var followerCount = response.jsonPath().get("followerCount");
                var followersContent = response.jsonPath().getList("content");

                assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
                assertEquals(1, followerCount);

                assertEquals(1, followersContent.size());

    }

    @Test
    @DisplayName("should return  404 on unfollow user and User id doen't exist")
    public void userNotFoundWhenUnfollowingAuserTest(){

        var inexistentUserid = 999;

        given()
                .pathParam("userId", inexistentUserid)
                .queryParam("followId", followId)
                .when()
                .delete()
                .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should Unfollow an user")
    public void unfollowUserTest(){

        given()
                .pathParam("userId", userId)
                .queryParam("followId", followId)
                .when()
                .delete()
                .then().statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }


}