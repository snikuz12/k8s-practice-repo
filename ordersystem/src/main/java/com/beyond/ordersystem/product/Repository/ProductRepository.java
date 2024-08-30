package com.beyond.ordersystem.product.Repository;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.product.Domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}

