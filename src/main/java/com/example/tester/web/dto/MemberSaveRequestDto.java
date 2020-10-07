package com.example.tester.web.dto;


import com.example.tester.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MemberSaveRequestDto {
    private String userId;
    private String userPw;
    private String name;
    private String auth;

    @Builder
    public MemberSaveRequestDto(String userId, String userPw, String name, String auth) {
        this.userId = userId;
        this.userPw = userPw;
        this.name = name;
        this.auth = auth;
    }

    public Member toEntity() {
        return Member.builder()
                .userId(userId)
                .userPw(userPw)
                .name(name)
                .auth(auth)
                .build();
    }

}
