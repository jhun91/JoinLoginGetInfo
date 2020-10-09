package com.example.tester.web;

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
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @BeforeEach
    public void init() throws Exception {

        String userId = "test@user.com";
        String userName = "tester";
        String userPw = "testUser12!@";
        memberService.joinMember(
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
    public void time_format_test() {
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println(currentTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS")));
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
    public void joinTest() {

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

        //위에 선언한 init의 테스트 계정 및 로그인 이후
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJ0ZXN0QHVzZXIuY29tIiwidXNlck5hbWUiOiJ0ZXN0ZXIifQ.MpW7dCZVLPEHmsBvoZLe5lMee3zeSP6Zdpw9dVxX-aM";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + token);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class,
                1
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("MemberApiControllerTests memberInfoTest :: " + responseEntity.getBody());
    }

}