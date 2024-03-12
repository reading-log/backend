package com.api.readinglog.domain.member.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class JoinRequest {

    private String email;
    private String nickName;
    private String password;
    private String passwordConfirm;
    private MultipartFile profileImage;
}
