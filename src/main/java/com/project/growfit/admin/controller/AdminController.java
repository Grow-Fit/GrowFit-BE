package com.project.growfit.admin.controller;

import com.project.growfit.admin.service.AdminUserService;
import com.project.growfit.domain.User.entity.Parent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService adminUserService;

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

    @GetMapping("/users")
    public String userListPage(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        Page<Parent> userPage;

        if (email != null && !email.isEmpty()) {
            userPage = adminUserService.findMembersByEmailContaining(email, pageable);
        } else {
            userPage = adminUserService.findAllMembers(pageable);
        }
        model.addAttribute("email", email);
        model.addAttribute("userPage", userPage);
        return "pages/users";
    }
}
