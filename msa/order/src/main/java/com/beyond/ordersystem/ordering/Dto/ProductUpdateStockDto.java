package com.beyond.ordersystem.ordering.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductUpdateStockDto {
    private Long productId;
    private Integer productQuantity;
}
