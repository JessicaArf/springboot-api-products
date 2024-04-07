package com.jessicaarf.springbootapiproducts.controllers;

import com.jessicaarf.springbootapiproducts.dtos.ProductDto;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.service.ImageService;
import com.jessicaarf.springbootapiproducts.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/products")
public class ProductController {


    private final ProductService productService;

    private final ImageService imageService;



    public ProductController(ProductService productService, ImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productDto, JwtAuthenticationToken token) {
        return productService.saveProduct(productDto, token);

    }

    @GetMapping
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductModel> getOneProduct(@PathVariable(value = "id") UUID id) {
        return productService.getOneProduct(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductModel> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductDto productDto, JwtAuthenticationToken token) {
        return productService.updateProduct(id, productDto, token);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") UUID id, JwtAuthenticationToken token) {

        return productService.deleteProduct(id, token);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file, JwtAuthenticationToken token) throws IOException {
        return imageService.uploadImage(id, file, token);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<String> updateImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file, JwtAuthenticationToken token) throws IOException {
        return imageService.updateImage(id, file, token);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteImage(@PathVariable(value = "id") UUID id, JwtAuthenticationToken token) {
        return imageService.deleteImage(id, token);
    }

}
