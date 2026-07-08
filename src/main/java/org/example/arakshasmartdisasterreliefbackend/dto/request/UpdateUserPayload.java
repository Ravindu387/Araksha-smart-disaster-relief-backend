package org.example.arakshasmartdisasterreliefbackend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserPayload {
    private String name;
    private String email;
    private String department;
}
