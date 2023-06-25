package com.touramigo.user

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotBlank

class RegisterUser extends EditUser {
    @NotBlank()
    @Email
    String email
    @NotBlank()
    String password
    String referredBy
}
