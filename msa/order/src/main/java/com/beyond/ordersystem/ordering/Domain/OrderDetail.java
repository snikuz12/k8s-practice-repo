package com.beyond.ordersystem.ordering.Domain;

import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_id")
    private Ordering ordering;


    private Long productId;



    public OrderListResDto.OrderDetailDto fromEntity(){
        OrderListResDto.OrderDetailDto orderDetailDto = OrderListResDto.OrderDetailDto.builder()
                .id(this.id)
                .count(this.quantity)
                .build();


        return orderDetailDto;
    }
}



