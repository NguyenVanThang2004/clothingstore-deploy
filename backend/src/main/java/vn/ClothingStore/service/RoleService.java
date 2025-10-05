package vn.ClothingStore.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.ClothingStore.domain.Role;
import vn.ClothingStore.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role fetchById(int id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent())
            return roleOptional.get();
        return null;
    }

}
