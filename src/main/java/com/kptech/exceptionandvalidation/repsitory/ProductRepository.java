package com.kptech.exceptionandvalidation.repsitory;

import com.kptech.exceptionandvalidation.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
