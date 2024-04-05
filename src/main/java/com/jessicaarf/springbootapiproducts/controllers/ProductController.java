package com.jessicaarf.springbootapiproducts.controllers;

import com.jessicaarf.springbootapiproducts.dtos.ProductDto;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.service.ImageService;
import com.jessicaarf.springbootapiproducts.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;
    @Autowired
    ImageService imageService;

    @PostMapping
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductDto productDto) {
        return productService.saveProduct(productDto);
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
    public ResponseEntity<ProductModel> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductDto productDto){
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") UUID id) {
        return productService.deleteProduct(id);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
           return imageService.uploadImage(id, file);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<String> updateImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException{
            return imageService.updateImage(id, file);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<String> deleteImage(@PathVariable(value = "id") UUID id) throws IOException{
            return imageService.deleteImage(id);
    }

}