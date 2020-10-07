package com.example.tester.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MemberLoginRequestDto {
    private String userId;
    private String userPw;

    @Builder
    public MemberLoginRequestDto(String userId, String userPw) {
        this.userId = userId;
        this.userPw = userPw;
    }
}

