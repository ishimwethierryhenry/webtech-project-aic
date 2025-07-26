package rw.ac.auca.ecommerce.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rw.ac.auca.ecommerce.core.user.model.Role;
import rw.ac.auca.ecommerce.core.user.model.User;
import rw.ac.auca.ecommerce.core.user.service.RoleService;
import rw.ac.auca.ecommerce.core.user.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    // Show form to create a new user
    @GetMapping("/new")
    public String showUserForm(Model model) {
        try {
            // Pass actual Role entities, not enum values
            List<Role> roles = roleService.findAll();
            System.out.println("Found " + roles.size() + " roles");
            for (Role role : roles) {
                System.out.println("Role: " + role.getName() + " (ID: " + role.getId() + ")");
            }
            model.addAttribute("roles", roles);
            return "user/user_form";
        } catch (Exception e) {
            System.err.println("Error loading roles: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading roles: " + e.getMessage());
            return "user/user_form";
        }
    }

    // Show form to edit an existing user
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") UUID id, Model model) {
        User user = userService.findById(id);
        List<Role> roles = roleService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "user/user_form";
    }

    // Save or update user
    @PostMapping("/save")
    public String saveUser(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           @RequestParam(value = "roles", required = false) List<String> roleIds,
                           RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Saving user: " + username + ", email: " + email);
            System.out.println("Selected role IDs: " + roleIds);

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            // Handle roles
            Set<Role> selectedRoles = new HashSet<>();
            if (roleIds != null && !roleIds.isEmpty()) {
                System.out.println("Processing " + roleIds.size() + " role IDs");
                // Convert String UUIDs to UUID objects and fetch roles
                Set<UUID> uuidSet = roleIds.stream()
                        .map(roleIdStr -> {
                            System.out.println("Converting role ID: " + roleIdStr);
                            return UUID.fromString(roleIdStr);
                        })
                        .collect(Collectors.toSet());
                selectedRoles = new HashSet<>(roleService.findAllById(uuidSet));
                System.out.println("Found " + selectedRoles.size() + " roles for user");
            } else {
                System.out.println("No roles selected for user");
            }
            user.setRoles(selectedRoles);

            User savedUser = userService.saveUser(user);
            System.out.println("User saved with ID: " + savedUser.getId());

            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            return "redirect:/users/all";

        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "redirect:/users/new";
        }
    }

    // List all users
    @GetMapping("/all")
    public String listUsers(Model model) {
        try {
            List<User> users = userService.findAll();
            model.addAttribute("users", users);
            return "user/user_list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading users: " + e.getMessage());
            return "user/user_list";
        }
    }

    // Delete a user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/users/all";
    }
}