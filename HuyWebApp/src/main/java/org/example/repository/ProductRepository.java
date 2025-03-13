package org.example.repository;

import org.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContaining(String name);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.price ASC")
    List<Product> findAvailableProductsSortedByPriceAsc();

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.price DESC")
    List<Product> findAvailableProductsSortedByPriceDesc();

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN ?1 AND ?2")
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
}