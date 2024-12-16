package com.kptech.exceptionandvalidation.service;

import com.kptech.exceptionandvalidation.dto.ProductRequest;
import com.kptech.exceptionandvalidation.entity.Product;
import com.kptech.exceptionandvalidation.exception.ProductIdNotFoundException;
import com.kptech.exceptionandvalidation.exception.ProductSaveException;
import com.kptech.exceptionandvalidation.repsitory.ProductRepository;
import lombok.extern.slf4j.Slf4j;  // Lombok annotation for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j  // Lombok will generate the 'log' object for logging
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;  // Updated to support generic Object


    @Transactional
    public void saveProduct(ProductRequest productRequest) {
        log.info("Saving product with name: {}", productRequest.name());  // Log the product name being saved
        Product product = new Product();
        product.setName(productRequest.name());
        product.setCategory(productRequest.category());
        product.setPrice(productRequest.price());

        try {
            // Save the product to the database
            Product savedProduct = productRepository.save(product);
            log.info("Product saved successfully with ID: {}", savedProduct.getId());  // Log success

            // Clear the cache for product list as it may have changed
            redisTemplate.delete("product:list");
            log.info("Cache for product list cleared");

            // Clear the cache for the saved product (if it exists in cache)
            redisTemplate.delete("product:id:" + savedProduct.getId());
            log.info("Cache for saved product with ID {} cleared", savedProduct.getId());

            // Optionally, cache the saved product (if you want to cache it)
            redisTemplate.opsForValue().set("product:id:" + savedProduct.getId(), savedProduct);
            log.info("Cache for product with ID {} set", savedProduct.getId());

        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation occurred while saving product: {}", ex.getMessage(), ex);
            throw new ProductSaveException("Failed to save product due to data integrity violation: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error occurred while saving product: {}", ex.getMessage(), ex);
            throw new ProductSaveException("An unexpected error occurred while saving the product: " + ex.getMessage());
        }
    }

    public Product findProductById(Long id) {
        log.info("Looking for product with ID: {}", id);  // Log the ID being searched
        // Check cache first
        String cacheKey = "product:id:" + id;
        if (redisTemplate.hasKey(cacheKey)) {
            log.info("Product found in cache with ID: {}", id);
            return (Product) redisTemplate.opsForValue().get(cacheKey);
        }

        // If not in cache, check the database
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            log.info("Product found in database with ID: {}", id);
            // Save to cache
            redisTemplate.opsForValue().set(cacheKey, product.get());
            log.info("Product with ID {} cached", id);
            return product.get();
        }

        // Throw an exception if not found
        log.warn("Product with ID {} not found", id);
        throw new ProductIdNotFoundException("Product with ID " + id + " not found");
    }

    // Cache all products
    public List<Product> findAllProducts() {
        log.info("Fetching all products from cache or database");
        String cacheKey = "product:list";

        // Check if the product list exists in the cache
        if (redisTemplate.hasKey(cacheKey)) {
            log.info("Product list found in cache");
            return (List<Product>) redisTemplate.opsForValue().get(cacheKey);
        }

        // If not in cache, retrieve from database
        List<Product> products = productRepository.findAll();
        log.info("Product list fetched from database with {} products", products.size());

        // Cache the list for future use
        redisTemplate.opsForValue().set(cacheKey, products);
        log.info("Product list cached");

        return products;
    }
}
