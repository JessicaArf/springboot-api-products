package com.jessicaarf.springbootapiproducts.controllers;

import com.jessicaarf.springbootapiproducts.dtos.UserDto;
import com.jessicaarf.springbootapiproducts.models.UserModel;
import com.jessicaarf.springbootapiproducts.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<UserModel> newUser(@RequestBody @Valid UserDto userDto) {
        return userService.createNewUser(userDto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<UserModel>> listAllUsers() {
        return userService.listAllUsers();
    }

    @GetMapping("/actives")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<UserModel>> listActiveUsers() {
        return userService.listActiveUsers();
    }

    @GetMapping("/inactives")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<UserModel>> listInactiveUsers() {
        return userService.listInactiveUsers();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<String> deactivateUser(@PathVariable(value = "id") UUID id) {
        return userService.deactivateUser(id);
    }

}
