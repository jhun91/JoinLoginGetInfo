package com.example.tester.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoResponseDto {
    private String userId;
    private String userName;
    private String lastLoginTime;

    @Builder
    public MemberInfoResponseDto(String userId, String userName, String lastLoginTime) {
        this.userId = userId;
        this.userName = userName;
        this.lastLoginTime = lastLoginTime;
    }
}
