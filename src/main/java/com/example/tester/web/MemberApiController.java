package com.example.tester.web;

import com.example.tester.domain.member.Member;
import com.example.tester.service.MemberService;
import com.example.tester.utils.JwtUtil;
import com.example.tester.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/member")
public class MemberApiController {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/join")
    public ResponseEntity<MsgResponseDto> joinMember(@RequestBody MemberSaveRequestDto requestDto) throws Exception {
        //중복된 이메일 계정 있는지 확인
        if(memberService.existDuplicateEmail(requestDto.getUserId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MsgResponseDto("이미 해당 아이디의 계정이 존재합니다!!"));
        }

        Member member = memberService.joinMember(requestDto);

        return ResponseEntity.ok(new MsgResponseDto(member.getUserId() + " 계정이 생성되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponseDto> loginMember(@RequestBody MemberLoginRequestDto loginRequest) {
        Member loginUser = (Member) memberService.loadUserByUsername(loginRequest.getUserId());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser, loginRequest.getUserPw()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.createToken(loginUser.getUserId(), loginUser.getName());

        return ResponseEntity.ok(
            MemberLoginResponseDto.builder()
            .userId(loginUser.getUserId())
            .userName(loginUser.getName())
            .accessToken(token)
            .build()
        );
    }

    @GetMapping("/info")
    public ResponseEntity<MemberLoginResponseDto> getMemberInfo(@AuthenticationPrincipal Member loginMember) {
        return ResponseEntity.ok(
                MemberLoginResponseDto.builder()
                .userId(loginMember.getUserId())
                .userName(loginMember.getName())
                .lastLoginTime(null)
                .build()
        );
    }
}
