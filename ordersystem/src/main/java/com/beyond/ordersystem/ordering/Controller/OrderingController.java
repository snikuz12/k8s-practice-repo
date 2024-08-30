package com.beyond.ordersystem.ordering.Controller;

import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import com.beyond.ordersystem.ordering.Dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.Service.OrderingService;
import com.beyond.ordersystem.ordering.Dto.OrderListResDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/order")
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> orderCreate(@RequestBody List<OrderSaveReqDto> dto) {
        Ordering ordering = orderingService.orderCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "정상완료", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> listorder() {
        List<OrderListResDto> ordering = orderingService.listorder();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "정상조회완료", ordering);

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


    @GetMapping("/myorders")
    public ResponseEntity<?> myOrders() {
        List<OrderListResDto> ordering = orderingService.myOrders();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "개인 주문 조회 성공 !", ordering);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }
    
    
    //admin 사용자가 주문취소 : /order/{id}/cancel -> orderstatus만 변경

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> orderCancel(@PathVariable Long id) {
        Ordering ordering = orderingService.orderCancle(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "취소 완료", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }
}

