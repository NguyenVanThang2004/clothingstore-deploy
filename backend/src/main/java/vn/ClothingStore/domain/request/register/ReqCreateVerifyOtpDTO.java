package vn.ClothingStore.domain.request.register;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateVerifyOtpDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String password;
    @NotBlank
    private String otp;

}
