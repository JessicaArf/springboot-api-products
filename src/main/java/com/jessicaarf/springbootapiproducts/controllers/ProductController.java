package com.jessicaarf.springbootapiproducts.controllers;

import com.jessicaarf.springbootapiproducts.dtos.ProductDto;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.service.ImageService;
import com.jessicaarf.springbootapiproducts.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
        return productService.getOneProduct(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id, @RequestBody @Valid ProductDto productDto){
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
        return productService.deleteProduct(id);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
       try{
           imageService.uploadImage(id, file);
           return ResponseEntity.ok().body("Image uploaded sucessfully for product with id: " + id);
       } catch(Exception e){
           return ResponseEntity.badRequest().body("Error uploading image: " + e.getMessage());
       }
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Object> updateImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException{
       try{
           imageService.updateImage(id, file);
           return ResponseEntity.ok().build();
       } catch (IOException e){
           return ResponseEntity.badRequest().body("Unable to update the image");
       }
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteImage(@PathVariable(value = "id") UUID id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(("Error when deleting image."));
        }
    }

}

