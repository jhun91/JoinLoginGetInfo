package com.example.tester.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class MemberLoginResponseDto {
    private String userId;
    private String userName;
    private String auth;
    private String accessToken;
    private LocalDateTime lastLoginTime;

    @Builder
    public MemberLoginResponseDto(String userId, String userName, String auth, String accessToken, LocalDateTime lastLoginTime) {
        this.userId = userId;
        this.userName = userName;
        this.auth = auth;
        this.accessToken = accessToken;
        this.lastLoginTime = lastLoginTime;
    }

}
