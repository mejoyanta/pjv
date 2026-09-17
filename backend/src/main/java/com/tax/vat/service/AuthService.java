package com.tax.vat.service;

import com.tax.vat.dto.request.LoginRequest;
import com.tax.vat.dto.response.LoginResponse;
import com.tax.vat.entity.User;
import com.tax.vat.exception.BadRequestException;
import com.tax.vat.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final GroupService groupService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, GroupService groupService) {
        this.userRepository = userRepository;
        this.groupService = groupService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String login = request.getUsername().trim().toLowerCase();
        String password = request.getPassword();

        // 1. Find user by username or email
        User user = userRepository.findByUsername(login)
                .or(() -> userRepository.findByEmail(login))
                .orElse(null);

        if (user == null) {
            // Case-insensitive fallback query
            user = userRepository.findAll().stream()
                    .filter(u -> (u.getUsername() != null && u.getUsername().equalsIgnoreCase(login)) ||
                                 (u.getEmail() != null && u.getEmail().equalsIgnoreCase(login)))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Invalid credentials. User not found."));
        }

        // 2. Validate Password (matches Laravel's Hash::check)
        if (!isPasswordValid(password, user.getPassword())) {
            throw new BadRequestException("Invalid username or password.");
        }

        // 3. Mark user online
        user.setIsOnline(true);
        userRepository.save(user);

        // 4. Load group permissions
        List<String> permissions = Collections.emptyList();
        String groupName = "User";
        if (user.getGroup() != null) {
            groupName = user.getGroup().getName();
            permissions = groupService.getGroupPermissions(user.getGroup().getSlug());
        }

        String companyName = user.getCompany() != null ? user.getCompany().getName() : null;
        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;

        String token = UUID.randomUUID().toString();

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                companyId,
                companyName,
                groupName,
                user.getIsAdmin(),
                permissions
        );
    }

    @Transactional
    public void logout(Long userId) {
        if (userId == null) return;
        userRepository.findById(userId).ifPresent(u -> {
            u.setIsOnline(false);
            u.setOfflineAt(LocalDateTime.now());
            userRepository.save(u);
        });
    }

    private boolean isPasswordValid(String rawPassword, String storedHash) {
        if (storedHash == null || rawPassword == null) return false;

        // PHP BCrypt ($2y$) to Java BCrypt ($2a$) normalization
        String normalizedHash = storedHash;
        if (storedHash.startsWith("$2y$")) {
            normalizedHash = "$2a$" + storedHash.substring(4);
        }

        try {
            if (passwordEncoder.matches(rawPassword, normalizedHash)) {
                return true;
            }
        } catch (Exception ignored) {
        }

        // Plaintext fallback
        return rawPassword.equals(storedHash);
    }
}
