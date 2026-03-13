package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyApprovalRequest {

    private String status;   // APPROVED / REJECTED
    private String remarks;
}
