package vn.ClothingStore.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.ClothingStore.domain.User;
import vn.ClothingStore.domain.request.user.ReqChangePassworDTO;
import vn.ClothingStore.domain.request.user.ReqResetPassworDTO;
import vn.ClothingStore.domain.response.ResultPaginationDTO;
import vn.ClothingStore.domain.response.user.ResCreateUserDTO;
import vn.ClothingStore.domain.response.user.ResUpdateUserDTO;
import vn.ClothingStore.domain.response.user.ResUserDTO;
import vn.ClothingStore.service.OtpService;
import vn.ClothingStore.service.UserService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/search")
    public Page<ResUserDTO> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") String role,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.searchUsers(keyword, role, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + user.getEmail() + "đã tồn tại, vui lòng sử dụng email khác.");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User user1 = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user1));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user success")

    public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }

        this.userService.DeleteUser(id);
        return ResponseEntity.ok(null);

    }

    @GetMapping("users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") int id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.convertToResUserDTO(fetchUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(
                this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("/users/{id}")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@PathVariable("id") int id, @RequestBody User user)
            throws IdInvalidException {
        User ericUser = this.userService.handleUpdateUser(id, user);
        if (ericUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(ericUser));
    }

    @PutMapping("/users/{id}/change-password")
    @ApiMessage("change password success")
    public ResponseEntity<String> changePassword(
            @PathVariable int id,
            @RequestBody ReqChangePassworDTO req) throws IdInvalidException {
        this.userService.changePassword(id, req);
        return ResponseEntity.ok("thay đổi mật khẩu thành công");
    }

    @PostMapping("/users/forgot-password-reset")
    @ApiMessage("forgot password reset success")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ReqResetPassworDTO req) throws IdInvalidException {

        this.userService.resetPassword(req.getEmail(), req.getNewPassword());
        return ResponseEntity.ok("Đặt lại mật khẩu thành công, hãy đăng nhập bằng mật khẩu mới");
    }

}
