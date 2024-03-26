package com.jessicaarf.springbootapiproducts.repositories;

import com.jessicaarf.springbootapiproducts.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {
    Optional<ProductModel> findByName(String name);

}
