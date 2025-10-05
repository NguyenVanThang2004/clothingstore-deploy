package vn.ClothingStore.domain.request.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "email khong duoc de trong")
    private String email;
    @NotBlank(message = "password khong duoc de trong")
    private String password;

}
