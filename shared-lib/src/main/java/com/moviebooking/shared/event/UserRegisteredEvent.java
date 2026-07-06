package com.moviebooking.shared.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisteredEvent {
    private UUID authUserId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
}
