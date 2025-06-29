package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.Post;

@ApplicationScoped
public class PostRespository implements PanacheRepository<Post> {
}
