package com.beyond.ordersystem.member.Dto;

import com.beyond.ordersystem.common.domain.Address;
import com.beyond.ordersystem.member.Domain.Member;
import com.beyond.ordersystem.member.Domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResDto {
        private Long id;
        private String name;
        private String email;
        private Address address;
        private Role role;
        private int orderCount;


    }

