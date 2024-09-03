package com.beyond.ordersystem.ordering.Service;


import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.common.serivce.StockInventoryService;


import com.beyond.ordersystem.ordering.Domain.OrderDetail;
import com.beyond.ordersystem.ordering.Domain.OrderStatus;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import com.beyond.ordersystem.ordering.Domain.ProductDto;
import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import com.beyond.ordersystem.ordering.Dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.Dto.ProductUpdateStockDto;
//import com.beyond.ordersystem.ordering.Dto.StockDecreaseEvent;
import com.beyond.ordersystem.ordering.Repository.OrderDetailRepository;
import com.beyond.ordersystem.ordering.Repository.OrderingRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.*;


@Service
@Transactional  // 붙어서 더티체킹 ㅇㅇ
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StockInventoryService stockInventoryService;
//    private final StockDecreaseEventHandler stockDecreaseEventHandler;
    private final RestTemplate restTemplate;
    private final ProductFeign productFeign;
//    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository, OrderDetailRepository orderDetailRepository, StockInventoryService stockInventoryService, RestTemplate restTemplate, ProductFeign productFeign) {
        this.orderingRepository = orderingRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.stockInventoryService = stockInventoryService;
//        this.stockDecreaseEventHandler = stockDecreaseEventHandler;
        this.restTemplate = restTemplate;
        this.productFeign = productFeign;
//        this.kafkaTemplate = kafkaTemplate;
    }


    @Synchronized
    public Ordering orderRestTemplateCreate(List<OrderSaveReqDto> dtos) {
//       Synchronized 설정한다 하더라도, 재고 감소가 db에 반영되는 시점은 트랜잭션이 커밋되고 종료되는 시점
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Ordering ordering = Ordering.builder().memberEmail(memberEmail).build();

        for (OrderSaveReqDto dto : dtos) {
            int quantity = dto.getProductCount();
            // Product API에 요청을 통해 product객체를 조회해야 함
            String productGetUrl = "http://product-service/product/" + dto.getProductId();
            HttpHeaders httpHeaders = new HttpHeaders();
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            httpHeaders.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<CommonResDto> productEntity = restTemplate.exchange(productGetUrl, HttpMethod.GET, entity, CommonResDto.class);
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto productDto = objectMapper.convertValue(productEntity.getBody().getResult(), ProductDto.class);
            System.out.println(productDto);
            if (productDto.getName().contains("sale")) {
                //redis를 통한 재고관리 및 재고잔량 확인
                int newQuantity = stockInventoryService.decreaseStock(dto.getProductId(), dto.getProductCount()).intValue();
                if (newQuantity < 0) {
                    throw new IllegalArgumentException("재고 부족");
                }
//                rdb에 재고를 업데이트. rabbitmq를 통해 비동기적으로 이벤트 처리
//                stockDecreaseEventHandler.publish(new StockDecreaseEvent(productDto.getId(), dto.getProductCount()));
            } else {
                if (productDto.getStockQuantity() < quantity) {
                    throw new IllegalArgumentException("재고 부족");
                } else {
//                    restTemplate을 통한 update요청
//                    product.updateStockQuantity(quantity);
                    String updateUrl = "http://product-service/product/updatestock";
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<ProductUpdateStockDto> updateEntity = new HttpEntity<>(new ProductUpdateStockDto(dto.getProductId(), dto.getProductCount()), httpHeaders);
                    restTemplate.exchange(updateUrl, HttpMethod.PUT, updateEntity, void.class);
                }
            }

            // 변경 감지(더티체킹)으로 인해 별도의 save 불필요

            OrderDetail orderDetail = OrderDetail.builder().productId(productDto.getId()).quantity(quantity).ordering(ordering).build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrdering = orderingRepository.save(ordering);
        return savedOrdering;
    }


    public Ordering orderFeignClientCreate(List<OrderSaveReqDto> dtos) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Ordering ordering = Ordering.builder().memberEmail(memberEmail).build();

        for (OrderSaveReqDto dto : dtos) {
            int quantity = dto.getProductCount();
//            ResponseEntity가 기본응답값이므로 바로 CommonResDto로 매핑
            CommonResDto commonResDto = productFeign.getProductById(dto.getProductId());
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDto productDto = objectMapper.convertValue(commonResDto.getResult(), ProductDto.class);

            System.out.println(productDto);
            if (productDto.getName().contains("sale")) {
                int newQuantity = stockInventoryService.decreaseStock(dto.getProductId(), dto.getProductCount()).intValue();
                if (newQuantity < 0) {
                    throw new IllegalArgumentException("재고 부족");
                }
//                stockDecreaseEventHandler.publish(new StockDecreaseEvent(productDto.getId(), dto.getProductCount()));
            } else {
                if (productDto.getStockQuantity() < quantity) {
                    throw new IllegalArgumentException("재고 부족");
                } else {
                    productFeign.updateProductStock(new ProductUpdateStockDto(dto.getProductId(), dto.getProductCount()));
                }

            }
            OrderDetail orderDetail = OrderDetail.builder().productId(productDto.getId()).quantity(quantity).ordering(ordering).build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering savedOrdering = orderingRepository.save(ordering);
        return savedOrdering;
    }

//    public Ordering orderFeignKafKaCreate(List<OrderSaveReqDto> dtos) {
//        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        Ordering ordering = Ordering.builder().memberEmail(memberEmail).build();
//
//        for (OrderSaveReqDto dto : dtos) {
//            int quantity = dto.getProductCount();
////            ResponseEntity가 기본응답값이므로 바로 CommonResDto로 매핑
//            CommonResDto commonResDto = productFeign.getProductById(dto.getProductId());
//            ObjectMapper objectMapper = new ObjectMapper();
//            ProductDto productDto = objectMapper.convertValue(commonResDto.getResult(), ProductDto.class);
//
//            System.out.println(productDto);
//            if (productDto.getName().contains("sale")) {
//                int newQuantity = stockInventoryService.decreaseStock(dto.getProductId(), dto.getProductCount()).intValue();
//                if (newQuantity < 0) {
//                    throw new IllegalArgumentException("재고 부족");
//                }
////                stockDecreaseEventHandler.publish(new StockDecreaseEvent(productDto.getId(), dto.getProductCount()));
//            } else {
//                if (productDto.getStockQuantity() < quantity) {
//                    throw new IllegalArgumentException("재고 부족");
//                } else {
//                    ProductUpdateStockDto productUpdateStockDto = new ProductUpdateStockDto(dto.getProductId(), dto.getProductCount());
////                    kafkaTemplate.send("product-update-topic", productUpdateStockDto);
//                }
//
//            }
//            OrderDetail orderDetail = OrderDetail.builder().productId(productDto.getId()).quantity(quantity).ordering(ordering).build();
//            ordering.getOrderDetails().add(orderDetail);
//        }
//        Ordering savedOrdering = orderingRepository.save(ordering);
//        return savedOrdering;
//    }


    public List<OrderListResDto> listorder() {
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();

        for (Ordering order : orderings) {
            orderListResDtos.add(order.fromEntity());
        }
        return orderListResDtos;
    }


    public List<OrderListResDto> myOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Ordering> orderings = orderingRepository.findByMemberEmail(email);
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for (Ordering order : orderings) {
            orderListResDtos.add(order.fromEntity());
        }
        return orderListResDtos;
    }

    public Ordering orderCancle(Long id) {
        Ordering ordering = orderingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found order"));
        ordering.updateStatus(OrderStatus.CANCELLED);
        return ordering;
    }
}

