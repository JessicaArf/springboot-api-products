package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.exceptions.FileProcessingException;
import com.jessicaarf.springbootapiproducts.exceptions.ProductNotFoundException;
import com.jessicaarf.springbootapiproducts.models.ProductModel;
import com.jessicaarf.springbootapiproducts.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ImageService {

    @Value("${upload.directory}")
    private String uploadDir;
    @Autowired
    private ProductRepository productRepository;

    public void uploadImage(UUID id, MultipartFile file) throws IOException {
        log.info("Starting image upload for product ID: {}", id);
        try {
            ProductModel product = getProductById(id);
            validateImageFile(file);
            saveImage(id, file);
            log.info("Image uploaded successfully for product with id: {}" + id);
        } catch (FileProcessingException e) {
            log.error("Error updating image for product ID {}: {}", id, e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("Failed to update image for product ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public void updateImage(UUID id, MultipartFile file) throws IOException {
        try {
            validateImageFile(file);
            deleteImage(id);
            saveImage(id, file);
            log.info("Image updated successfully for product with id: {}", id);
        } catch (FileProcessingException e) {
            log.error("Error updating image for product ID {}: {}", id, e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("Failed to update image for product ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public void deleteImage(UUID id) {
        log.info("Starting image deletion for product ID: {}", id);
        try {
            ProductModel product = getProductById(id);
            product.setImageUrl(null);
            productRepository.save(product);
            log.info("Image deleted successfully for product with id: {}", id);
        } catch (ProductNotFoundException e) {
            log.error("Error deleting image for product ID {}: Product not found", id);
            throw e;
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
        Optional<ProductModel> productO = productRepository.findById(id);
        if (productO.isPresent()) {
            return productO.get();
        } else {
            throw new ProductNotFoundException("Product not found.");
        }
    }
}
