package com.beyond.ordersystem.ordering.Domain;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)

    // 빌더패턴에서도 ArrayList로 초기화 되도록 하는 설정
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();


    public OrderListResDto fromEntity(){
        List<OrderDetail> orderDetailList = this.getOrderDetails();
        List<OrderListResDto.OrderDetailDto> orderDetailDtos = new ArrayList<>();
        for(OrderDetail orderDetail : orderDetailList){
            // orderDetail를 꺼내는 건데 형태가 OrderListResDto.OrderDetailDto가
            // 되어야하기 때문에 밑에 코드 추가
            orderDetailDtos.add(orderDetail.fromEntity());
        }

        OrderListResDto orderListResDto = OrderListResDto.builder()
                .id(this.id)
                .memberEmail(this.member.getEmail())
                .orderStatus(this.orderStatus)
                .orderDetailDtos(orderDetailDtos)
                .build();
        return orderListResDto;
    }

    public void updateStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
}