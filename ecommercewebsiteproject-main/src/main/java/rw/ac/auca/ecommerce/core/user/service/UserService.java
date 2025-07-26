package rw.ac.auca.ecommerce.core.user.service;

import rw.ac.auca.ecommerce.core.user.model.User;

import java.util.List;
import java.util.UUID;


public interface UserService {
    void save(User user);
    List<User> findAll();
    List<User> findAllUsers();


    User findById(UUID id);
    boolean existsByEmail(String email);


    User saveUser(User user);
    void deleteUser(UUID id);


}
