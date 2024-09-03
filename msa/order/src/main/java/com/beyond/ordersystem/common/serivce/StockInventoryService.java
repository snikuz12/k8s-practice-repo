package com.beyond.ordersystem.common.serivce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockInventoryService {
    @Qualifier("3")
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public StockInventoryService(@Qualifier("3") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 상품 등록시 increaseStock 호출
    public Long increaseStock(Long itemId, int quantity){
        //redis가 음수까지 내려갈 경우 추후 재고 update 상황에서 inreases값이 정확하지 않을 수 있으므로.
//        음수면 0으로 setting로직이 필요
        // 아래 메서드의 리턴 값은 잔량값
        return redisTemplate.opsForValue().increment(String.valueOf(itemId), quantity);
    }

    // 주문 등록시 호출
    public Long decreaseStock(Long itemId, int quantity){
        Object remains = redisTemplate.opsForValue().get(String.valueOf(itemId));
        int longRemains = Integer.parseInt(remains.toString());
        if(longRemains < quantity){
            return -1L;
        }else{
//            남아있는 잔량 리턴
            return redisTemplate.opsForValue().decrement(String.valueOf(itemId), quantity);
        }
        
    }

}
