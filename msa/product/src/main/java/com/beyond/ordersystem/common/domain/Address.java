package com.beyond.ordersystem.common.domain;

import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 타 엔티티에서 사용 가능한 형태로 만드는 어노테이션
@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;
}