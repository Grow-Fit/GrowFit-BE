package com.project.growfit;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticController {


    @GetMapping("/admin")
    public String adminPage() {
        return "index";
    }
}
