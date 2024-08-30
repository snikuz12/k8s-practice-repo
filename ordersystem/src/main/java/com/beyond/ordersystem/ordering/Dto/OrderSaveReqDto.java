package com.beyond.ordersystem.ordering.Dto;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.ordering.Domain.OrderDetail;
import com.beyond.ordersystem.ordering.Domain.OrderStatus;
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
//    private Long memberId;
//    private List<OrderDto> orderDtos;
    private Long productId;
    private Integer productCount;
    
    // 별도 클래스니까 어노테이션 붙어야함
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class OrderDto {
//        private Long productId;
//        private Integer productCount;
//
//    }

    public Ordering toEntity(Member member){
        return Ordering.builder()
                .member(member)
//                .orderStatus(OrderStatus.ORDERED)
                .build();
    }

}



