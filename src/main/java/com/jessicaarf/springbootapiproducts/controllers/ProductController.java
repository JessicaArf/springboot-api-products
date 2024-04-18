package com.jessicaarf.springbootapiproducts.controllers;

import com.jessicaarf.springbootapiproducts.dtos.ProductDto;
import com.jessicaarf.springbootapiproducts.exceptions.FileProcessingException;
import com.jessicaarf.springbootapiproducts.models.Product;
import com.jessicaarf.springbootapiproducts.service.ImageService;
import com.jessicaarf.springbootapiproducts.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {


    private final ProductService productService;

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody @Valid ProductDto productDto, JwtAuthenticationToken token) {
        log.info("Creating a new product: {}", productDto);
        try {
            Product product = productService.saveProduct(productDto, token);
            log.info("Product created successfully: {}", product);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (DataAccessException e) {
            log.error("Error saving product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            log.info("Fetching all products");
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch (DataAccessException e) {
            log.error("Error fetching all products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOneProduct(@PathVariable(value = "id") UUID id) {
        try {
            log.info("Fetching product with id: {}", id);
            Product product = productService.getOneProduct(id);
            log.info("Product found: {}", product);
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } catch (DataAccessException e) {
            log.error("Error fetching product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductDto productDto, JwtAuthenticationToken token) {
        try{
            Product product = productService.updateProduct(id, productDto, token);
            log.info("Product updated sucessfully");
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }catch (DataAccessException e) {
            log.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") UUID id, JwtAuthenticationToken token) {
        log.info("Received request to delete product with id: {}", id);
        try {
            productService.deleteProduct(id, token);
            log.info("Product deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
        } catch (DataAccessException e) {
            log.error("Unexpected error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error deleting product.");
        }
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Product> saveImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file, JwtAuthenticationToken token) throws IOException {
        try {
            log.info("Starting image upload for product id: {}", id);
            Product product = imageService.saveProductImage(id, file, token);
            log.info("Image updated successfully for product with id: {}", id);
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } catch (IOException e) {
            log.error("Image upload failed due to I/O error: {}", e.getMessage());
            throw new FileProcessingException("Error saving product image. Contact support.", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteImage(@PathVariable(value = "id") UUID id, JwtAuthenticationToken token) {
        try {
            log.info("Starting image deletion for product id: {}", id);
            imageService.deleteImage(id, token);
            log.info("Image deleted successfully for product with id: {}", id);
            return ResponseEntity.status(HttpStatus.OK).body("Image deleted successfully.");
        } catch (DataAccessException e) {
            log.error("Error deleting image from database for product id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the image.");
        }
    }

}
