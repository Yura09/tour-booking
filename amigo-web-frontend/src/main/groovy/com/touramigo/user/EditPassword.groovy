package com.touramigo.user

import org.hibernate.validator.constraints.NotBlank

class EditPassword {
    @NotBlank()
    String oldPassword
    @NotBlank()
    String password
    @NotBlank()
    String confirmPassword
}
