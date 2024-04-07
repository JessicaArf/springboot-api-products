package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.dtos.ProductDto;
import com.jessicaarf.springbootapiproducts.exceptions.ProductNotFoundException;
import com.jessicaarf.springbootapiproducts.exceptions.UserNotFoundException;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.models.UserModel;
import com.jessicaarf.springbootapiproducts.repositories.ProductRepository;
import com.jessicaarf.springbootapiproducts.repositories.UserRepository;
import jakarta.validation.Valid;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, AuthorizationService authorizationService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
    }

    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productDto, JwtAuthenticationToken token) {
        log.info("Saving a new product: {}", productDto);
        Optional<ProductModel> existingProduct = productRepository.findByName(productDto.name());
        if (existingProduct.isPresent()) {
            log.error("Product with name '{}' already exists.", productDto.name());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product name already exists");
        }
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productDto, productModel);
        UserModel user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        productModel.setUserModel(user);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
        } catch (DataAccessException e) {
            log.error("Error saving product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<ProductModel>> getAllProducts() {
        try {
            log.info("Fetching all products");
            return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
        } catch (DataAccessException e) {
            log.error("Error fetching all products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<ProductModel> getOneProduct(@PathVariable(value = "id") UUID id) {
        log.info("Fetching product with id: {}", id);
        ProductModel product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id : " + id + " not found."));
        log.info("Product found: {}", product);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    public ResponseEntity<ProductModel> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductDto productDto, JwtAuthenticationToken token) {
        log.info("Updating product with id: {}", id);
        ProductModel product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id : " + id + " not found."));
        authorizationService.ensureAuthorized(token, product);
        BeanUtils.copyProperties(productDto, product);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(product));
    }

    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") UUID id, JwtAuthenticationToken token) {
        log.info("Received request to delete product with id: {}", id);
        try {
            ProductModel product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product with id : " + id + " not found."));
            authorizationService.ensureAuthorized(token, product);
            productRepository.delete(product);
            log.info("Product deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
        } catch (ProductNotFoundException e) {
            log.error("Error deleting product: {}", e.getMessage());
            throw new ProductNotFoundException("Product with id : " + id + " not found.");
        } catch (DataAccessException e) {
            log.error("Unexpected error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error deleting product.");
        }
    }

}




