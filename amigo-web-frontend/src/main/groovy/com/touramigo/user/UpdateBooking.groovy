package com.touramigo.user

import org.hibernate.validator.constraints.NotBlank

class UpdateBooking {
    @NotBlank()
    String reason
}
