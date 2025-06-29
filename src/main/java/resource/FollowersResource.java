package resource;

import dto.FollowRequest;
import dto.FollowerResponse;
import dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Follower;
import model.User;
import org.jboss.logging.annotations.Param;
import repository.FollowersRepository;
import repository.UserRepository;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FollowersResource {

        @Inject
        FollowersRepository followersRepository;

        @Inject
        UserRepository userRepository;

        @Transactional
        @PUT
        public Response followUer(
                @PathParam("userId") Long userId, FollowRequest request){

                if(userId.equals(request.getFollowersId())){

                    return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
                }

                var user =  userRepository.findById(userId);
                if(user == null) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                var follower =  userRepository.findById(request.getFollowersId());

             boolean follows = followersRepository.follows(follower, user);

             if(!follows){

            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            followersRepository.persist(entity);
             }

            return  Response.status(Response.Status.NO_CONTENT).build();

        }

        @GET
        public Response listFollowers(@PathParam("userId") Long userId){
            var list = followersRepository.findByUser(userId);

            var user =  userRepository.findById(userId);
            if(user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
            responseObject.setFollowerCount(list.size());
            var followerList = list.stream().map(FollowerResponse::new)
                    .collect(Collectors.toList());

            responseObject.setContent(followerList);
            return Response.ok(responseObject).build();
        }















}
