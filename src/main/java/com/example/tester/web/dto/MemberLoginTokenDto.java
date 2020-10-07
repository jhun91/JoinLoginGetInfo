package com.example.tester.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginTokenDto {
    private String accessToken;

    @Builder
    public MemberLoginTokenDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
