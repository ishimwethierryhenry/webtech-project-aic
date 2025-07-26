package rw.ac.auca.ecommerce.core.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.ac.auca.ecommerce.core.user.RoleName;
import rw.ac.auca.ecommerce.core.user.model.Role;
import rw.ac.auca.ecommerce.core.user.repositories.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
    @Override
    public List<Role> findAllById(Set<UUID> ids) {
        return roleRepository.findAllById(ids);
    }
    @Override
    public Set<Role> findByNames(Set<RoleName> names) {
        return new HashSet<>(roleRepository.findByNameIn(names));
    }

    @Override
    public Role findById(UUID id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
