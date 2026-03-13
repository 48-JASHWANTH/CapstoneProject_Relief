package org.hartford.relief.dto.request;

import lombok.Data;

@Data
public class JwtRequest {
    private String email;
    private String password;
}