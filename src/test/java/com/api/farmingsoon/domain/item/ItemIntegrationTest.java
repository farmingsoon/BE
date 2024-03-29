package com.api.farmingsoon.domain.item;

import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.domain.IntegrationTest;
import com.api.farmingsoon.domain.bid.dto.BidRequest;
import com.api.farmingsoon.domain.bid.service.BidService;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.api.farmingsoon.domain.item.dto.ItemListResponse;
import com.api.farmingsoon.domain.item.dto.MyItemListResponse;
import com.api.farmingsoon.domain.item.dto.SoldOutRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ItemIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemService itemService;

    @Autowired
    private BidService bidService;

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
        for(int i = 1; i <= 2; i++){
            JoinRequest joinRequest = JoinRequest.builder()
                    .email("user" + i +"@naver.com")
                    .nickname("user" + i)
                    .password("12345678")
                    .profileImg(thumbnailImage).build();
            memberService.join(joinRequest);
        }


        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_MEMBER"));

        UserDetails principal = new User("user1@naver.com", "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));

        for(int i = 1; i <= 20; i++){
            Item item = Item.builder()
                    .title("title" + i)
                    .description("description" + i)
                    .hopePrice(10000 * i)
                    .bidPeriod(i)
                    .itemStatus(ItemStatus.BIDDING)
                    .viewCount(i)
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

    @DisplayName("상품 등록 후 상세 조회 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItems() throws Exception {

        MvcResult mvcResult2 = mockMvc.perform(get("/api/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();



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
         *  등록만 컨트롤러 통하고 나머지 로직은 서비스 이용하는 방향으로 리팩토링하기
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
        Assertions.assertThat(result.get("highestPrice").asLong()).isEqualTo(0);
        Assertions.assertThat(result.get("lowestPrice").asLong()).isEqualTo(0);
        Assertions.assertThat(result.get("bidCount").asLong()).isEqualTo(0);
        Assertions.assertThat(result.get("likeCount").asLong()).isEqualTo(0);
        Assertions.assertThat(result.get("viewCount").asLong()).isEqualTo(0);
        Assertions.assertThat(result.get("itemStatus").asText()).isEqualTo("경매중");


        /**
         *  Todo
         *  등록만 서비스로하고 조회하는걸 컨트롤러 이용하도록 리팩토링하기
         *  이미지 저장도 같이 확인해야할 듯 ExpiredAt 체킹도!
         */

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

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title20");
        Assertions.assertThat(itemListResponse.getItems().get(11).getTitle()).isEqualTo("title9");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(20L,12);
    }

    @DisplayName("판매완료된 상품 조회")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getSoldOutItems() throws Exception {
        for(int i = 1; i <= 10; i++){
            itemService.soldOut((long) i, SoldOutRequest.builder().awardPrice(10000 * (20 - i)).buyerId(2L).build());
        }
        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("itemStatus", "SOLDOUT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        ItemListResponse itemListResponse = objectMapper.readValue(result, ItemListResponse.class);

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title10");
        Assertions.assertThat(itemListResponse.getItems().get(9).getTitle()).isEqualTo("title1");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(10L,10);
    }

    @DisplayName("상품 목록 조회 낙찰가 최고가 순")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsOrderByHighestPrice() throws Exception {
        for(int i = 1; i <= 20; i++){
            itemService.soldOut((long) i, SoldOutRequest.builder().awardPrice(10000 * (20 - i)).buyerId(2L).build());
        }


        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("sortCode", "highest")
                        .param("itemStatus", "SOLDOUT"))
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

    @DisplayName("상품 목록 조회 낙찰가 최저가 순")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsOrderByLowestPrice() throws Exception {
        for(int i = 1; i <= 20; i++){
            itemService.soldOut((long) i, SoldOutRequest.builder().awardPrice(10000 * (20 - i)).buyerId(2L).build());
        }
        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("sortCode", "lowest")
                        .param("itemStatus", "SOLDOUT"))
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

    @DisplayName("상품 목록 조회 인기순")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsOrderByViewCount() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("sortCode", "hot"))
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

    @DisplayName("상품 목록 조회 마감임박순")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getItemsOrderByDeadlineImminent() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items")
                        .param("sortCode", "imminent"))
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

        Assertions.assertThat(itemListResponse.getItems().get(0).getTitle()).isEqualTo("title15");
        Assertions.assertThat(itemListResponse.getItems().get(1).getTitle()).isEqualTo("title5");

        Assertions.assertThat(itemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(2L,2);
    }

    @DisplayName("상품 등록 후 삭제")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteItem() throws Exception {
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

        MvcResult mvcResult2 = mockMvc.perform(delete("/api/items/" + itemId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();



        /**
         *  Todo
         *  등록만 서비스로하고 조회하는걸 컨트롤러 이용하도록 리팩토링하기
         *  이미지 저장도 같이 확인해야할 듯 ExpiredAt 체킹도!
         */

    }

    @DisplayName("내가 등록한 상품 조회")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getMyItems() throws Exception {

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/items/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        MyItemListResponse myItemListResponse = objectMapper.readValue(result, MyItemListResponse.class);

        Assertions.assertThat(myItemListResponse.getItems().get(0).getTitle()).isEqualTo("title20");
        Assertions.assertThat(myItemListResponse.getItems().get(11).getTitle()).isEqualTo("title9");

        Assertions.assertThat(myItemListResponse.getPagination()).isNotNull()
                .extracting("totalElementSize", "elementSize")
                .contains(20L,12);
    }
    @DisplayName("판매완료 처리 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void soldOutSuccess() throws Exception {
        SoldOutRequest soldOutRequest = SoldOutRequest.builder().awardPrice(50000).buyerId(2L).build();
        // when
        MvcResult mvcResult = mockMvc.perform(patch("/api/items/1/sold-out")
                        .content(objectMapper.writeValueAsString(soldOutRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        // then
        Assertions.assertThat(itemService.getItemDetail(1L).getAwardPrice()).isEqualTo(50000);
        Assertions.assertThat(itemService.getItemDetail(1L).getItemStatus()).isEqualTo(ItemStatus.SOLDOUT.getStatus());
    }
    @DisplayName("판매완료 처리 실패")
    @WithUserDetails(value = "user2@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void soldOutFail() throws Exception {

        SoldOutRequest soldOutRequest = SoldOutRequest.builder().awardPrice(50000).buyerId(2L).build();
        // when
        MvcResult mvcResult = mockMvc.perform(patch("/api/items/1/sold-out")
                        .content(objectMapper.writeValueAsString(soldOutRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
        Assertions.assertThat(itemService.getItemDetail(1L).getAwardPrice()).isNull();
    }

    // @Todo 내가 입찰에 참여한 상품 조회
}