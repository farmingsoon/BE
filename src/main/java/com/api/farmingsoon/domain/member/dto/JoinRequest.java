package com.api.farmingsoon.domain.member.dto;

import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.model.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "이메일은 필수 입력값 입니다.")
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값 입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8글자, 최대 20글자로 작성해야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9@$!%*?&]*$", message = "비밀번호는 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;


    @NotBlank(message = "닉네임은 필수 입력값 입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 최소 2글자, 최대 20글자로 작성해야 합니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]*$", message = "닉네임은 영문, 한글, 숫자를 사용해서 작성해주세요.")
    private String nickname;

    private MultipartFile profileImg;

    @Builder
    private JoinRequest(String email, String password, String nickname, MultipartFile profileImg) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

    public Member toEntity(){
        return Member.builder()
                .nickname(this.nickname)
                .email(this.getEmail())
                .role(MemberRole.MEMBER)
                .build();
    }
}
