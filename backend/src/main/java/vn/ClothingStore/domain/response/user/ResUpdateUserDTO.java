package vn.ClothingStore.domain.response.user;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateUserDTO {
    private int id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Instant updateAt;

    private RoleDTO role; // thêm role object

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDTO {
        private long id;
        private String name;
    }
}
