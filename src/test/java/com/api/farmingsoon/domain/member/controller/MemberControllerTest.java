package com.api.farmingsoon.domain.member.controller;

import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import com.api.farmingsoon.util.TestImageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("회원가입 성공")
    @Test
    void createMember() throws Exception {
        MockMultipartFile profileImage = TestImageUtils.generateMockImageFile("profileImg");
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

}