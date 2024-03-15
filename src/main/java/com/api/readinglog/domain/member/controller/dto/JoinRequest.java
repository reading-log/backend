package com.api.readinglog.domain.member.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class JoinRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email( message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;
    private MultipartFile profileImage;
}
