package com.touramigo.user

import com.touramigo.booking.BookingUtils
import com.touramigo.email.EmailService
import com.touramigo.email.UserEmailRequest
import com.touramigo.security.SecurityUtils
import com.touramigo.security.UserInfoModel
import com.touramigo.security.UserProfile
import com.touramigo.security.UserProfileRepository
import com.touramigo.util.LocalDateEditor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

import java.time.LocalDate

@Controller
@RequestMapping(value = "/user")
class UserController {
    private static String VIEW_PROFILE = 'user/profile'
    
    private static String REDIRECT_PROFILE = 'redirect:profile'

    private static String ERROR_FORM = 'form'
    private static String ERROR_FORM_MESSAGE = 'Form contains errors. Please try again.'
    private static String ERROR_OLD_PASSWORD_NOT_MATCH = 'oldNotMatch'
    private static String ERROR_OLD_PASSWORD_NOT_MATCH_MESSAGE = 'Your old password is not correct.'
    private static String ERROR_PASSWORD_NOT_MATCH = 'notMatch'
    private static String ERROR_PASSWORD_NOT_MATCH_MESSAGE = 'Passwords do not match.'

    private static String SUCCESS_PROFILE_UPDATE = 'profileUpdate'
    private static String SUCCESS_PROFILE_UPDATE_MESSAGE = 'Your profile have been updated.'
    private static String SUCCESS_CHANGE_PASSWORD = 'changePassword'
    private static String SUCCESS_CHANGE_PASSWORD_MESSAGE = 'Your password have been updated.'

    private final UserProfileRepository userProfileRepository
    private final EmailService emailService
    private final BookingUtils bookingUtils

    private final PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256)

    @Autowired
    UserController(UserProfileRepository userProfileRepository, EmailService emailService, BookingUtils bookingUtils) {
        this.userProfileRepository = userProfileRepository
        this.emailService = emailService
        this.bookingUtils = bookingUtils
    }

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate, new LocalDateEditor())
    }

    @GetMapping('/profile')
    profile(@RequestParam('success') Optional<String> success, @RequestParam('error') Optional<String> error, Model model) {
        switch(success.orElse(null)) {
            case SUCCESS_PROFILE_UPDATE:
                model.addAttribute('success', SUCCESS_PROFILE_UPDATE_MESSAGE)
                break
            case SUCCESS_CHANGE_PASSWORD:
                model.addAttribute('success', SUCCESS_CHANGE_PASSWORD_MESSAGE)
                break
        }

        switch(error.orElse(null)) {
            case ERROR_FORM:
                model.addAttribute('error', ERROR_FORM_MESSAGE)
                break
            case ERROR_OLD_PASSWORD_NOT_MATCH:
                model.addAttribute('error', ERROR_OLD_PASSWORD_NOT_MATCH_MESSAGE)
                break
            case ERROR_PASSWORD_NOT_MATCH:
                model.addAttribute('error', ERROR_PASSWORD_NOT_MATCH_MESSAGE)
                break
        }

        UserInfoModel user = SecurityUtils.authenticatedUserInfo
        model.addAttribute('user', user)
        model.addAttribute('countries', bookingUtils.buildListOfCountries())
        model.addAttribute('titles', bookingUtils.buildListOfTitles())
        model.addAttribute('nationalities', bookingUtils.buildListOfNationalities())

        return VIEW_PROFILE
    }

    @PostMapping('/profile-edit')
    profileEdit(@Validated() @ModelAttribute('editUser') EditUser editUser, Errors errors) {
        if (errors.hasErrors()) {
            return REDIRECT_PROFILE + '?error=' + ERROR_FORM
        }

        UserProfile user = userProfileRepository.findByEmailAddress(SecurityUtils.authenticatedName)
        user.title = editUser.title
        user.firstName = editUser.firstName
        user.middleName = editUser.middleName
        user.surname = editUser.surname
        user.address = editUser.address
        user.country = editUser.country
        user.city = editUser.city
        user.state = editUser.state
        user.postCode = editUser.postCode
        user.dateOfBirth = editUser.dateOfBirth
        user.nationality = editUser.nationality
        user.mobilePhone = editUser.mobilePhone
        user.passportNumber = editUser.passportNumber
        user.issueDate = editUser.issueDate
        user.expiryDate = editUser.expiryDate
        user.placeOfIssue = editUser.placeOfIssue
        userProfileRepository.save(user)

        SecurityUtils.authenticatedUserProfile.title = editUser.title
        SecurityUtils.authenticatedUserProfile.firstName = editUser.firstName
        SecurityUtils.authenticatedUserProfile.middleName = editUser.middleName
        SecurityUtils.authenticatedUserProfile.surname = editUser.surname
        SecurityUtils.authenticatedUserProfile.address = editUser.address
        SecurityUtils.authenticatedUserProfile.country = editUser.country
        SecurityUtils.authenticatedUserProfile.city = editUser.city
        SecurityUtils.authenticatedUserProfile.state = editUser.state
        SecurityUtils.authenticatedUserProfile.postCode = editUser.postCode
        SecurityUtils.authenticatedUserProfile.dateOfBirth = editUser.dateOfBirth
        SecurityUtils.authenticatedUserProfile.nationality = editUser.nationality
        SecurityUtils.authenticatedUserProfile.mobilePhone = editUser.mobilePhone
        SecurityUtils.authenticatedUserProfile.passportNumber = editUser.passportNumber
        SecurityUtils.authenticatedUserProfile.issueDate = editUser.issueDate
        SecurityUtils.authenticatedUserProfile.expiryDate = editUser.expiryDate
        SecurityUtils.authenticatedUserProfile.placeOfIssue = editUser.placeOfIssue

        return REDIRECT_PROFILE + '?success=' + SUCCESS_PROFILE_UPDATE
    }

    @PostMapping('/profile-password')
    profilePassword(@Validated() @ModelAttribute('editPassword') EditPassword editPassword, Errors errors) {
        if (errors.hasErrors()) {
            return REDIRECT_PROFILE + '?error=' + ERROR_FORM
        }

        if (editPassword.password != editPassword.confirmPassword) {
            return REDIRECT_PROFILE + '?error=' + ERROR_OLD_PASSWORD_NOT_MATCH
        }

        UserProfile user = userProfileRepository.findByEmailAddress(SecurityUtils.authenticatedName)

        def oldPassword = passwordEncoder.encodePassword(editPassword.oldPassword, user.salt)
        if (oldPassword != user.password) {
            return REDIRECT_PROFILE + '?error=' + ERROR_PASSWORD_NOT_MATCH
        }

        user.password = passwordEncoder.encodePassword(editPassword.password, user.salt)
        userProfileRepository.save(user)

        emailService.sendUserChangePasswordEmail(new UserEmailRequest(sendTo: user.emailAddress, name: user.firstName))

        return REDIRECT_PROFILE + '?success=' + SUCCESS_CHANGE_PASSWORD
    }
}
