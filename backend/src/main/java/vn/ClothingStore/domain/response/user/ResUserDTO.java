package vn.ClothingStore.domain.response.user;

import java.time.Instant;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ResUserDTO {

    private int id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Instant createdAt;
    private boolean facebookLinked;
    private boolean googleLinked;
    private Instant updateAt;

    private RoleDTO role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDTO {
        private long id;
        private String name;
    }

}
