package org.hartford.relief.service.adminService;

import org.hartford.relief.dto.request.AgentRequest;
import org.hartford.relief.dto.response.AgentResponse;

import java.util.List;

public interface AdminAgentService {

    AgentResponse createAgent(AgentRequest request);

    List<AgentResponse> getAllAgents();

    AgentResponse getAgentById(Long id);

    AgentResponse updateAgent(Long id, AgentRequest request);

    void deleteAgent(Long id);

    List<AgentResponse> getAgentsByRegion(String region);
}
