package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRoleRequest {

    private Long userId;
    private String roleName;
}
