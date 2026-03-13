package org.hartford.relief.service.impl.userServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.JwtRequest;
import org.hartford.relief.dto.response.JwtResponse;
import org.hartford.relief.entity.User;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.userService.UserLoginService;
import org.hartford.relief.util.JwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private AuthenticationManager authManager;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + (user.getRole() != null ? user.getRole().getName() : "CUSTOMER"))
                )
        );
    }

    @Override
    public JwtResponse login(JwtRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmailWithRoles(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole() != null ? user.getRole().getName() : "CUSTOMER";
        String token = jwtUtil.generateToken(request.getEmail(), role, user.getId());

        return new JwtResponse(token);
    }
}
