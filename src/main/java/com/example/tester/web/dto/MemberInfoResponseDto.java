package com.example.tester.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberInfoResponseDto {
    private String userId;
    private String userName;
    private LocalDateTime lastLoginTime;

    @Builder
    public MemberInfoResponseDto(String userId, String userName, LocalDateTime lastLoginTime) {
        this.userId = userId;
        this.userName = userName;
        this.lastLoginTime = lastLoginTime;
    }
}
