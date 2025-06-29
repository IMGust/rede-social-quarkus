package resource;

import dto.CreateUserRequest;
import dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.User;
import repository.UserRepository;

import java.util.Set;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class UserResource {

    private UserRepository repository;
    private Validator validator;
    @Inject
    UserResource(UserRepository repository, Validator validator){
        this.validator = validator;
        this.repository = repository;
    }


    @POST
    @Transactional
    public Response criar(CreateUserRequest create){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(create);
        if(!violations.isEmpty()){
            return ResponseError.createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }


        User user = new User();
        user.setNome(create.getNome());
        user.setIdade(create.getIdade());
        repository.persist(user);
        return Response.status(Response.Status.CREATED.getStatusCode()).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, CreateUserRequest create) {
            User user = repository.findById(id);

            if(user != null){ user.setNome(create.getNome());
                user.setIdade(create.getIdade());
                return Response.noContent().build();
            }
               return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id){
            User user = repository.findById(id);
            if(user != null) {
                repository.delete(user);
                return Response.noContent().build();
            }
                return Response.status(Response.Status.NOT_FOUND).build();

    }

    @GET
    public Response mostrar(){
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

}
