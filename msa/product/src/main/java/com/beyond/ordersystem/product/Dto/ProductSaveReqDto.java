package com.beyond.ordersystem.product.Dto;

import com.beyond.ordersystem.product.Domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaveReqDto {
    private String name;

    private String category;

    private Integer price;

    private Integer stockQuantity;

    private MultipartFile productimage;

    public Product toEntity() {
        return Product.builder()
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .build();
    }
}
