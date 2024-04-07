package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.dtos.UserDto;
import com.jessicaarf.springbootapiproducts.exceptions.NoUserFoundException;
import com.jessicaarf.springbootapiproducts.exceptions.UserAlreadyExistsException;
import com.jessicaarf.springbootapiproducts.exceptions.UserNotFoundException;
import com.jessicaarf.springbootapiproducts.models.RoleModel;
import com.jessicaarf.springbootapiproducts.models.UserModel;
import com.jessicaarf.springbootapiproducts.repositories.RoleRepository;
import com.jessicaarf.springbootapiproducts.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UserModel> createNewUser(@RequestBody @Valid UserDto userDto) {

        RoleModel basicRole = roleRepository.findByName(RoleModel.Values.BASIC.name());

        Optional<UserModel> userFromDb = userRepository.findByUsernameIgnoreCase(userDto.username());
        if (userFromDb.isPresent()) {
            log.error("User with username '{}' already exists.", userDto.username());
            throw new UserAlreadyExistsException("User with username already exists.");
        }
        try {
            var user = new UserModel();
            user.setUsername(userDto.username());
            user.setPassword(passwordEncoder.encode(userDto.password()));
            user.setRoles(Set.of(basicRole));
            user.setActive(true);
            return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
        } catch (DataAccessException e) {
            log.error("Error saving user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<UserModel>> listAllUsers() {
        try {
            log.info("Fetching all users");
            return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
        } catch (DataAccessException e) {
            log.error("Error fetching all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<UserModel>> listActiveUsers() {
        try {
            List<UserModel> activeUsers = userRepository.findByIsActive(true);

            if (activeUsers.isEmpty()) {
                log.info("No active users found.");
                throw new NoUserFoundException("No active users found");
            } else {
                log.info("Retrieved {} active users.", activeUsers.size());
                return ResponseEntity.ok(activeUsers);
            }
        } catch (DataAccessException e) {
            log.error("Error fetching active users: {}", e.getMessage(), e); // Log exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<UserModel>> listInactiveUsers() {
        try {
            List<UserModel> inactiveUsers = userRepository.findByIsActive(false);

            if (inactiveUsers.isEmpty()) {
                log.info("No inactive users found.");
                throw new NoUserFoundException("No active users found");
            } else {
                log.info("Retrieved {} inactive users.", inactiveUsers.size());
                return ResponseEntity.ok(inactiveUsers);
            }
        } catch (DataAccessException e) {
            log.error("Error fetching inactive users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deactivateUser(@PathVariable(value = "userId") UUID id) {
        log.info("deactivating user with id: {}", id);
        Optional<UserModel> userToDeactive = userRepository.findById(id);
        if (userToDeactive.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        UserModel user = userToDeactive.get();
        user.setActive(false);
        userRepository.save(user);
        log.info("User deactived successfully");
        return ResponseEntity.status(HttpStatus.OK).body("User deactivated successfully.");
    }

}
