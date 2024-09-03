package com.beyond.ordersystem.product.Controller;

import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.product.Domain.Product;
import com.beyond.ordersystem.product.Dto.ProductResDto;
import com.beyond.ordersystem.product.Dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.Dto.ProductSearchDto;
import com.beyond.ordersystem.product.Dto.ProductUpdateStockDto;
import com.beyond.ordersystem.product.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// 해당 어노테이션 사용시 아래 빈은 실시간 config 변경사항의 대상이 됨

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createMember(@ModelAttribute ProductSaveReqDto dto) {
        Product product = productService.awscreateProduct(dto);
//        Product product = productService.createProduct(dto);

        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "[succuessed]product is create", product.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listProducts(ProductSearchDto searchDto, Pageable pageable) {
        Page<ProductResDto> dtos = productService.listProducts(searchDto,pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "[succuessed] products are found", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> productDetail(@PathVariable Long id){
        ProductResDto productResDto = productService.productDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, productResDto.getName()+" 상품을 조회합니다.", productResDto);
        return new ResponseEntity<>(commonResDto,HttpStatus.OK);
    }

    @PutMapping("/updatestock")
    public ResponseEntity<?> productStockUpdate(@RequestBody ProductUpdateStockDto dto){
        Product product = productService.productUpdateStock(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,  "update is successful", product);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}




