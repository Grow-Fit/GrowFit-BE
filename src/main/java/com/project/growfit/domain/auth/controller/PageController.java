package com.project.growfit.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test-page")
public class PageController {

    @GetMapping("home")
    public String testPage() {
        return "jwt_auth_test";
    }
}