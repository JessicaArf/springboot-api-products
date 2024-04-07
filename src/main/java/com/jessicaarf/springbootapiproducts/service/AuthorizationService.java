package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.exceptions.AuthorizationException;
import com.jessicaarf.springbootapiproducts.exceptions.UserNotFoundException;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.models.RoleModel;
import com.jessicaarf.springbootapiproducts.models.UserModel;
import com.jessicaarf.springbootapiproducts.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class AuthorizationService {

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void ensureAuthorized(JwtAuthenticationToken token, ProductModel product) {
        log.debug("Checking authorization for user with id: {}", token.getName());
        log.debug("Product user id: {}", product.getUserModel().getUserId());
        UserModel user = userRepository.findById(UUID.fromString(token.getName())).orElseThrow(() -> new UserNotFoundException("User not found."));
        if (!hasAuthorization(user, product)) {
            throw new AuthorizationException("User not authorized.");
        }
    }

    public boolean hasAuthorization(UserModel user, ProductModel product){
        log.debug("Checking authorization for user id: {}", user.getUserId());
        log.debug("Product user id: {}", product.getUserModel().getUserId());
        return user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(RoleModel.Values.ADMIN.name()))
                || product.getUserModel().getUserId().equals(user.getUserId());
    }
}
