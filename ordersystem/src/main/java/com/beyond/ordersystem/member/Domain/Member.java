package com.beyond.ordersystem.member.Domain;

import com.beyond.ordersystem.common.domain.Address;
import com.beyond.ordersystem.common.domain.BaseTimeEntity;
import com.beyond.ordersystem.member.Dto.MemberResDto;
import com.beyond.ordersystem.ordering.Domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Ordering> orderingList;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;


    public MemberResDto fromEntity() {
        return MemberResDto.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .address(this.address)
                .orderCount(this.orderingList.size())
                .build();
    }

    public void updatePassword(String password){
        this.password = password;
    }
}

