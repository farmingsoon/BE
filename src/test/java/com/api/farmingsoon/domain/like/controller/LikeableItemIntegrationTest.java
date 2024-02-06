package com.api.farmingsoon.domain.like.controller;

import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.like.service.LikeableItemService;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.service.MemberService;
import com.api.farmingsoon.util.TestImageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LikeableItemIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private LikeableItemService likeableItemService;

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

    /**
     *  @ToDo 테스트 별 분리
     */
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
                    .viewCount(i)
                    .expiredAt(TimeUtils.setExpireAt(i)).build();

            List<String> imageUrl = new ArrayList<>(Arrays.asList("/subFile1/" + i, "/subFile2/" + i, "/subFile3/" + i));
            imageUrl.add(0, "/thumnailImage/" + i);

            Long itemId = itemService.saveItemAndImage(item, imageUrl);
            if(itemId % 2 == 0) // 짝수 상품에만 좋아요 등록
                likeableItemService.like(itemId);
        }

        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }
    /**
     * @Todo
     * 등록
     * 삭제
     * 조회
     */
    @DisplayName("관심 상품 등록 성공")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void likeableItemCreateSuccess() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post("/api/likeable-items/" + 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then

        Assertions.assertThat(getLikeableItemSize(1L)).isEqualTo(11);

        /**
         *  Todo
         *  등록만 컨트롤러 통하고 나머지 로직은 서비스 이용하는 방향으로 리팩토링하기
         *  이미지 저장도 같이 확인해야할 듯 ExpiredAt 체킹도!
         */
    }

    @Transactional(readOnly = true)
    public int getLikeableItemSize(long l) {
        return itemService.getItemById(1L).getLikeableItemList().size();
    }
}