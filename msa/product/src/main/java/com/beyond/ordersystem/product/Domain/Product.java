package com.beyond.ordersystem.product.Domain;


import com.beyond.ordersystem.common.domain.BaseTimeEntity;
import com.beyond.ordersystem.product.Dto.ProductResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
    private String name;

//    @Column(nullable = false)
    private String category;

//    @Column(nullable = false)
    private Integer price;

//    @Column(nullable = false)
    private Integer stockQuantity;

    private String imagePath;

    public void updateImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public ProductResDto fromEntity() {
        return ProductResDto.builder()
                .id(this.id)
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .imagePath(this.imagePath)
                .build();
    }
    public void updateStockQuantity(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalArgumentException("재고부족");
        }
        this.stockQuantity -= quantity;
    }

}

