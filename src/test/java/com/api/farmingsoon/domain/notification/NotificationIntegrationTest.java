package com.api.farmingsoon.domain.notification;

import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.common.util.Transaction;
import com.api.farmingsoon.domain.IntegrationTest;
import com.api.farmingsoon.domain.bid.dto.BidRequest;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.bid.model.BidResult;
import com.api.farmingsoon.domain.bid.repository.BidRepository;
import com.api.farmingsoon.domain.bid.service.BidService;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.api.farmingsoon.domain.item.dto.SoldOutRequest;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import com.api.farmingsoon.domain.notification.dto.NotificationListResponse;
import com.api.farmingsoon.domain.notification.model.Notification;
import com.api.farmingsoon.domain.notification.repository.NotificationRepository;
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


public class NotificationIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemService itemService;

    @Autowired
    private BidService bidService;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private static MockMultipartFile profileImage;
    @BeforeAll
    static void beforeAll() throws IOException {
        profileImage = TestImageUtils.generateMockImageFile("profileImage");
    }

    @BeforeEach
    void beforeEach(){
        databaseCleanup.execute();
        for(int i = 1; i <= 4; i++){
            JoinRequest joinRequest = JoinRequest.builder()
                    .email("user" + i + "@naver.com")
                    .nickname("user" + i)
                    .password("12345678")
                    .profileImg(profileImage).build();

            memberService.join(joinRequest);
        }

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_MEMBER"));

        UserDetails principal = new User("user1@naver.com", "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));


        Item item = Item.builder()
                .title("title")
                .description("description")
                .hopePrice(10000)
                .bidPeriod(1)
                .itemStatus(ItemStatus.BIDDING)
                .viewCount(0)
                .expiredAt(TimeUtils.setExpireAt(3)).build();

        List<String> imageUrl = new ArrayList<>(Arrays.asList("/subFile1", "/subFile2" , "/subFile3"));
        imageUrl.add(0, "/thumnailImage");

        itemService.saveItemAndImage(item, imageUrl);
        bidRepository.save(Bid.of(item, memberService.getMemberByEmail("user2@naver.com"), 10000, BidResult.BIDDING));
        bidRepository.save(Bid.of(item, memberService.getMemberByEmail("user3@naver.com"), 10000, BidResult.BIDDING));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @DisplayName("상품에 입찰 등록 시 알림 생성")
    @WithUserDetails(value = "user3@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void bidNotification() throws Exception {
        //given
        bidService.bid(BidRequest.builder().itemId(1L).price(10000).build());
        Member seller = memberService.getMemberByEmail("user1@naver.com");
        Member buyer1 = memberService.getMemberByEmail("user2@naver.com");
        Member buyer2 = memberService.getMemberByEmail("user3@naver.com");

        // when
        List<Notification> notificationBySeller = notificationRepository.findByReceiverAndReadDateIsNull(seller);
        List<Notification> notificationByBuyer1 = notificationRepository.findByReceiverAndReadDateIsNull(buyer1);
        List<Notification> notificationByBuyer2 = notificationRepository.findByReceiverAndReadDateIsNull(buyer2);

        //then
        Assertions.assertThat(notificationBySeller.get(0).getItemId()).isEqualTo(1L);
        Assertions.assertThat(notificationBySeller.get(0).getMessage()).isEqualTo("새로운 입찰이 등록되었습니다.");
        Assertions.assertThat(notificationBySeller.get(0).getReadDate()).isNull();

        Assertions.assertThat(notificationByBuyer1.get(0).getItemId()).isEqualTo(1L);
        Assertions.assertThat(notificationByBuyer1.get(0).getMessage()).isEqualTo("새로운 입찰이 등록되었습니다.");
        Assertions.assertThat(notificationByBuyer1.get(0).getReadDate()).isNull();

        Assertions.assertThat(notificationByBuyer2.size()).isEqualTo(0);

    }

    @DisplayName("상품 판매 완료 시 알림 생성")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void soldOutNotification() throws Exception {
        SoldOutRequest soldOutRequest = SoldOutRequest.builder().awardPrice(50000).buyerId(2L).build();
        //given
        itemService.soldOut(1L, soldOutRequest);
        Member seller = memberService.getMemberByEmail("user1@naver.com");
        Member buyer1 = memberService.getMemberByEmail("user2@naver.com");
        Member buyer2 = memberService.getMemberByEmail("user3@naver.com");

        // when
        List<Notification> notificationBySeller = notificationRepository.findByReceiverAndReadDateIsNull(seller);
        List<Notification> notificationByBuyer1 = notificationRepository.findByReceiverAndReadDateIsNull(buyer1);
        List<Notification> notificationByBuyer2 = notificationRepository.findByReceiverAndReadDateIsNull(buyer2);

        //then
        Assertions.assertThat(notificationBySeller.size()).isEqualTo(0);

        Assertions.assertThat(notificationByBuyer1.get(0).getItemId()).isEqualTo(1L);
        Assertions.assertThat(notificationByBuyer1.get(0).getMessage()).isEqualTo("입찰하신 상품에 낙찰되셨습니다.");
        Assertions.assertThat(notificationByBuyer1.get(0).getReadDate()).isNull();

        Assertions.assertThat(notificationByBuyer2.get(0).getItemId()).isEqualTo(1L);
        Assertions.assertThat(notificationByBuyer2.get(0).getMessage()).isEqualTo("입찰하신 상품에 낙찰받지 못하셨습니다.");
        Assertions.assertThat(notificationByBuyer2.get(0).getReadDate()).isNull();

    }

    @DisplayName("입찰 종료 시알림 생성")
    @Test
    void bidEndNotification() throws Exception {
        //given
        itemService.bidEnd(1L);
        Member seller = memberService.getMemberByEmail("user1@naver.com");
        Member buyer1 = memberService.getMemberByEmail("user2@naver.com");
        Member buyer2 = memberService.getMemberByEmail("user3@naver.com");

        // when
        List<Notification> notificationBySeller = notificationRepository.findByReceiverAndReadDateIsNull(seller);
        List<Notification> notificationByBuyer1 = notificationRepository.findByReceiverAndReadDateIsNull(buyer1);
        List<Notification> notificationByBuyer2 = notificationRepository.findByReceiverAndReadDateIsNull(buyer2);

        //then
        Assertions.assertThat(notificationBySeller.get(0).getItemId()).isEqualTo(1L);
        Assertions.assertThat(notificationBySeller.get(0).getMessage()).isEqualTo("입찰이 종료되었습니다. 거래를 진행해주세요");
        Assertions.assertThat(notificationBySeller.get(0).getReadDate()).isNull();

        Assertions.assertThat(notificationByBuyer1.get(0).getItemId()).isEqualTo(1L);
        Assertions.assertThat(notificationByBuyer1.get(0).getMessage()).isEqualTo("입찰이 종료되었습니다. 거래를 기다려주세요");
        Assertions.assertThat(notificationByBuyer1.get(0).getReadDate()).isNull();

        Assertions.assertThat(notificationByBuyer2.get(0).getItemId()).isEqualTo(1L);
        Assertions.assertThat(notificationByBuyer2.get(0).getMessage()).isEqualTo("입찰이 종료되었습니다. 거래를 기다려주세요");
        Assertions.assertThat(notificationByBuyer2.get(0).getReadDate()).isNull();

    }

    @DisplayName("나의 모든 알림 조회")
    @WithUserDetails(value = "user3@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getMyAllNotification() throws Exception {

        //given
        itemService.bidEnd(1L);


        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/notifications/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").toString();
        NotificationListResponse notificationListResponse = objectMapper.readValue(result, NotificationListResponse.class);


        //then
        Assertions.assertThat(notificationListResponse.getNotifications().size()).isEqualTo(1L);
    }

    @DisplayName("나의 모든 알림 읽음 처리")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void readMyAllNotification() throws Exception {
        //given
        MvcResult mvcResult = mockMvc.perform(patch("/api/notifications"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // when
        List<Notification> notifications = notificationRepository.findByReceiverAndReadDateIsNull(memberService.getMemberByEmail("user1@naver.com"));

        //then
        Assertions.assertThat(notifications.size()).isEqualTo(0L);
    }
}
