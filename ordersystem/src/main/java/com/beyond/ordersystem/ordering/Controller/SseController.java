package com.beyond.ordersystem.ordering.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SseController {
//    SseEmitter는 연결된 사용자 정보를 의미
//    ConcurrentHashMap은 Thread-safe한 map(동시성 이슈발생 x)
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(14400*60*1000L); // 30분정도로 emitter 유효시간 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        emitters.put(email, emitter);
        emitter.onCompletion(()->emitters.remove(email));
        emitter.onTimeout(()->emitters.remove(email));
        try{
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        }catch(IOException e){
            e.printStackTrace();
        }
        return emitter;
    }
}
