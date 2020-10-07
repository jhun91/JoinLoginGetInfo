package com.example.tester.service;

import com.example.tester.domain.member.Member;
import com.example.tester.domain.member.MemberRepository;
import com.example.tester.web.dto.MemberSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 계정 중복 확인
     */
    public boolean existDuplicateEmail(String userId) {
        return memberRepository.findByUserId(userId).isPresent();
    }

    /**
     * 회원 등록
     * ID는 이메일 양식에 맞아야 함
     * PW는 12자 이상 영어 대문자, 소문자, 특수문자, 숫자 중 최소 3가지가 섞여야 함
     * @param requestDto
     * @return ResponseEntity<MemberResponseDto>
     * @throws Exception
     */
    @Transactional
    public Member joinMember(MemberSaveRequestDto requestDto) throws Exception {
        String userId = requestDto.getUserId();
        String userPw = requestDto.getUserPw();

        String idRegex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        String pwRegex = "^(?:(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])|(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])|(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]))[A-Za-z\\d!@#$%^&*]{12,}$";

        //ID 생성규칙 맞지 않을 경우
        if (userId.equals("") || !userId.matches(idRegex)) {
            throw new IllegalArgumentException("아이디 생성 규칙에 맞지 않습니다.");
        }

        if (userPw.equals("") || !userPw.matches(pwRegex)) {
            throw new IllegalArgumentException("비밀번호 규칙에 맞지 않습니다.");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        //비밀번호 암호화
        requestDto.setUserPw(encoder.encode(userPw));

        requestDto.setAuth("ROLE_USER");

        return memberRepository.save(requestDto.toEntity());
    }

    /**
     * 접속한 사람의 마지막 로그인 일시 저장
     */
    public void updateLastLoginTime(Member loginMember) {
        Optional<Member> member = memberRepository.findByUserId(loginMember.getUserId());
        memberRepository.updateLastLoginTime(member.get().getId(), LocalDateTime.now());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException((username)));
    }
}
