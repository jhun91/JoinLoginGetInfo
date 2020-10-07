package com.example.tester.web;

import com.example.tester.domain.member.Member;
import com.example.tester.domain.member.MemberRepository;
import com.example.tester.service.MemberService;
import com.example.tester.web.dto.MemberLoginRequestDto;
import com.example.tester.web.dto.MemberSaveRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberApiControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Member initMember;

    @BeforeEach
    public void init() throws Exception {

        String userId = "test@user.com";
        String userName = "tester";
        String userPw = "testUser12!@";
        initMember = memberService.joinMember(
                MemberSaveRequestDto.builder()
                        .userId(userId)
                        .userPw(userPw)
                        .name(userName)
                        .auth("ROLE_USER")
                        .build()
        );
    }

    @AfterEach
    public void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    public void regex_test() {
        //given
        String userId = "user@test.co.kr";
        String userPwLowerNum = "testuser123456";
        String userPwUpperNum = "TESTUSER123456";
        String userPwUppLowNum = "testUser123456";
        String userPwUppLowSpc = "testUser123$%^";

        String idRegex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        String pwRegex = "^(?:(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])|(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])|(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]))[A-Za-z\\d!@#$%^&*]{12,}$";

        System.out.println("ID 정규식 : " + userId.matches(idRegex));
        System.out.println("PW 정규식1 : " + userPwLowerNum + " > " + userPwLowerNum.matches(pwRegex));
        System.out.println("PW 정규식2 : " + userPwUpperNum + " > " + userPwUpperNum.matches(pwRegex));
        System.out.println("PW 정규식3 : " + userPwUppLowNum + " > " + userPwUppLowNum.matches(pwRegex));
        System.out.println("PW 정규식4 : " + userPwUppLowSpc + " > " + userPwUppLowSpc.matches(pwRegex));
    }

    @Test
    public void passwordEncodeTest() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String userPw = "Testuser12@#";
        String encPw = encoder.encode(userPw);

        assertThat(encoder.matches(userPw, encPw));
    }

    @Test
    public void joinTest() throws Exception {

        String userId = "test1@user.com";  //"test@user.com";
        String userName = "tester1";
        String userPw = "testUser12!@";

        String url = "http://localhost:" + port + "/v1/member/join";

        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .userId(userId)
                .userPw(userPw)
                .name(userName)
                .auth("ROLE_USER")
                .build();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestDto, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        //assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        System.out.println("MemberApiControllerTests joinTest :: " + responseEntity.getBody());
    }

    @Test
    public void loginTest() {
        String userId = "test@user.com";
        String userPw = "testUser12!@";

        String url = "http://localhost:" + port + "/v1/member/login";

        MemberLoginRequestDto requestDto = MemberLoginRequestDto.builder()
                .userId(userId)
                .userPw(userPw)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestDto, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("MemberApiControllerTests loginTest :: " + responseEntity.getBody());
    }

    @Test
    public void memberInfoTest() {
        String url = "http://localhost:" + port + "/v1/member/info";

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        //assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        System.out.println("MemberApiControllerTests memberInfoTest :: " + responseEntity.getBody());
    }

}