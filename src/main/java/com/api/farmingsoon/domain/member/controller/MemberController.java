package com.api.farmingsoon.domain.member.controller;

import com.api.farmingsoon.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @GetMapping("/test")
    public Response<List<String>> test() {
        List<String> response = List.of("test1", "test2", "test3");
        return Response.success(HttpStatus.OK, "테스트 성공!", response);
    }

    @GetMapping("/void-test")
    public Response<Void> voidTest() {
        return Response.success(HttpStatus.OK, "void 테스트 성공!");
    }
}
