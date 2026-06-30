package com.moviebooking.auth_service.dto;

import com.moviebooking.auth_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    private String password;

    private Role role;


}
