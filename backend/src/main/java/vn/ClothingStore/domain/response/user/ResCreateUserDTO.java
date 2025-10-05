package vn.ClothingStore.domain.response.user;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {

    private int id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Instant createdAt;
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
