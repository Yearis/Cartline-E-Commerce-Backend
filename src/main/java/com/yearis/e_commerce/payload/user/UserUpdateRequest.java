package com.yearis.e_commerce.payload.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateRequest {

    @Size(min = 3, max = 30, message = "first name must be between 3 and 30")
    private String firstName;

    @Size(min = 3, max = 30, message = "last name must be between 3 and 30")
    private String lastName;

    // no email here as it will be handled separately
}
