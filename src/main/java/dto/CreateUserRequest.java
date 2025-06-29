package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Name is Require")
    private String nome;
    @NotNull(message = "Age is Require")
    private Integer idade;

}
