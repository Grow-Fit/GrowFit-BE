package com.project.growfit.admin.scheduler;

import com.project.growfit.admin.service.AdminService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountScheduler {

    private final AdminService adminService;

    @PostConstruct
    public void initAdminAccount() {
        //adminService.registerAdminCredentials();
    }
}
