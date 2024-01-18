package com.api.farmingsoon.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberPageController {
    @GetMapping("/home")
    public String login(){
        return "login";
    }

}
