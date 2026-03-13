package org.hartford.relief.service.userService;

import org.hartford.relief.dto.request.JwtRequest;
import org.hartford.relief.dto.response.JwtResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserLoginService extends UserDetailsService {
    JwtResponse login(JwtRequest request);
}
