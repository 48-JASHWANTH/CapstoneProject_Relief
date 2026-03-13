package org.hartford.relief.service.impl.userServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.RegisterRequest;
import org.hartford.relief.dto.response.UserResponse;
import org.hartford.relief.entity.Role;
import org.hartford.relief.entity.User;
import org.hartford.relief.exception.ConfigurationException;
import org.hartford.relief.exception.DuplicateResourceException;
import org.hartford.relief.repository.RoleRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.userService.RegisterService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Assign default role CUSTOMER
        Role userRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new ConfigurationException("RoleRepository", "Default role 'CUSTOMER' not found in the database."));

        // Build and save user with encoded password
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .role(userRole)
                .build();

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .role(saved.getRole() != null ? saved.getRole().getName() : null)
                .build();
    }
}
