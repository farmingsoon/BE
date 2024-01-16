package com.api.farmingsoon.domain.member.controller;

import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.common.response.ResponseMessage;
import com.api.farmingsoon.common.response.StatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @GetMapping("/test")
    public Response<List<String>> test() {
        List<String> testResult = List.of("test1", "test2", "test3");
        return Response.success(StatusCode.OK, ResponseMessage.JOIN_MEMBER, testResult);
    }

    @GetMapping("/void-test")
    public Response<Void> voidTest() {
        return Response.success(StatusCode.OK, ResponseMessage.JOIN_MEMBER);
    }
}
