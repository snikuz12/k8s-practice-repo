package com.beyond.ordersystem.common.serivce;

import com.beyond.ordersystem.member.Domain.Role;
import com.beyond.ordersystem.member.Dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import com.beyond.ordersystem.member.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


// CommandLineRunner를 상속합으로써 해당 컴포넌트가 스프링빈으로 등록되는 시점에 run메서드 실행
@Component
public class InitialDataLoader implements CommandLineRunner{
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;


    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.findByEmail("admin@test").isEmpty()) {
            memberService.memberCreate(MemberSaveReqDto.builder()
                    .name("admin")
                    .email("admin@test")
                    .password("12341234")
                    .role(Role.ADMIN)
                    .build());
        }
    }
}
