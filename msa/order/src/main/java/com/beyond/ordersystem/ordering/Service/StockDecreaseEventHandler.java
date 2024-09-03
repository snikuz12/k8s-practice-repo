//package com.beyond.ordersystem.ordering.Service;
//
//import com.beyond.ordersystem.ordering.Dto.StockDecreaseEvent;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import com.beyond.ordersystem.common.config.RebbitMqConfig;
//
//
//@Component
//public class StockDecreaseEventHandler {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//
//    public void publish(StockDecreaseEvent event){
//        rabbitTemplate.convertAndSend(RebbitMqConfig.STOCK_DECREASE_QUEUE,event);
//    }
//
//    @Transactional // 트랜잭션 처리가 다 끝나고 나서 그 다음 메시지 수신하므로, 동시성 이슈 x
//    @RabbitListener(queues = RebbitMqConfig.STOCK_DECREASE_QUEUE)
//    public void  listen(Message message){
//        String messageBody = new String(message.getBody());
////        System.out.println(messageBody)
//        // 재고 업데이트. json 메시지를 parsing (objectparser, stockDecreaseEvent)
//        ObjectMapper objectMapper = new ObjectMapper();
//        try{
//            StockDecreaseEvent stockDecreaseEvent = objectMapper.readValue(messageBody,StockDecreaseEvent.class);
////            Product product = productRepository.findById(stockDecreaseEvent.getProductId()).orElseThrow(()->new EntityNotFoundException("product not found"));
////            product.updateStockQuantity(stockDecreaseEvent.getProductCount());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//}