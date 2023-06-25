package com.touramigo.user

import com.touramigo.email.EmailService
import com.touramigo.email.ForgotPasswordEmailRequest
import com.touramigo.email.UserEmailRequest
import com.touramigo.partner.ResetPasswordModel
import com.touramigo.partner.ResetPasswordRequestModel
import com.touramigo.referral.ReferralController
import com.touramigo.referral.ReferralEmailRequest
import com.touramigo.partner.GaiaFeignClient
import com.touramigo.security.SecurityUtils
import com.touramigo.security.UserProfile
import com.touramigo.security.UserProfileRepository
import com.touramigo.security.WebTokenService
import feign.FeignException
import groovy.transform.CompileStatic
import io.jsonwebtoken.Claims
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping(value = "/")
@CompileStatic
class AccessController {
    private static String VIEW_LOGIN = 'user/login'
    private static String VIEW_SIGNUP = 'user/signup'
    private static String VIEW_FORGOT_PASSWORD = 'user/forgotPassword'
    private static String VIEW_CHANGE_PASSWORD = 'user/changePassword'

    private static String REDIRECT_LOGIN = 'redirect:/login'
    private static String REDIRECT_SEARCH = 'redirect:/search-tours'

    private static String ERROR_ALREADY_LOGIN = 'alreadyLogin'
    private static String ERROR_ALREADY_LOGIN_MESSAGE = 'User is already signed in. Please logout and sign in again'
    private static String ERROR_FORM = 'form'
    private static String ERROR_FORM_MESSAGE = 'Form contains errors. Please try again.'
    private static String ERROR_USER_EXISTS = 'userExists'
    private static String ERROR_USER_EXISTS_MESSAGE = 'User with same email already exists.'
    private static String ERROR_WRONG_LOGIN_OR_PASSWORD_MESSAGE = 'The email/password combination used was not found on the system.'
    private static String ERROR_USER_DO_NOT_EXISTS_MESSAGE = 'A user with that email login doesn\'t exist.'
    private static String ERROR_EMAIL_SERVICE_FAIL = 'Failed to send email. Please try again later.'
    private static String ERROR_WRONG_TOKEN_MESSAGE = 'Provided token is not correct.'
    private static String ERROR_PASSWORD_NOT_MATCH_MESSAGE = 'Passwords do not match.'

    private static String SUCCESS_FORGOT_PASSWORD = 'forgotPassword'
    private static String SUCCESS_CHANGE_PASSWORD = 'changePassword'
    private static String SUCCESS_FORGOT_PASSWORD_MESSAGE = 'Password reset successfully. Please click on the verification link sent to you at your registered email address.'
    private static String SUCCESS_CHANGE_PASSWORD_MESSAGE = 'Your password have been updated.'

    private final UserProfileRepository userProfileRepository
    private final EmailService emailService
    private final WebTokenService webTokenService
    private final RestTemplateBuilder restTemplateBuilder
    private final AuthenticationManager authManager
    private final GaiaFeignClient apiAuthClient;

    PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256)

    @Autowired
    AccessController(UserProfileRepository userProfileRepository, EmailService emailService, WebTokenService webTokenService, RestTemplateBuilder restTemplateBuilder, AuthenticationManager authenticationManager, GaiaFeignClient apiAuthClient) {
        this.userProfileRepository = userProfileRepository
        this.emailService = emailService
        this.webTokenService = webTokenService
        this.restTemplateBuilder = restTemplateBuilder
        this.authManager = authenticationManager
        this.apiAuthClient = apiAuthClient
    }

    @GetMapping('/login')
    String login(@RequestParam('error') Optional<Boolean> error,
                 @RequestParam('success') Optional<String> success,
                 @RequestParam('redirectTo') Optional<String> redirectTo,
                 Model model) {
        if (SecurityUtils.authenticated) {
            return redirectTo.map({ it -> 'redirect:' + redirectTo.get() }).orElse(REDIRECT_SEARCH)
        }

        model.addAttribute('error', error.orElse(false) ? ERROR_WRONG_LOGIN_OR_PASSWORD_MESSAGE : null)
        String successMessage = null
        if (success.orElse(null) == SUCCESS_FORGOT_PASSWORD) {
            successMessage = SUCCESS_FORGOT_PASSWORD_MESSAGE
        } else if (success.orElse(null) == SUCCESS_CHANGE_PASSWORD) {
            successMessage = SUCCESS_CHANGE_PASSWORD_MESSAGE
        }
        model.addAttribute('success', successMessage)
        return VIEW_LOGIN
    }

    @GetMapping('/external-login')
    String loginExternal(
            @RequestParam('token') Optional<String> token,
            @RequestParam('redirectTo') Optional<String> redirectTo,
            Model model) {

        if (token.isPresent()) {
            try {
                UserDetails userDetails = apiAuthClient.getLoggedUserInfo("Bearer " + token.get())
                UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userDetails, null)
                Authentication auth = authManager.authenticate(authReq)
                SecurityContextHolder.getContext().setAuthentication(auth)
                return redirectTo.map({ it -> 'redirect:' + redirectTo.get() }).orElse(REDIRECT_SEARCH)
            } catch (Exception e) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation")
            }
        } else {
            return VIEW_LOGIN
        }
    }

    @GetMapping('/signup')
    String signUp(Model model) {
        if (SecurityUtils.authenticated) {
            return REDIRECT_SEARCH
        }

        model.addAttribute('registerUser', new RegisterUser())
        return VIEW_SIGNUP
    }

    private boolean verifyRecaptcha(String ip,
                                    String recaptchaResponse) {
        Map<String, String> body = new HashMap<>();
        body.put('secret', '6LdZtKgUAAAAAP4ZN5f5WT-rujBeuC8yDUlr1QdA');
        body.put('response', recaptchaResponse);
        body.put('remoteip', ip);
        ResponseEntity<Map> recaptchaResponseEntity =
                restTemplateBuilder.build()
                        .postForEntity("https://www.google.com/recaptcha/api/siteverify" +
                                "?secret={secret}&response={response}&remoteip={remoteip}",
                                body, Map.class, body);
        Map<String, Object> responseBody =
                recaptchaResponseEntity.getBody();

        return (Boolean) responseBody.get("success");
    }

    private void signUpUser(RegisterUser registerUser, HttpServletRequest request) {
        UserProfile newUserProfile = new UserProfile(
                emailAddress: registerUser.email,
                referredBy: registerUser.referredBy,
                title: registerUser.title,
                firstName: registerUser.firstName,
                middleName: registerUser.middleName,
                surname: registerUser.surname,
                address: registerUser.address,
                country: registerUser.country,
                city: registerUser.city,
                state: registerUser.state,
                postCode: registerUser.postCode,
                dateOfBirth: registerUser.dateOfBirth,
                nationality: registerUser.nationality,
                mobilePhone: registerUser.mobilePhone
        )

        String recaptchaResponse = request.getParameter("g-recaptcha-response")

        if (verifyRecaptcha(request.getRemoteAddr(), recaptchaResponse)) {

            newUserProfile.password = passwordEncoder.encodePassword(registerUser.password, newUserProfile.salt)
            newUserProfile.roles = UserRoleConstants.Customer

            userProfileRepository.save(newUserProfile)

            request.login(registerUser.email, registerUser.password)

            if (SecurityUtils.authenticatedUserProfile != null) {
                SecurityUtils.authenticatedUserProfile.referredBy = registerUser.referredBy

                if (registerUser.referredBy != null) {
                    def referredByUser = userProfileRepository.findByReferralCode(registerUser.referredBy)
                    if (referredByUser != null) {
                        emailService.sendReferralRegistrationEmail(new ReferralEmailRequest(sendTo: referredByUser.emailAddress, name: referredByUser.firstName, referralName: registerUser.firstName, referralAccountUrl: ReferralController.USER_REFERRAL_ACCOUNT))
                    }
                }
            }

            emailService.sendUserSignUpEmail(new UserEmailRequest(sendTo: registerUser.email, name: registerUser.firstName))
        }
    }

    @PostMapping('/signup')
    String signUpProcess(@Validated() @ModelAttribute('registerUser') RegisterUser registerUser, Errors errors, Model model, HttpServletRequest request) {
        if (SecurityUtils.authenticated) {
            return REDIRECT_SEARCH
        }

        if (errors.hasErrors()) {
            model.addAttribute('error', ERROR_FORM_MESSAGE)
            registerUser.password == ''
            model.addAttribute('registerUser', registerUser)
            return VIEW_SIGNUP
        }

        def alreadyExistUser = userProfileRepository.findByEmailAddress(registerUser.email)
        if (alreadyExistUser != null) {
            model.addAttribute('error', ERROR_USER_EXISTS_MESSAGE)
            registerUser.password == ''
            model.addAttribute('registerUser', registerUser)
            return VIEW_SIGNUP
        }

        signUpUser(registerUser, request)

        return REDIRECT_SEARCH
    }

    @ResponseBody
    @RequestMapping(value = '/signupAjax', method = RequestMethod.POST, produces = 'application/json')
    RegisterUserResult signUpAjaxProcess(@Validated() @ModelAttribute('registerUser') RegisterUser registerUser, Errors errors, HttpServletRequest request) {
        if (SecurityUtils.authenticated) {
            return new RegisterUserResult(success: false, errorCode: ERROR_ALREADY_LOGIN, errorMessage: ERROR_ALREADY_LOGIN_MESSAGE)
        }

        if (errors.hasErrors()) {
            return new RegisterUserResult(success: false, errorCode: ERROR_FORM, errorMessage: ERROR_FORM_MESSAGE)
        }

        def alreadyExistUser = userProfileRepository.findByEmailAddress(registerUser.email)
        if (alreadyExistUser != null) {
            return new RegisterUserResult(success: false, errorCode: ERROR_USER_EXISTS, errorMessage: ERROR_USER_EXISTS)
        }

        signUpUser(registerUser, request)

        return new RegisterUserResult(success: true)
    }

    @ResponseBody
    @RequestMapping(value = '/emailVerify', method = RequestMethod.GET, produces = 'application/json')
    EmailVerification verifyEmail(@RequestParam('email') String email) {
        if (email != null && !email.empty) {
            def alreadyExistUser = userProfileRepository.findByEmailAddress(email)
            if (alreadyExistUser == null) {
                return new EmailVerification(isUnique: true)
            }
        }
        return new EmailVerification(isUnique: false)
    }

    @GetMapping('/forgot-password')
    String forgotPassword() {
        return VIEW_FORGOT_PASSWORD
    }

    @PostMapping('/forgot-password')
    String forgotPasswordProcess(@Validated() @ModelAttribute('username') ForgotPassword forgotPassword, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute('error', ERROR_FORM_MESSAGE)
            return VIEW_FORGOT_PASSWORD
        }
        def emailResponse
        try {
            emailResponse = apiAuthClient.resetPasswordRequest(new ResetPasswordRequestModel(forgotPassword.username))
        } catch (FeignException feignException) {
            model.addAttribute('error', getErrorMessage(feignException))
            return VIEW_FORGOT_PASSWORD
        }
        if (!emailResponse.success) {
            model.addAttribute('error', ERROR_EMAIL_SERVICE_FAIL)
            return VIEW_FORGOT_PASSWORD
        }

        return REDIRECT_LOGIN + '?success=' + SUCCESS_FORGOT_PASSWORD
    }

    @GetMapping('/change-password')
    String changePassword(@RequestParam('token') String token, Model model) {

        if (!apiAuthClient.isTokenExist(token)) {
            model.addAttribute('error', ERROR_WRONG_TOKEN_MESSAGE)
        }
        model.addAttribute('token', token)
        return VIEW_CHANGE_PASSWORD
    }

    @PostMapping('/change-password')
    String changePasswordProcess(@Validated() @ModelAttribute('changePassword') ChangePassword changePassword, Model model) {
        if (changePassword.password != changePassword.confirmPassword) {
            model.addAttribute('error', ERROR_PASSWORD_NOT_MATCH_MESSAGE)
            return VIEW_CHANGE_PASSWORD
        }
        try {
            apiAuthClient.resetPassword(new ResetPasswordModel(password: changePassword.password, token: changePassword.passwordToken))
        } catch (FeignException feignException) {
            model.addAttribute('error', getErrorMessage(feignException))
        return VIEW_CHANGE_PASSWORD
        }

        return REDIRECT_LOGIN + '?success=' + SUCCESS_CHANGE_PASSWORD
    }

    private String getErrorMessage(FeignException feignException) {
        return StringUtils.substringBetween(feignException.getMessage(), '"message":"', '"}')
    }
}
