package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.dtos.ProductDto;
import com.jessicaarf.springbootapiproducts.exceptions.ProductNotFoundException;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.repositories.ProductRepository;
import jakarta.validation.Valid;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Saving a new product: {}", productDto);
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productDto, productModel);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
        } catch (DataAccessException e){
            log.error("Error saving product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

public ResponseEntity<List<ProductModel>> getAllProducts() {
    try {
        log.info("Fetching all products");
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.findAll());
    } catch(DataAccessException e){
        log.error("Error fetching all products: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
    log.info("Fetching product with id: {}", id);
    Optional<ProductModel> productO = productRepository.findById(id);
    if (productO.isEmpty()) {
        throw new ProductNotFoundException("Product not found.");
    }
    log.info("Product found: {}", productO.get());
    return ResponseEntity.status(HttpStatus.OK).body(productO.get());
}

public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductDto productDto) {
    log.info("Updating product with id: {}", id);
    Optional<ProductModel> productO = productRepository.findById(id);
    if (productO.isEmpty()) {
        throw new ProductNotFoundException("Product not found.");
    }
    var productModel = productO.get();
    BeanUtils.copyProperties(productDto, productModel);
    return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
}

public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
    log.info("Deleting product with id: {}", id);
    Optional<ProductModel> productO = productRepository.findById(id);
    if (productO.isEmpty()) {
        throw new ProductNotFoundException("Product not found.");
    }
    productRepository.delete(productO.get());
    log.info("Product deleted successfully");
    return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
}

}

