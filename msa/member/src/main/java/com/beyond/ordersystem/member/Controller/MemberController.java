package com.beyond.ordersystem.member.Controller;

import com.beyond.ordersystem.common.auth.jwtTokenprovider;
import com.beyond.ordersystem.common.dto.CommonErrorDto;
import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.*;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import com.beyond.ordersystem.member.Service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequestMapping("/member")
@RestController
public class MemberController {

    @Value("${jwt.secretKeyRT}")
    private  String secretKey;


    private final MemberService memberService;
    private final jwtTokenprovider jwtTokenProvider;
    @Qualifier("2")
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public MemberController(MemberService memberService, jwtTokenprovider jwtTokenProvider, @Qualifier("2") RedisTemplate<String, Object> redisTemplate) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }
    @PostMapping("/create")
    public ResponseEntity<?> memberCreate (@Valid @RequestBody MemberSaveReqDto createDto) {
        memberService.memberCreate(createDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "회원 가입 성공 !", createDto);
        ResponseEntity<CommonResDto> result = new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        return result;
    }


    //admin만 회원 목록 전체 조회 가능하게
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> memberList(Pageable pageable){
        Page<MemberResDto> listDto = memberService.memberList(pageable);
        memberService.memberList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "회원 목록 조회 성공 !", listDto);
        ResponseEntity<CommonResDto> result = new ResponseEntity<>(commonResDto, HttpStatus.OK);
        return result;
    }
    
    
    //본인 회원정보만 조회 가능
    @GetMapping("/myinfo")
    public ResponseEntity<MemberResDto> myinfo() {
        // 현재 인증된 사용자 정보 가져오기
        MemberResDto memberResDto = memberService.myinfo();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "개인회원 조회 성공 !", memberResDto);
        return new ResponseEntity(commonResDto, HttpStatus.OK);
    }


    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        memberService.resetPassword(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "password is renewed" , "ok");
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/doLogin")
    public ResponseEntity doLogin(@RequestBody MemberLoginDto dto) {

        // email, password가 일치한지 검증
        Member member = memberService.login(dto);

        // 일치할 경우 accessToken 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole().toString());

        // redis에 email과 rt를 key:value로 하여 저장
        redisTemplate.opsForValue().set(member.getEmail(), refreshToken, 240, TimeUnit.HOURS); // 240시간
        // 생성된 토큰을 commonResDto에 담아 사용자에게 return
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("refreshToken", refreshToken);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "login is successful", loginInfo);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> generatedNewAt(@RequestBody MemberRefreshDto dto){
        String rt = dto.getRefreshToken();
        Claims claims = null;

        try {
            
            // 코드를 통해 rt 검증
            claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(rt)
                    .getBody();
        }catch (Exception e) {
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(),
                    "invalid refresh roken"), HttpStatus.BAD_REQUEST);
        }
        String email = claims.getSubject();
        String role = claims.get("role").toString();
        
        //redis를 조회하여 rt 추가 검증
        Object obj = redisTemplate.opsForValue().get(email);
//        if(obj == null || (obj != null && !obj.toString().equals(rt))){
        if(obj == null || !obj.toString().equals(rt)){
            return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST.value(),
                    "invalid refresh roken"), HttpStatus.BAD_REQUEST);
        }

        String newAt = jwtTokenProvider.createToken(email, role);
        Map<String, Object> info = new HashMap<>();
        info.put("token", newAt);

        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "AT is renewed", info);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


}