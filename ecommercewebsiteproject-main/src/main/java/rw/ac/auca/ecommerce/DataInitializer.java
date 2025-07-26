package rw.ac.auca.ecommerce;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rw.ac.auca.ecommerce.core.user.RoleName;
import rw.ac.auca.ecommerce.core.user.model.Role;
import rw.ac.auca.ecommerce.core.user.repositories.RoleRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initializeRoles();
    }

    private void initializeRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName) == null) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                roleRepository.save(role);
                System.out.println("Created role: " + roleName);
            }
        }
    }
}