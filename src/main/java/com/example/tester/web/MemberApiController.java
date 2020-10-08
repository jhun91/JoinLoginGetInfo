package com.example.tester.web;

import com.example.tester.domain.member.Member;
import com.example.tester.service.MemberService;
import com.example.tester.utils.JwtUtil;
import com.example.tester.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        if (memberService.existDuplicateEmail(requestDto.getUserId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MsgResponseDto("이미 해당 아이디의 계정이 존재합니다!!"));
        }

        Member member = memberService.joinMember(requestDto);

        return ResponseEntity.ok(new MsgResponseDto(member.getUserId() + " 계정이 생성되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginTokenDto> loginMember(@RequestBody MemberLoginRequestDto loginRequest) {
        Member loginMember = (Member) memberService.loadUserByUsername(loginRequest.getUserId());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginMember, loginRequest.getUserPw()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //토큰 생성
        String token = jwtUtil.createToken(loginMember.getUserId(), loginMember.getName());

        //마지막 로그인 일시 저장
        memberService.updateLastLoginTime(loginMember);

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "Bearer " + token);

        return new ResponseEntity<>(
                MemberLoginTokenDto.builder()
                        .accessToken(token)
                        .build()
                , headers
                , HttpStatus.OK
        );
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal Member loginMember) {

        if (loginMember == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MsgResponseDto("로그인이 필요합니다."));
        }

        //String token = jwtUtil.createToken(loginMember.getUserId(), loginMember.getName());

        LocalDateTime lastLoginTime = loginMember.getLastLoginTime();
        String formattedTime = "";

        if (lastLoginTime != null) {
            formattedTime = lastLoginTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS"));
        }

        return ResponseEntity.ok(
                MemberInfoResponseDto.builder()
                        .userId(loginMember.getUserId())
                        .userName(loginMember.getName())
                        .lastLoginTime(formattedTime)
                        .build()
        );
    }
}
