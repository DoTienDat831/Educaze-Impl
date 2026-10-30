package com.example.student_management.config;

import com.example.student_management.dao.AppUserRepository;
import com.example.student_management.entity.AppRole;
import com.example.student_management.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminBootstrap(AppUserRepository users, PasswordEncoder passwordEncoder,
            @Value("${app.security.bootstrap-admin-username}") String username,
            @Value("${app.security.bootstrap-admin-password}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (username.isBlank() && password.isBlank()) return;
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Set both ADMIN_USERNAME and ADMIN_PASSWORD to bootstrap an administrator");
        }
        if (password.length() < 12) {
            throw new IllegalStateException("ADMIN_PASSWORD must contain at least 12 characters");
        }
        if (!users.existsByUsername(username)) {
            users.save(new AppUser(username, passwordEncoder.encode(password), AppRole.ADMIN));
        }
    }
}
