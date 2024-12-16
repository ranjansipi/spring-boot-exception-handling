package com.kptech.exceptionandvalidation.controller;

import com.kptech.exceptionandvalidation.dto.ProductRequest;
import com.kptech.exceptionandvalidation.entity.Product;
import com.kptech.exceptionandvalidation.exception.ProductIdNotFoundException;
import com.kptech.exceptionandvalidation.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/save")
    public ResponseEntity<String> addProduct(
            @Valid @RequestBody ProductRequest productRequest,
            BindingResult bindingResult) {  // Add BindingResult here

        if (bindingResult.hasErrors()) {
            // Collect errors
            Map<String, String> errorDetails = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errorDetails.put(error.getField(), error.getDefaultMessage());
            });

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }

        // If no validation errors, proceed to save the product
        productService.saveProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product saved successfully");
    }
    // Endpoint to find product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        try {
            Product product = productService.findProductById(id);  // Call the service method
            return new ResponseEntity<>(product, HttpStatus.OK);  // Return product with status 200 (OK)
        } catch (ProductIdNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  // Return 404 if product not found
        }
    }

    // Endpoint to find all products
    @GetMapping("/getall")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAllProducts();  // Call the service method
        return new ResponseEntity<>(products, HttpStatus.OK);  // Return product list with status 200 (OK)
    }

}
