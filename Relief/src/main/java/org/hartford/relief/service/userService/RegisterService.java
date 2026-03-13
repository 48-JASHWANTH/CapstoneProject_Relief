package org.hartford.relief.service.userService;

import org.hartford.relief.dto.request.RegisterRequest;
import org.hartford.relief.dto.response.UserResponse;

public interface RegisterService {
    UserResponse register(RegisterRequest request);
}
