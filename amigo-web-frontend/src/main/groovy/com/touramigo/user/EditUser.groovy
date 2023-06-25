package com.touramigo.user

import org.hibernate.validator.constraints.NotBlank
import org.hibernate.validator.constraints.SafeHtml

import java.time.LocalDate

class EditUser {
    @SafeHtml()
    String title
    @NotBlank()
    @SafeHtml()
    String firstName
    @SafeHtml()
    String middleName
    @SafeHtml()
    String surname
    @SafeHtml()
    String address
    @SafeHtml()
    String country;
    @SafeHtml()
    String city
    @SafeHtml()
    String state
    @SafeHtml()
    String postCode
    @SafeHtml()
    String mobilePhone
    LocalDate dateOfBirth
    @SafeHtml()
    String nationality
    @SafeHtml()
    String passportNumber
    LocalDate issueDate
    LocalDate expiryDate
    @SafeHtml()
    String placeOfIssue
}
