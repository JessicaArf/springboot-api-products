package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.exceptions.FileProcessingException;
import com.jessicaarf.springbootapiproducts.exceptions.ProductNotFoundException;
import com.jessicaarf.springbootapiproducts.models.Product;
import com.jessicaarf.springbootapiproducts.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
public class ImageService {

    @Value("${upload.directory}")
    private String uploadDir;
    private final ProductRepository productRepository;

    private final AuthorizationService authorizationService;

    public Product saveProductImage(UUID id, MultipartFile file, JwtAuthenticationToken token) throws IOException {
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        authorizationService.ensureAuthorized(token, product);
        validateImageFile(file, id);
        if (product.getImageUrl() != null) {
            log.info("Product already has an image.");
            deleteImage(id, token);
        }
        String uniqueFileName = generateUniqueFileName(file);
        Path filePath = createFilePath(uniqueFileName);
        try {
            Files.copy(file.getInputStream(), filePath);
            product.setImageUrl("/imagens-api/" + uniqueFileName);
        } catch (IOException e) {
            log.error("Failed to save file for product: {}", e.getMessage());
            throw new IOException("Failed to save product image.", e);
        }

        return productRepository.save(product);
    }

    public void deleteImage(UUID id, JwtAuthenticationToken token) {
            Product product =  productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));
            authorizationService.ensureAuthorized(token, product);
            product.setImageUrl(null);
            productRepository.save(product);
    }


    private void validateImageFile(MultipartFile file, UUID id) throws FileProcessingException {
        if (file.isEmpty()) {
            log.warn("Error updating image for product id {}: Empty file", id);
            throw new FileProcessingException("Empty file.", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > ImageConstants.MAX_FILE_SIZE) {
            log.warn("Error updating image for product id {}: File size exceeds the allowed limit", id);
            throw new FileProcessingException("File size exceeds the allowed limit.", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (!Arrays.asList(ImageConstants.ALLOWED_CONTENT_TYPES).contains(contentType)) {
            log.warn("Error updating image for product id {}: Unsupported file type", id);
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

}

