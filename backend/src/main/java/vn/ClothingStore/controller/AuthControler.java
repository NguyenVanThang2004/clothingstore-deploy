package vn.ClothingStore.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import vn.ClothingStore.domain.User;
import vn.ClothingStore.domain.request.login.ReqLoginDTO;
import vn.ClothingStore.domain.request.register.ReqCreateVerifyOtpDTO;
import vn.ClothingStore.domain.response.ResLoginDTO;
import vn.ClothingStore.domain.response.ResLoginDTO.UserLogin;
import vn.ClothingStore.domain.response.user.ResCreateUserDTO;
import vn.ClothingStore.service.EmailService;
import vn.ClothingStore.service.OtpService;
import vn.ClothingStore.service.UserService;
import vn.ClothingStore.util.SecurityUtil;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class AuthControler {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;

    @Value("${backend.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthControler(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder, EmailService emailService,
            OtpService otpService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @GetMapping("/auth/me")
    public Object me(Authentication authentication) {
        System.out.println(">>> authorities = " + authentication.getAuthorities());
        return authentication;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {

        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken); // import//
                                                                                                                    // org.springframework.security.core.Authentication;

        // create token
        // set thong tin nguoi dung dang nhap vao context(co the dung cho sau nay)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();

        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getEmail());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getFullName());

            if (currentUserDB.getRole() != null) {
                userLogin.setRole(new ResLoginDTO.RoleDTO(
                        currentUserDB.getRole().getId(),
                        currentUserDB.getRole().getName()));
            }

            res.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
        res.setAccessToken(access_token);

        // create refresh token
        String refresh_token = this.securityUtil.CreateRefreshToken(loginDTO.getEmail(), res);

        // update user
        this.userService.updateUserToken(refresh_token, loginDTO.getEmail());

        // set cookies

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUserDB != null) {

            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getFullName());
            userGetAccount.setUser(userLogin);

        }

        return ResponseEntity.ok().body(userGetAccount);

    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token)
            throws IdInvalidException {
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);

        if (currentUser == null) {
            throw new IdInvalidException("refresh token khong hop le");
        }

        ResLoginDTO res = new ResLoginDTO();

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getFullName());

            if (currentUserDB.getRole() != null) {
                userLogin.setRole(new ResLoginDTO.RoleDTO(
                        currentUserDB.getRole().getId(),
                        currentUserDB.getRole().getName()));
            }

            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(email, res.getUser());
        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.CreateRefreshToken(email, res);

        // update user
        this.userService.updateUserToken(new_refresh_token, email);

        // set cookies

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refresh token = null
        this.userService.updateUserToken(null, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");

        }

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User TUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(TUser));
    }

    @PostMapping("/auth/verify-register-otp")
    @ApiMessage("Register a new user")
    public ResponseEntity<String> verigyRegisterOtp(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");

        }

        this.emailService.sendEmailFromTemplateSync(user.getEmail(), "Xác thực tài toản", "templateVerifyEmail",
                this.otpService.generateOtp6Digits(user.getEmail()));

        return ResponseEntity.ok("OTP đã gửi qua email ,vui lòng xác thực tài khoản");
    }

    @PostMapping("/auth/create-verify-otp")
    @ApiMessage("Verify OTP and create account")
    public ResponseEntity<String> createAccountAfterVerifyOtp(
            @Valid @RequestBody ReqCreateVerifyOtpDTO req) throws IdInvalidException {

        boolean valid = this.otpService.verifyOtp(req.getEmail(), req.getOtp());
        if (!valid) {
            throw new IdInvalidException("OTP không hợp lệ hoặc đã hết hạn");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        this.userService.createUser(user);

        return ResponseEntity.ok("Tạo tài khoản thành công, bạn có thể đăng nhập!");
    }

    @GetMapping("/auth/forgot-password-send-email")
    @ApiMessage("forgot password send email success")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(email);
        if (!isEmailExist) {
            throw new IdInvalidException("vui lòng nhập lại email bạn đã đăng kí");

        }

        String otpCode = otpService.generateOtp6Digits(email);
        emailService.sendEmailFromTemplateSync(
                email,
                "Xác thực quên mật khẩu",
                "templateForgotPassword",
                otpCode);

        return ResponseEntity.ok("OTP đã được gửi về email " + email);
    }

    @PostMapping("/auth/forgot-password-verify-otp")
    @ApiMessage("forgot password verify otp success")
    public ResponseEntity<String> verifyOtp(
            @RequestParam @Email String email,
            @RequestParam @Pattern(regexp = "\\d{6}") String otp) {

        // 1. Kiểm tra email có tồn tại trong hệ thống không
        User user = this.userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Email không tồn tại trong hệ thống");
        }

        // 2. Kiểm tra OTP hợp lệ
        boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            return ResponseEntity.badRequest().body("OTP không hợp lệ hoặc đã hết hạn");
        }
        return ResponseEntity.ok("Xác thực OTP thành công, hãy đặt mật khẩu mới");
    }

}
