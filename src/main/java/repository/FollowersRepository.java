package repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import model.Follower;
import model.User;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FollowersRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){

        var params = Parameters.with("follower", follower)
                .and("user", user).map();
        PanacheQuery<Follower> query = find("follower =:follower and user =:user", params);
        Optional<Follower> result = query.firstResultOptional();


        return result.isPresent();

    }

    public List<Follower> findByUser(Long userId){

       PanacheQuery<Follower> query =  find("user.id", userId);

        return query.list();

    }

    public void deleteByFollowerAndUser( Long followerId,  Long userId){

        var params = Parameters.with("userId", userId).and("followerId", followerId).map();

        delete("follower.id =:followerId and user.id =:userId", params);
    }





}
