package vn.ClothingStore.domain.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqChangePassworDTO {
    private String oldPassword;
    private String newPassword;
    private String reEnterPassword;
}
