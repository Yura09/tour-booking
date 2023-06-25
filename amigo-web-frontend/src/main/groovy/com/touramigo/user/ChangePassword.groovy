package com.touramigo.user

import org.hibernate.validator.constraints.NotBlank

class ChangePassword {
    @NotBlank
    String passwordToken
    @NotBlank()
    String password
    @NotBlank()
    String confirmPassword
}
