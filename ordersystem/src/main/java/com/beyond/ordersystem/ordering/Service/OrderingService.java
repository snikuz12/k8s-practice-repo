package com.beyond.ordersystem.ordering.Service;


import com.beyond.ordersystem.common.serivce.StockInventoryService;
import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberLoginDto;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import com.beyond.ordersystem.ordering.Domain.OrderDetail;
import com.beyond.ordersystem.ordering.Domain.OrderStatus;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import com.beyond.ordersystem.ordering.Dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.Dto.StockDecreaseEvent;
import com.beyond.ordersystem.ordering.Repository.OrderDetailRepository;
import com.beyond.ordersystem.ordering.Repository.OrderingRepository;
import com.beyond.ordersystem.product.Domain.Product;
import com.beyond.ordersystem.product.Repository.ProductRepository;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;


@Service
@Transactional  // 붙어서 더티체킹 ㅇㅇ
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StockInventoryService stockInventoryService;
//    private final StockDecreaseEventHandler stockDecreaseEventHandler;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository, StockInventoryService stockInventoryService) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.stockInventoryService = stockInventoryService;
//        this.stockDecreaseEventHandler = stockDecreaseEventHandler;
    }


    @Synchronized
    public Ordering orderCreate(List<OrderSaveReqDto> dtos) {

//       Synchronized 설정한다 하더라도, 재고 감소가 db에 반영되는 시점은 트랜잭션이 커밋되고 종료되는 시점
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with email: " + memberEmail));

        Ordering ordering = Ordering.builder()
                .member(member)
                .build();

        for (OrderSaveReqDto dto : dtos) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + dto.getProductId()));

            int quantity = dto.getProductCount();

            if(product.getName().contains("sale")){
                //redis를 통한 재고관리 및 재고잔량 확인

                int newQuantity = stockInventoryService.decreaseStock(dto.getProductId(), dto.getProductCount()).intValue();
                if(newQuantity < 0){
                    throw new IllegalArgumentException("재고 부족");
                }
//                rdb에 재고를 업데이트. rabbitmq를 통해 비동기적으로 이벤트 처리
//                stockDecreaseEventHandler.publish(new StockDecreaseEvent(product.getId(), dto.getProductCount()));
            }else{
                if(product.getStockQuantity() < quantity){
                    throw new IllegalArgumentException("재고 부족");
                }
            }

            // 변경 감지(더티체킹)으로 인해 별도의 save 불필요
            product.updateStockQuantity(quantity);
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    .build();

            ordering.getOrderDetails().add(orderDetail);
        }

        Ordering savedOrdering = orderingRepository.save(ordering);
        return savedOrdering;
    }

    public List<OrderListResDto> listorder() {
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();

        for (Ordering order : orderings) {
            orderListResDtos.add(order.fromEntity());
        }
        return orderListResDtos;
    }


    public List<OrderListResDto> myOrders(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new EntityNotFoundException("member not found"));
        List<Ordering> orderings = orderingRepository.findByMember(member);
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for (Ordering order : orderings) {
            orderListResDtos.add(order.fromEntity());
        }
        return orderListResDtos;
    }

    public Ordering orderCancle(Long id){
        Ordering ordering = orderingRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("not found order"));
        ordering.updateStatus(OrderStatus.CANCELLED);
        return ordering;
    }
}


//    public Ordering orderCreate(OrderSaveReqDto dto){
//        // ordering 생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMemberId())
//                .orElseThrow(()->new EntityNotFoundException("member is not found"));
//        Ordering ordering = Ordering.builder()
//                .member(member)
//                .build();
//
////        {productId, productCount}를 한건한건 받기 위해 for문
//        for (OrderSaveReqDto.OrderDto orderDto : dto.getOrderDtos()){
//            Product product = productRepository.findById(orderDto.getProductId())
//                    .orElseThrow(()->new EntityNotFoundException("product is not found"));
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .product(product)
//                    .quantity(orderDto.getProductCount())
//                    .ordering(ordering)
//                    .build();
//            orderDetailRepository.save(orderDetail);   // 쉬운 방법임. 걍 한번 돌면서
//        }
//
//        return ordering;
//    }
//}