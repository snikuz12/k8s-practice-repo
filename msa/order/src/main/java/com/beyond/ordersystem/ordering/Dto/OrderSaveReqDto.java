package com.beyond.ordersystem.ordering.Dto;


import com.beyond.ordersystem.ordering.Domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

//주문 등록(/order/create) :
//      memberId, [productId, productCount]

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSaveReqDto {
    private Long productId;
    private Integer productCount;
}



