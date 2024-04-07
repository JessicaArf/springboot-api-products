package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.exceptions.FileProcessingException;
import com.jessicaarf.springbootapiproducts.exceptions.ImageAlreadyExistsException;
import com.jessicaarf.springbootapiproducts.exceptions.ProductNotFoundException;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
public class ImageService {

    @Value("${upload.directory}")
    private String uploadDir;
    private final ProductRepository productRepository;

    private final AuthorizationService authorizationService;

    public ImageService(ProductRepository productRepository, AuthorizationService authorizationService) {
        this.productRepository = productRepository;
        this.authorizationService = authorizationService;
    }

    public ResponseEntity<String> uploadImage(UUID id, MultipartFile file, JwtAuthenticationToken token) throws IOException {
        log.info("Starting image upload for product id: {}", id);
        try {
            ProductModel product = getProductById(id);
            if (product.getImageUrl() != null) {
                log.info("Image update not required. Product already has an image.");
                throw new ImageAlreadyExistsException("The product image already exists. To update the image, please use the update endpoint instead of the upload endpoint.");
            }
            authorizationService.ensureAuthorized(token, product);
            validateImageFile(file);
            saveImage(id, file);
            log.info("Image uploaded successfully for product with id: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(product.getImageUrl());
        } catch (IOException e) {
            log.error("Image upload failed due to I/O error: {}", e.getMessage());
            throw new FileProcessingException("Error saving product image. Contact support.", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public ResponseEntity<String> updateImage(UUID id, MultipartFile file, JwtAuthenticationToken token) throws IOException {
        log.info("Starting image update for product id: {}", id);
        try {
            validateImageFile(file);
            deleteImage(id, token);
            saveImage(id, file);
            log.info("Image updated successfully for product with id: {}", id);
            return ResponseEntity.status(HttpStatus.OK).body("Image updated successfully");
        } catch (FileProcessingException e) {
            log.error("Error updating image for product id {}: {}", id, e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("Image upload failed due to I/O error: {}", e.getMessage());
            throw new FileProcessingException("Error saving product image. Contact support.", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public ResponseEntity<String> deleteImage(UUID id, JwtAuthenticationToken token) {
        log.info("Starting image deletion for product id: {}", id);
        try {
            ProductModel product = getProductById(id);
            authorizationService.ensureAuthorized(token, product);
            product.setImageUrl(null);
            productRepository.save(product);
            log.info("Image deleted successfully for product with id: {}", id);
            return ResponseEntity.status(HttpStatus.OK).body("Image deleted successfully.");
        } catch (ProductNotFoundException e) {
            log.error("Error deleting image for product id {}: Product not found", id);
            throw e;
        }catch (DataAccessException e) {
            log.error("Error deleting image from database for product id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the image.");
        }
    }

    private void saveImage(UUID id, MultipartFile file) throws IOException {
        String uniqueFileName = generateUniqueFileName(file);
        Path filePath = createFilePath(uniqueFileName);
        saveFile(file, filePath);
        updateProductImageUrl(id, uniqueFileName);
    }

    private void validateImageFile(MultipartFile file) throws FileProcessingException {
        if (file.isEmpty()) {
            throw new FileProcessingException("Empty file.", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > ImageConstants.MAX_FILE_SIZE) {
            throw new FileProcessingException("File size exceeds the allowed limit.", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (!Arrays.asList(ImageConstants.ALLOWED_CONTENT_TYPES).contains(contentType)) {
            throw new FileProcessingException("Unsupported file type.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }

    private Path createFilePath(String uniqueFileName) {
        Path uploadPath = Paths.get(uploadDir);
        return uploadPath.resolve(uniqueFileName);
    }

    private void saveFile(MultipartFile file, Path filePath) throws IOException {
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            log.error("Failed to save file for product: {}", e.getMessage());
            throw new IOException("Failed to save file.", e);
        }
    }

    private void updateProductImageUrl(UUID id, String uniqueFileName) {
        ProductModel product = getProductById(id);
            product.setImageUrl("/imagens-api/" + uniqueFileName);
            productRepository.save(product);
    }

    private ProductModel getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("User not found"));
    }

}

