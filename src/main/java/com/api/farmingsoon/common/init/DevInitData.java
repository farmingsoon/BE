package com.api.farmingsoon.common.init;


import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.repository.ItemRepository;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.model.MemberRole;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevInitData {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            Member member = memberRepository.save(createMember("test123@email.com", "test123", passwordEncoder.encode("test1234")));

            itemRepository.save(createItem("상품1", "상품1 내용", member));
            itemRepository.save(createItem("상품2", "상품2 내용", member));
        };
    }

    private Member createMember(String email, String nickname, String password) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .role(MemberRole.MEMBER)
                .build();
    }

    private Item createItem(String title, String description, Member member) {
        return Item.builder()
                .title(title)
                .description(description)
                .member(member)
                .build();
    }
}
