package com.touramigo.user

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotBlank

class ForgotPassword {
    @NotBlank()
    @Email
    String username
}
