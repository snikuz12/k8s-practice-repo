package com.beyond.ordersystem.ordering.Domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
        private Long id;
        private String name;
        private String category;
        private Integer price;
        private Integer stockQuantity;
        private String imagePath;

    }

