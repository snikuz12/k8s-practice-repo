package com.beyond.ordersystem.common.config;

import com.beyond.ordersystem.common.auth.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity // spring security 설정을 customizing 화기 위함.
@EnableGlobalMethodSecurity(prePostEnabled = true) // pre : 사전 post : 사후 인증검사
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;


//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .cors().and()   //CORS활성화 : Cross Origin Resource Strict => 다른 도메인에서 서버로 호출
                .httpBasic().disable()
                .authorizeRequests()
                // 예외 url만 허용하겠다.
                .antMatchers("/member/create", "/","/member/doLogin","/member/refresh-token","/member/reset-password")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                // 세션로그인이 아닌 stateless한 token 사용하겠다는 의미
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 로그인시 사용자는 서버로부터 토큰을 발급닥고 매 요청마다 해당 토큰을 httpheader 넣어 요청
                // 아래 코드는 사용자로부터 받아온 토큰이 정산인지 아닌지를 검증하는 코드
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}