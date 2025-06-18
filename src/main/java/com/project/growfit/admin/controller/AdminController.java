package com.project.growfit.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @GetMapping("/login")
    public String loginPage() {
        log.info("redirect: pages/login");
        return "pages/login";
    }

    @GetMapping("main")
    public String adminMainPage() {
        log.info("redirect: pages/dashboard");
        return "pages/dashboard";
    }
}
