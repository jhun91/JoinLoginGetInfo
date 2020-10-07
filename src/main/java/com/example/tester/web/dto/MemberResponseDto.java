package com.example.tester.web.dto;

import com.example.tester.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private Long id;
    private String userId;
    private String userName;
    private String auth;

    public MemberResponseDto(Member entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.userName = entity.getName();
        this.auth = entity.getAuth();
    }
}
