package vn.ClothingStore.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private int id;
        private String email;
        private String name;
        private RoleDTO role; // thêm role object
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDTO {
        private long id;
        private String name;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
    }

}
