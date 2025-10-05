package vn.ClothingStore.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.ClothingStore.domain.Role;
import vn.ClothingStore.domain.User;
import vn.ClothingStore.domain.request.user.ReqChangePassworDTO;
import vn.ClothingStore.domain.response.ResultPaginationDTO;
import vn.ClothingStore.domain.response.user.ResCreateUserDTO;
import vn.ClothingStore.domain.response.user.ResUpdateUserDTO;
import vn.ClothingStore.domain.response.user.ResUserDTO;
import vn.ClothingStore.repository.RoleRepository;
import vn.ClothingStore.repository.UserRepository;
import vn.ClothingStore.specifications.UserSpecs;
import vn.ClothingStore.util.error.IdInvalidException;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, RoleService roleService, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    public Page<ResUserDTO> searchUsers(String keyword, String role, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.or(UserSpecs.hasKeyword(keyword)
                    .or(UserSpecs.hasPhoneName(keyword)));
        }
        if (role != null && !role.isBlank()) {
            spec = spec.or(UserSpecs.hasRole(role));
        }

        Page<User> page = (Page<User>) userRepository.findAll(spec, pageable);

        return page.map(this::convertToResUserDTO);
    }

    public User createUser(User user) {
        // luôn set role mặc định là USER
        Role defaultRole = this.roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));

        user.setRole(defaultRole);

        return this.userRepository.save(user);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {

        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setDateOfBirth(user.getDateOfBirth());
        res.setCreatedAt(user.getCreatedAt());

        if (user.getRole() != null) {
            res.setRole(new ResCreateUserDTO.RoleDTO(
                    user.getRole().getId(),
                    user.getRole().getName()));
        }

        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setAddress(user.getAddress());
        res.setDateOfBirth(user.getDateOfBirth());
        res.setFacebookLinked(user.isFacebookLinked());
        res.setGoogleLinked(user.isGoogleLinked());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdateAt(user.getUpdatedAt());

        if (user.getRole() != null) {
            res.setRole(new ResUserDTO.RoleDTO(
                    user.getRole().getId(),
                    user.getRole().getName()));
        }

        return res;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User fetchUserById(int id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public void DeleteUser(int id) {
        this.userRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public User handleUpdateUser(int id, User user) {
        User currentUser = this.fetchUserById(id);
        if (currentUser != null) {

            currentUser.setFullName(user.getFullName());
            currentUser.setAddress(user.getAddress());
            currentUser.setDateOfBirth(user.getDateOfBirth());

            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setAddress(user.getAddress());
        res.setUpdateAt(user.getUpdatedAt());

        if (user.getRole() != null) {
            res.setRole(new ResUpdateUserDTO.RoleDTO(
                    user.getRole().getId(),
                    user.getRole().getName()));
        }

        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    // change password
    public void changePassword(int id, ReqChangePassworDTO req) throws IdInvalidException {
        User currentUser = this.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id " + id + "không tồn tại");
        }

        if (!passwordEncoder.matches(req.getOldPassword(), currentUser.getPassword())) {
            throw new IdInvalidException("Mật khẩu cũ không đúng");
        }
        if (!req.getNewPassword().equals(req.getReEnterPassword())) {
            throw new IdInvalidException("nhập lại mật khẩu không đúng");
        }
        currentUser.setPassword(passwordEncoder.encode(req.getNewPassword()));

        this.userRepository.save(currentUser);

    }

    // reset password
    public void resetPassword(String email, String newPassword) throws IdInvalidException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("Email không tồn tại trong hệ thống");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
