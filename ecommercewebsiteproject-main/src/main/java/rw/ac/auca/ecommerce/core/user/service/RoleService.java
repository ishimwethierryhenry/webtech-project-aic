package rw.ac.auca.ecommerce.core.user.service;

import rw.ac.auca.ecommerce.core.user.RoleName;
import rw.ac.auca.ecommerce.core.user.model.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface RoleService {
    void deleteRole(UUID id);

    List<Role> findAll();

    List<Role> findAllRoles();

    Role findById(UUID id);

    Role saveRole(Role role);

    List<Role> findAllById(Set<UUID> ids);

    Set<Role> findByNames(Set<RoleName> names);
}
