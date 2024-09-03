package com.beyond.ordersystem.product.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResDto {
        private Long id;
        private String name;
        private String category;
        private Integer price;
        private Integer stockQuantity;
        private String imagePath;

    }

