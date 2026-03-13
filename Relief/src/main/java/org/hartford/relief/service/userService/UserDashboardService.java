package org.hartford.relief.service.userService;

import org.hartford.relief.dto.response.UserDashboardResponse;

public interface UserDashboardService {

    UserDashboardResponse getMyDashboard(Long userId);
}
