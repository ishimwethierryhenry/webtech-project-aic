package rw.ac.auca.ecommerce.core.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import rw.ac.auca.ecommerce.core.user.model.Role;
import rw.ac.auca.ecommerce.core.user.RoleName;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    List<Role> findByNameIn(Set<RoleName> names);
    Optional<Role> findByName(RoleName name);
}




