package com.beyond.ordersystem.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class redisConfig {

    // application.yml 의 spring.redis.host 정보를 소스코드의 변수로 가져오는 것.
    @Value("${spring.redis.host}")
    public String host;

    @Value("${spring.redis.port}")
    public int port;

    @Bean
    @Qualifier("2")
    // RedisConnectionFactory : Redis 서버와의 연결을 설정하는 역할
    // LettuceConnectionFactory : RedisConnectionFactory 구현체로서 실질적인 역할 수행
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
//        1번 db 사용
        redisStandaloneConfiguration.setDatabase(1);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    // redisTemplate : redis와 상호작용할때 redis key, value 형식을 정의
    @Bean
    @Qualifier("2")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("2") RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
//     redisTemplate.opsForValue().set(key, value);
//     redisTemplate.opsForValue().get(key);
//     redisTemplate.opsForValue().increment(); 증가
//     redisTemplate.opsForValue().decrement(); 감소

    @Bean
    @Qualifier("3")
    public RedisConnectionFactory stockFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(2);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    // redisTemplate : redis와 상호작용할때 redis key, value 형식을 정의
    @Bean
    @Qualifier("3")
    public RedisTemplate<String, Object> stockRedisTemplate(@Qualifier("3") RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}