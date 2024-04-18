package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.dtos.ProductDto;
import com.jessicaarf.springbootapiproducts.exceptions.ProductNotFoundException;
import com.jessicaarf.springbootapiproducts.exceptions.UserNotFoundException;
import com.jessicaarf.springbootapiproducts.models.Product;
import com.jessicaarf.springbootapiproducts.models.User;
import com.jessicaarf.springbootapiproducts.repositories.ProductRepository;
import com.jessicaarf.springbootapiproducts.repositories.UserRepository;


import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;


    public Product saveProduct(ProductDto productDto, JwtAuthenticationToken token) {
        Optional<Product> existingProduct = productRepository.findByName(productDto.name());
        if (existingProduct.isPresent()) {
            log.error("Product with name '{}' already exists.", productDto.name());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product name already exists");
        }
        var productModel = new Product();
        BeanUtils.copyProperties(productDto, productModel);
        User user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new UserNotFoundException());
        productModel.setUserModel(user);
        return productRepository.save(productModel);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getOneProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product updateProduct(UUID id, ProductDto productDto, JwtAuthenticationToken token) {
        log.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        authorizationService.ensureAuthorized(token, product);
        BeanUtils.copyProperties(productDto, product);
        return productRepository.save(product);
    }

    public void deleteProduct(UUID id, JwtAuthenticationToken token) {
        log.info("Received request to delete product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        authorizationService.ensureAuthorized(token, product);
        productRepository.delete(product);
    }

}




