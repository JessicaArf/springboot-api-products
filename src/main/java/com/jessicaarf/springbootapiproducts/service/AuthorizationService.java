package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.exceptions.AuthorizationException;
import com.jessicaarf.springbootapiproducts.exceptions.UserNotFoundException;
import com.jessicaarf.springbootapiproducts.models.Product;
import com.jessicaarf.springbootapiproducts.models.Role;
import com.jessicaarf.springbootapiproducts.models.User;
import com.jessicaarf.springbootapiproducts.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;

    public void ensureAuthorized(JwtAuthenticationToken token, Product product) {
        log.debug("Checking authorization for user with id: {}", token.getName());
        log.debug("Product user id: {}", product.getUserModel().getUserId());
        User user = userRepository.findById(UUID.fromString(token.getName())).orElseThrow(() -> new UserNotFoundException("User not found."));
        if (!hasAuthorization(user, product)) {
            throw new AuthorizationException("User not authorized.");
        }
    }

    public boolean hasAuthorization(User user, Product product){
        log.debug("Checking authorization for user id: {}", user.getUserId());
        log.debug("Product user id: {}", product.getUserModel().getUserId());
        return user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()))
                || product.getUserModel().getUserId().equals(user.getUserId());
    }
}
