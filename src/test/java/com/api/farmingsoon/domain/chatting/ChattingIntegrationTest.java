package com.api.farmingsoon.domain.chatting;

import com.api.farmingsoon.common.clean.DatabaseCleanup;
import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.domain.bid.dto.BidRequest;
import com.api.farmingsoon.domain.bid.service.BidService;
import com.api.farmingsoon.domain.chat.dto.ChatMessageRequest;
import com.api.farmingsoon.domain.chat.service.ChatService;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomCreateRequest;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomResponse;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomService;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.service.MemberService;
import com.api.farmingsoon.util.TestImageUtils;
import com.api.farmingsoon.util.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ChattingIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Transaction transaction;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private static MockMultipartFile profileImage;

    @BeforeAll
    static void beforeAll() throws IOException {
        profileImage = TestImageUtils.generateMockImageFile("profileImage");
    }

    @BeforeEach
    void beforeEach(){
        databaseCleanup.execute();
        for(int i = 1; i <= 2; i++){
            JoinRequest joinRequest = JoinRequest.builder()
                    .email("user" + i + "@naver.com")
                    .nickname("user1")
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
                .itemStatus(ItemStatus.BIDDING)
                .viewCount(0)
                .expiredAt(TimeUtils.setExpireAt(3)).build();

        List<String> imageUrl = new ArrayList<>(Arrays.asList("/subFile1", "/subFile2" , "/subFile3"));
        imageUrl.add(0, "/thumnailImage");

        itemService.saveItemAndImage(item, imageUrl);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @DisplayName("채팅방 생성(구매자 입장)")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void createChatRoomByBuyer() throws Exception {
        //given
        ChatRoomCreateRequest chatRoomCreateRequest = ChatRoomCreateRequest.of("user2@naver.com", 1L);

        // when

        MvcResult mvcResult = mockMvc.perform(post("/api/chat-rooms")
                        .content(objectMapper.writeValueAsString(chatRoomCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        Long chatRoomId = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").asLong();
        
        transaction.invoke(() ->
            {
            ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);

            Assertions.assertThat(chatRoom.getSeller().getEmail()).isEqualTo("user1@naver.com");
            Assertions.assertThat(chatRoom.getBuyer().getEmail()).isEqualTo("user2@naver.com");
            }
        );

    }
    @DisplayName("채팅방 생성(판매자 입장)")
    @WithUserDetails(value = "user2@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void createChatRoomBySeller() throws Exception {
        //given
        ChatRoomCreateRequest chatRoomCreateRequest = ChatRoomCreateRequest.of("user2@naver.com", 1L);

        // when

        MvcResult mvcResult = mockMvc.perform(post("/api/chat-rooms")
                        .content(objectMapper.writeValueAsString(chatRoomCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        Long chatRoomId = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").asLong();

        transaction.invoke(() ->
                {
                    ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);

                    Assertions.assertThat(chatRoom.getSeller().getEmail()).isEqualTo("user1@naver.com");
                    Assertions.assertThat(chatRoom.getBuyer().getEmail()).isEqualTo("user2@naver.com");
                }
        );

    }

    @DisplayName("채팅방 조회(판매자 입장)")
    @WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getChatRoomBySeller() throws Exception {
        //given
        ChatRoomCreateRequest chatRoomCreateRequest = ChatRoomCreateRequest.of("user2@naver.com", 1L);
        Long chatRoomId = chatRoomService.handleChatRoom(chatRoomCreateRequest);
        chatService.create(ChatMessageRequest.builder().chatRoomId(chatRoomId).message("chat1").build());
        chatService.create(ChatMessageRequest.builder().chatRoomId(chatRoomId).message("chat2").build());
        // when

        MvcResult mvcResult = mockMvc.perform(get("/api/chat-rooms/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then

        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").get(0).toString();
        ChatRoomResponse chatRoomResponse = objectMapper.readValue(result, ChatRoomResponse.class);



        Assertions.assertThat(chatRoomResponse.getToUserName()).isEqualTo("user2@naver.com");
        Assertions.assertThat(chatRoomResponse.getLastMessage()).isEqualTo("chat2");


    }

    @DisplayName("채팅방 조회(구매자 입장)")
    @WithUserDetails(value = "user2@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void getChatRoomByBuyer() throws Exception {
        //given
        ChatRoomCreateRequest chatRoomCreateRequest = ChatRoomCreateRequest.of("user2@naver.com", 1L);
        Long chatRoomId = chatRoomService.handleChatRoom(chatRoomCreateRequest);
        chatService.create(ChatMessageRequest.builder().chatRoomId(chatRoomId).message("chat1").build());
        chatService.create(ChatMessageRequest.builder().chatRoomId(chatRoomId).message("chat2").build());
        chatService.create(ChatMessageRequest.builder().chatRoomId(chatRoomId).message("chat3").build());
        // when

        MvcResult mvcResult = mockMvc.perform(get("/api/chat-rooms/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String result = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("result").get(0).toString();
        ChatRoomResponse chatRoomResponse = objectMapper.readValue(result, ChatRoomResponse.class);



        Assertions.assertThat(chatRoomResponse.getToUserName()).isEqualTo("user1@naver.com");
        Assertions.assertThat(chatRoomResponse.getLastMessage()).isEqualTo("chat3");


    }
}
