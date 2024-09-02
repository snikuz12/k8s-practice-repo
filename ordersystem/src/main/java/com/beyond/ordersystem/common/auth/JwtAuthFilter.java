package com.beyond.ordersystem.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends GenericFilter {
    @Value("${jwt.secretKey}")
    private  String secretKey;

    @Value("${jwt.expiration}")
    private  int expiration;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String bearertoken = ((HttpServletRequest)servletRequest).getHeader("Authorization");

        try {
            if (bearertoken != null) {
//            token관레적으로 Bearer로 시작하는 문구를 넣어서 요청
                if (!bearertoken.substring(0, 7).equals("Bearer ")) {
                    throw new AuthenticationServiceException("Not in BEARER format");
                }
                String token = bearertoken.substring(7); // 7 이후 자리만 cut!
//            token 검증 및 claims(사용자 정보) 추출
//            token생성시에 사용한 secret 키값을 넣어 토큰 검증에 사용 밑에가 걍 검증 코드임
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token)
                        .getBody();
//            Authentication 객체 생성 : security  Holder에 넣어있음 (UserDetails 객체더 필요)

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            }
//        filterchain에서 그 다음 filtering으로 넘어가도록 하는 메서드
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (Exception e){
            log.error(e.getMessage());
            
            //컨트롤러가 없어서 수작업으로 해야함. controller보다 앞단계임
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("{=token?");
        }
    }
}
