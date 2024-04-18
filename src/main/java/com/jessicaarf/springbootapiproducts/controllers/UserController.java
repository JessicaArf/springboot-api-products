package com.jessicaarf.springbootapiproducts.controllers;

import com.jessicaarf.springbootapiproducts.dtos.UserDto;
import com.jessicaarf.springbootapiproducts.models.User;
import com.jessicaarf.springbootapiproducts.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Transactional
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<User> newUser(@RequestBody @Valid UserDto userDto) {
        try {
            User newUser = userService.createNewUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DataAccessException e) {
            log.error("Unexpected error saving user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listAllUsers() {
       try {
          List<User> users = userService.listAllUsers();
           return ResponseEntity.status(HttpStatus.OK).body(users);
       }  catch (DataAccessException e) {
        log.error("Error fetching all users: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    }

    @GetMapping("/actives")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listActiveUsers() {
        try {
            List<User> activeUsers = userService.listActiveUsers();
            return ResponseEntity.status(HttpStatus.OK).body(activeUsers);
        } catch (DataAccessException e) {
            log.error("Error fetching active users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/inactives")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listInactiveUsers() {
        try {
            List<User> inactiveUsers = userService.listInactiveUsers();
            return ResponseEntity.status(HttpStatus.OK).body(inactiveUsers);
        } catch (DataAccessException e) {
            log.error("Error fetching active users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<String> deactivateUser(@PathVariable(value = "id") UUID id) {
        try {
            userService.deactivateUser(id);
            log.info("User deactived successfully");
            return ResponseEntity.status(HttpStatus.OK).body("User deactivated successfully.");
        } catch (DataAccessException e) {
            log.error("Error deactivating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
