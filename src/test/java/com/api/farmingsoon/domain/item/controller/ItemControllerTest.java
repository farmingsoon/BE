package com.api.farmingsoon.domain.item.controller;

import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.domain.bid.dto.BidRequest;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.bid.model.BidResult;
import com.api.farmingsoon.domain.bid.service.BidService;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.api.farmingsoon.domain.item.dto.ItemListResponse;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    private BidService bidService;

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

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_MEMBER"));

        UserDetails principal = new User("user1@naver.com", "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));

        for(int i = 1; i <= 20; i++){
            Item item = Item.builder()
                    .title("title" + i)
                    .description("description" + i)
                    .hopePrice(10000 * i)
                    .itemStatus(ItemStatus.BIDDING)
                    .viewCount((long) i)
                    .expiredAt(TimeUtils.setExpireAt(i)).build();

            List<String> imageUrl = new ArrayList<>(Arrays.asList("/subFile1/" + i, "/subFile2/" + i, "/subFile3/" + i));
            imageUrl.add(0, "/thumnailImage/" + i);

            itemService.saveItemAndImage(item, imageUrl);

            bidService.bid(BidRequest.builder().itemId(item.getId()).price(10000 * i).build());
        }

        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }



    @DisplayName("상품 등록 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void itemCreateSuccess() throws Exception {
        // when
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
        // when
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
        // then
        Long itemId = objectMapper.readTree(mvcResult1.getResponse().getContentAsString()).get("result").asLong();

        MvcResult mvcResult2 = mockMvc.perform(get("/api/items/" + itemId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        JsonNode result = objectMapper.readTree(mvcResult2.getResponse().getContentAsString()).get("result");
        Assertions.assertThat(result.get("title").asText()).isEqualTo("아이폰 팔아요~");
        Assertions.assertThat(result.get("description").asText()).isEqualTo("합정 근처에서 거래 가능합니다.");
        Assertions.assertThat(result.get("hopePrice").asLong()).isEqualTo(10000L);
        Assertions.assertThat(result.get("itemStatus").asText()).isEqualTo("경매중");



    }

    @DisplayName("상품 목록 조회(Default) 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsSuccess() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        ItemListResponse itemListResponse = objectMapper.readValue(result, ItemListResponse.class);

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title1");
        Assertions.assertThat(itemListResponse.getItems().get(11).getTitle()).isEqualTo("title12");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(20L,12);
    }

    @DisplayName("상품 목록 조회 최고가 순 정렬 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsOrderByHighestPrice() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("sort", "highest,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        ItemListResponse itemListResponse = objectMapper.readValue(result, ItemListResponse.class);

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title20");
        Assertions.assertThat(itemListResponse.getItems().get(11).getTitle()).isEqualTo("title9");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(20L,12);
    }

    @DisplayName("상품 목록 조회 최저가 순 정렬 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsOrderByLowestPrice() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("sort", "highest,asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        ItemListResponse itemListResponse = objectMapper.readValue(result, ItemListResponse.class);

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title1");
        Assertions.assertThat(itemListResponse.getItems().get(11).getTitle()).isEqualTo("title12");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(20L,12);
    }

    @DisplayName("상품 목록 조회 인기순 정렬 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsOrderByViewCount() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("sort", "hot,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        ItemListResponse itemListResponse = objectMapper.readValue(result, ItemListResponse.class);

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title20");
        Assertions.assertThat(itemListResponse.getItems().get(11).getTitle()).isEqualTo("title9");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(20L,12);
    }

    @DisplayName("검색어로 상품 목록 조회")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsByKeyword() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("keyword","5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        ItemListResponse itemListResponse = objectMapper.readValue(result, ItemListResponse.class);

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title5");
        Assertions.assertThat(itemListResponse.getItems().get(1).getTitle()).isEqualTo("title15");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(20L,2);
    }
}