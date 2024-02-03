package com.api.farmingsoon.domain.item.controller;

import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import com.api.farmingsoon.util.TestImageUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseCleanup databaseCleanup;
    private static MockMultipartFile thumbnailImage;
    private static List<MockMultipartFile> images;
    @BeforeAll
    static void beforeAll() throws IOException {
        thumbnailImage = TestImageUtils.generateMockImageFile("thumbnailImage");
        images = List.of(
                TestImageUtils.generateMockImageFile("images"),
                TestImageUtils.generateMockImageFile("images"),
                TestImageUtils.generateMockImageFile("images")
        );
    }
    @BeforeEach
    void beforeEach(){
        databaseCleanup.execute();
        JoinRequest joinRequest = JoinRequest.builder()
                .email("user1@naver.com")
                .nickname("user1")
                .password("12345678")
                .profileImg(thumbnailImage).build();

        memberService.join(joinRequest);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }



    @DisplayName("상품 등록 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void itemCreateSuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(multipart("/api/items")
                        .file(thumbnailImage)
                        .file(images.get(0))
                        .file(images.get(1))
                        .file(images.get(2))
                        .param("title", "아이폰 팔아요~")
                        .param("description", "합정 근처에서 거래 가능합니다.")
                        .param("hopePrice", "10000")
                        .param("period", "3")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        Long itemId = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").asLong();
        Item item = itemService.getItemById(itemId);

        Assertions.assertThat(item.getTitle()).isEqualTo("아이폰 팔아요~");
        Assertions.assertThat(item.getDescription()).isEqualTo("합정 근처에서 거래 가능합니다.");
        Assertions.assertThat(item.getHopePrice()).isEqualTo(10000);
        Assertions.assertThat(item.getItemStatus()).isEqualTo(ItemStatus.BIDDING);

        /**
         *  Todo
         *  이미지 저장도 같이 확인해야할 듯 ExpiredAt 체킹도!
         */


    }

    @DisplayName("상품 등록 후 상세 조회 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemDetailSuccess() throws Exception {
        MvcResult mvcResult1 = mockMvc.perform(multipart("/api/items")
                        .file(thumbnailImage)
                        .file(images.get(0))
                        .file(images.get(1))
                        .file(images.get(2))
                        .param("title", "아이폰 팔아요~")
                        .param("description", "합정 근처에서 거래 가능합니다.")
                        .param("hopePrice", "10000")
                        .param("period", "3")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        Long itemId = objectMapper.readTree(mvcResult1.getResponse().getContentAsString()).get("result").asLong();

        MvcResult mvcResult2 = mockMvc.perform(get("/api/items/" + itemId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode result = objectMapper.readTree(mvcResult2.getResponse().getContentAsString()).get("result");
        Assertions.assertThat(result.get("title").asText()).isEqualTo("아이폰 팔아요~");
        Assertions.assertThat(result.get("description").asText()).isEqualTo("합정 근처에서 거래 가능합니다.");
        Assertions.assertThat(result.get("hopePrice").asLong()).isEqualTo(10000L);
        Assertions.assertThat(result.get("itemStatus").asText()).isEqualTo("BIDDING");


    }
}