package com.api.readinglog.domain.member.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdatePasswordRequest {
    private String currentPassword;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,20}$", message = "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    @Schema(description = "새로운 비밀번호")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    @Schema(description = "새로운 비밀번호 확인")
    private String newPasswordConfirm;
}
