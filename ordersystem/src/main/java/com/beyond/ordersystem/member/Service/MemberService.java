package com.beyond.ordersystem.member.Service;

import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Dto.MemberLoginDto;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.member.Dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.Dto.ResetPasswordDto;
import com.beyond.ordersystem.member.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;



@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member memberCreate(MemberSaveReqDto createDto){
        if(memberRepository.findByEmail(createDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 Email 입니다.");
        }
        if (createDto.getPassword().length() < 8){
            throw new IllegalArgumentException("비밀번호의 길이가 짧습니다.");
        }
        Member member = createDto.toEntity(passwordEncoder.encode(createDto.getPassword()));
        Member success = memberRepository.save(member);
        return success;
    }
    public Page<MemberResDto> memberList(Pageable pageable){
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(a->a.fromEntity());
    }


    public MemberResDto myinfo(){
       Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
               .orElseThrow(()->new EntityNotFoundException("member not found"));
       return member.fromEntity();
    }

    public Member login(MemberLoginDto dto){
        // email 존재 여부
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 이메일입니다."));
        // password 일치 여부 => 들어온 dto 의 password 를 암호화해서 DB 에서 암호화되어 들어있던 비밀번호와 비교.
        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public void resetPassword(ResetPasswordDto dto){
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 이메일입니다."));
        if(!passwordEncoder.matches(dto.getAsIsPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        member.updatePassword(passwordEncoder.encode(dto.getToBePassword()));
    }


}



