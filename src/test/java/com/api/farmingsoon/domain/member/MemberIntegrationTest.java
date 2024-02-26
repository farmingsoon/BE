package com.api.farmingsoon.domain.member;

import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.dto.LoginRequest;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import com.api.farmingsoon.util.TestImageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MemberIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseCleanup databaseCleanup;
    private static MockMultipartFile profileImage;
    @BeforeAll
    static void beforeAll() throws IOException {
        profileImage = TestImageUtils.generateMockImageFile("profileImg");
    }
    @BeforeEach
    void beforeEach(){
        databaseCleanup.execute();
        JoinRequest joinRequest = JoinRequest.builder()
                .email("user1@naver.com")
                .nickname("user1")
                .password("12345678")
                .profileImg(profileImage).build();

        memberService.join(joinRequest);
    }

    @DisplayName("회원가입 성공")
    @Test
    void joinSuccess() throws Exception {

        //when
        MvcResult mvcResult = mockMvc.perform(multipart("/api/members/join")
                        .file(profileImage)
                        .param("email", "test@naver.com")
                        .param("password", "TestPassword1234@@")
                        .param("nickname", "testNickname")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        Long memberId = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").asLong();
        Member member = memberService.getMemberById(memberId);
        String encode = passwordEncoder.encode("TestPassword1234@@");

        Assertions.assertThat(member.getEmail()).isEqualTo("test@naver.com");
        Assertions.assertThat(member.getNickname()).isEqualTo("testNickname");
        Assertions.assertThat(passwordEncoder.matches("TestPassword1234@@", member.getPassword())).isTrue();
    }

   @DisplayName("로그인 성공")
    @Test
    void loginSuccess() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("user1@naver.com")
                .password("12345678").build();

        //when
        MvcResult mvcResult = mockMvc.perform(post("/api/members/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists("AccessToken"))
                .andExpect(cookie().exists("RefreshToken"))
                .andReturn();

    }

     /*
      @Description
      토큰 재발급 시 이전 토큰과 다른 토큰임을 확인
     */
    @DisplayName("토큰 재발급 성공")
    @Test
    void rotateTokenSuccess() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("user1@naver.com")
                .password("12345678").build();

        //when
        MvcResult mvcResult1 = mockMvc.perform(post("/api/members/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Optional<Cookie> refreshTokenCookie = Arrays.stream(mvcResult1.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .findFirst();
        Optional<Cookie> accessTokenCookie = Arrays.stream(mvcResult1.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("AccessToken"))
                .findFirst();


        MvcResult mvcResult2 = mockMvc.perform(get("/api/members/rotate")
                        .cookie(refreshTokenCookie.get())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Optional<Cookie> rotateRefreshTokenCookie = Arrays.stream(mvcResult2.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .findFirst();
        Optional<Cookie> rotateAccessTokenCookie = Arrays.stream(mvcResult2.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("AccessToken"))
                .findFirst();

        Assertions.assertThat(refreshTokenCookie.get()).isNotEqualTo(rotateRefreshTokenCookie.get());
        Assertions.assertThat(accessTokenCookie.get()).isNotEqualTo(rotateAccessTokenCookie.get());
    }
    /*
     * @Description
     * 로그아웃 후 재발급 요청 시 이미 로그아웃된 토큰으로 401 예외처리
     *
     * */
    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("user1@naver.com")
                .password("12345678").build();

        //when
        MvcResult mvcResult1 = mockMvc.perform(post("/api/members/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        Optional<Cookie> refreshTokenCookie = Arrays.stream(mvcResult1.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .findFirst();

        MvcResult mvcResult2 = mockMvc.perform(post("/api/members/logout")
                        .cookie(refreshTokenCookie.get()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        MvcResult mvcResult3 = mockMvc.perform(get("/api/members/rotate")
                        .cookie(refreshTokenCookie.get()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
}