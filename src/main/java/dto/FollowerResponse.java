package dto;


import lombok.Data;
import model.Follower;

@Data
public class FollowerResponse {

    private Long id;
    private String name;

    public FollowerResponse(){}


    public FollowerResponse(Follower follower){
        this(follower.getId(), follower.getFollower().getNome());
    }

    public FollowerResponse(Long id,String name ){
        this.id = id;
        this.name = name;
    }

}
