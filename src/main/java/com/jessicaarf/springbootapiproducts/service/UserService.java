package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.dtos.UserDto;
import com.jessicaarf.springbootapiproducts.exceptions.UsersNotFoundException;
import com.jessicaarf.springbootapiproducts.exceptions.UserAlreadyExistsException;
import com.jessicaarf.springbootapiproducts.exceptions.UserNotFoundException;
import com.jessicaarf.springbootapiproducts.models.Role;
import com.jessicaarf.springbootapiproducts.models.User;
import com.jessicaarf.springbootapiproducts.repositories.RoleRepository;
import com.jessicaarf.springbootapiproducts.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public User createNewUser(UserDto userDto) {

        Role basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        Optional<User> userFromDb = userRepository.findByUsernameIgnoreCase(userDto.username());
        if (userFromDb.isPresent()) {
            log.error("User with username '{}' already exists.", userDto.username());
            throw new UserAlreadyExistsException("User with username already exists.");
        }
        var user = new User();
        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRoles(Set.of(basicRole));
        user.setActive(true);
        return userRepository.save(user);
    }


    public List<User> listAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public List<User> listActiveUsers() {
        List<User> activeUsers = userRepository.findByIsActive(true);
        if (activeUsers.isEmpty()) {
            log.info("No active users found.");
            throw new UsersNotFoundException();
        } else {
            log.info("Retrieved {} active users.", activeUsers.size());
            return activeUsers;
        }
    }

    public List<User> listInactiveUsers() {
        List<User> inactiveUsers = userRepository.findByIsActive(false);

        if (inactiveUsers.isEmpty()) {
            log.info("No inactive users found.");
            throw new UsersNotFoundException();
        } else {
            log.info("Retrieved {} inactive users.", inactiveUsers.size());
            return inactiveUsers;
        }
    }

    public void deactivateUser(UUID id) {
        log.info("deactivating user with id: {}", id);
        Optional<User> userToDeactive = userRepository.findById(id);
        if (userToDeactive.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        User user = userToDeactive.get();
        user.setActive(false);
        userRepository.save(user);
    }

}
