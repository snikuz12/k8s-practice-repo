package com.beyond.ordersystem.member.Repository;

import com.beyond.ordersystem.member.Domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Page<Member> findAll(Pageable pageable);
    List<Member> findAll();
}

