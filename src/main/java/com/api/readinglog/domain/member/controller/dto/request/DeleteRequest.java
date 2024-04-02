package com.api.readinglog.domain.member.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRequest {

    @Schema(description = "회원 탈퇴에 필요한 비밀번호")
    private String password;
}
