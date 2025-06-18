package com.project.growfit.admin.service;

import com.project.growfit.admin.domain.Admin;
import com.project.growfit.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Value("${admin.login-id}")
    private String adminId;

    @Value("${admin.login-password}")
    private String adminPassword;

    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    @Override
    public void registerAdminCredentials() {
        Admin admin = Admin.creatAdmin(adminId, passwordEncoder.encode(adminPassword));
        adminRepository.save(admin);
    }
}
