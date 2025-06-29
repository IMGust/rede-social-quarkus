package resource;

import dto.CreatePostRequest;
import dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Post;
import model.User;
import repository.PostRespository;
import repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    UserRepository userRepository;

    @Inject
    PostRespository postRespository;

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){
      User user = userRepository.findById(userId);
      if(user == null){
          return Response.status(Response.Status.NOT_FOUND).build();
      }
        Post post = new Post();
        post.setUser(user);
        post.setText(user.getNome());
        post.prePresist();
        postRespository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

            PanacheQuery<Post> query = postRespository.find("user",
                    Sort.by("dateTime", Sort.Direction.Descending), user);
            List<Post> list = query.list();
            list.stream().map(PostResponse::fromEntity).
                    collect(Collectors.toList());

        return Response.ok(list).build();
    }
}
