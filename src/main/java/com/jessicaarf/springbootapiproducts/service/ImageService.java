package com.jessicaarf.springbootapiproducts.service;

import com.jessicaarf.springbootapiproducts.exceptions.FileProcessingException;
import com.jessicaarf.springbootapiproducts.repositories.ProductRepository;
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
import java.util.UUID;

@Service
public class ImageService {

    @Value("${upload.directory}")
    private String uploadDir;
    @Autowired
    private ProductRepository productRepository;

        public void uploadImage(UUID id, MultipartFile file) throws IOException {

            long maxFileSize = 10485760;
            String[] allowedTypes = {"image/jpeg", "image/png"};

            if (file.isEmpty()) {
                throw new FileProcessingException("Empty file", HttpStatus.BAD_REQUEST);
            } else if (file.getSize() > maxFileSize) {
                throw new FileProcessingException("File size exceeds the allowed limit", HttpStatus.PAYLOAD_TOO_LARGE);
            }

            String contentType = file.getContentType();
            System.out.println(contentType);
            if (!Arrays.asList(allowedTypes).contains(contentType)) {
                throw new FileProcessingException("Unsupported file type", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }

            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);
            Path filePath = uploadPath.resolve(uniqueFileName);

            try {
                Files.copy(file.getInputStream(), filePath);

            } catch (IOException e) {
                throw new IOException("Falha ao salvar o arquivo", e);
            }
        }
    }
