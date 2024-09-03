package com.beyond.ordersystem.ordering.Service;

import com.beyond.ordersystem.common.config.FeignConfig;
import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.ordering.Dto.ProductUpdateStockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

// url설정을 추가하여 service자원을 검색하도록 설정
@FeignClient(name="product-service",url="http://product-service" ,configuration = FeignConfig.class)
public interface ProductFeign {

    @GetMapping(value = "/product/{id}")
    CommonResDto getProductById(@PathVariable("id") Long id);

    @PutMapping(value="/product/updatestock")
    void updateProductStock(@RequestBody ProductUpdateStockDto dto);
}
