package com.api.readinglog.domain.member.controller;

import com.api.readinglog.common.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    // TODO: API 응답 예제 - 보고 나서 지워주시면 됩니다!!
    @GetMapping("/api-test1")
    public Response<Void> testApi01() {
        return Response.success(HttpStatus.OK, "응답 데이터가 없는 API 통신!");
    }

    @GetMapping("/api-test2")
    public Response<List<String>> testApi02() {
        List<String> members = new ArrayList<>();
        members.add("member1");
        members.add("member1");
        members.add("member1");

        return Response.success(HttpStatus.OK, "회원 목록 조회 성공!", members);
    }
}
