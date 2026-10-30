package com.example.student_management.service;

import com.example.student_management.dao.AppUserRepository;
import com.example.student_management.dto.AuthResponse;
import com.example.student_management.dto.CreateUserRequest;
import com.example.student_management.dto.UserResponse;
import com.example.student_management.entity.AppRole;
import com.example.student_management.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long ttlMinutes;

    public AuthService(AuthenticationManager authenticationManager, AppUserRepository users,
            PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,
            @Value("${app.security.jwt-issuer}") String issuer,
            @Value("${app.security.jwt-ttl-minutes}") long ttlMinutes) {
        this.authenticationManager = authenticationManager;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    public AuthResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password));
        AppUser user = users.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlMinutes * 60);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getUsername())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new AuthResponse(token, "Bearer", ttlMinutes * 60, user.getUsername(), user.getRole());
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (request.role() == AppRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ được tạo tài khoản TEACHER hoặc STUDENT qua chức năng này");
        }
        if (users.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username đã tồn tại");
        }
        try {
            AppUser user = users.save(new AppUser(request.username(),
                    passwordEncoder.encode(request.password()), request.role()));
            return toResponse(user);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username đã tồn tại", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return users.findAll().stream().map(AuthService::toResponse).toList();
    }

    @Transactional
    public UserResponse updateRole(Long id, AppRole role) {
        AppUser user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));
        if (user.isEnabled() && user.getRole() == AppRole.ADMIN && role != AppRole.ADMIN
                && countEnabledAdmins() <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể hạ quyền ADMIN cuối cùng");
        }
        user.setRole(role);
        return toResponse(user);
    }

    @Transactional
    public void disableUser(Long id, String currentUsername) {
        AppUser user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));
        if (Objects.equals(user.getUsername(), currentUsername)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể tự vô hiệu hóa tài khoản đang đăng nhập");
        }
        if (user.isEnabled() && user.getRole() == AppRole.ADMIN && countEnabledAdmins() <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể vô hiệu hóa ADMIN cuối cùng");
        }
        user.setEnabled(false);
    }

    private long countEnabledAdmins() {
        return users.findAll().stream()
                .filter(AppUser::isEnabled)
                .filter(user -> user.getRole() == AppRole.ADMIN)
                .count();
    }

    private static UserResponse toResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.isEnabled());
    }
}
