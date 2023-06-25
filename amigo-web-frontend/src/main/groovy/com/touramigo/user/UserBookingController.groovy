package com.touramigo.user

import com.touramigo.booking.*
import com.touramigo.booking.confirmation.TourOperatorClientService
import com.touramigo.config.BookingNotFoundException
import com.touramigo.email.EmailService
import com.touramigo.payment.StripePaymentService
import com.touramigo.security.SecurityUtils
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

import java.util.stream.Collectors

@Controller
@RequestMapping(value = "/user/booking")
@CompileStatic
class UserBookingController {
    private static String VIEW_LIST_BOOKING = 'user/bookingList'
    private static String VIEW_BOOKING = 'user/bookingDetails'
    private static String VIEW_CHANGE_BOOKING = 'user/bookingChange'
    private static String VIEW_CANCEL_BOOKING = 'user/bookingCancel'
    private static String VIEW_UPDATE_PAYMENT = 'user/bookingUpdatePayment'
    private static String VIEW_PROCESS_DEPOSIT_PAYMENT = 'user/bookingProcessDepositPayment'

    private static String REDIRECT_BOOKING = 'redirect:/user/booking'

    private static String SUCCESS_CHANGE_BOOKING = 'change'
    private static String SUCCESS_CHANGE_BOOKING_MESSAGE = 'We have received your booking change request, we will be in touch shortly.'
    private static String SUCCESS_CANCEL_BOOKING = 'cancel'
    private static String SUCCESS_CANCEL_BOOKING_MESSAGE = 'We have received your booking cancellation, we will be in touch shortly.'
    private static String SUCCESS_CHANGE_PAYMENT = 'payment'
    private static String SUCCESS_CHANGE_PAYMENT_MESSAGE = 'Your payment method was updated.'
    private static String SUCCESS_PROCESS_DEPOSIT_PAYMENT = 'processPayment'
    private static String SUCCESS_PROCESS_DEPOSIT_PAYMENT_MESSAGE = 'Your payment method was passed to processing.'

    private static final String ERROR_FORM_MESSAGE = 'Form contains errors. Please try again.'
    private static final String ERROR_SEND_EMAIL_MESSAGE = 'There was a problem with sending your request. Please try again.'

    private static final String CHANGE_REASON_QUESTION = 'There may be fees and restrictions from the tour operator for changing your booking. Please tell us the reason for change:'
    private static final String CANCEL_REASON_QUESTION = 'There may be fees and restrictions from the tour operator for cancelling your tour booking. Please tell us the reason for cancellation:'

    private final EmailService emailService
    private final BookingRepository bookingRepository
    private final StripePaymentService stripePaymentService
    private final TourOperatorClientService tourOperatorClientService

    @Autowired
    UserBookingController(EmailService emailService, BookingRepository bookingRepository, StripePaymentService stripePaymentService, TourOperatorClientService tourOperatorClientService) {
        this.emailService = emailService
        this.bookingRepository = bookingRepository
        this.stripePaymentService = stripePaymentService
        this.tourOperatorClientService = tourOperatorClientService
    }

    @GetMapping()
    list(@RequestParam('success') Optional<String> success, Model model) {
        switch(success.orElse(null)) {
            case SUCCESS_CHANGE_BOOKING:
                model.addAttribute('success', SUCCESS_CHANGE_BOOKING_MESSAGE)
                break
            case SUCCESS_CANCEL_BOOKING:
                model.addAttribute('success', SUCCESS_CANCEL_BOOKING_MESSAGE)
                break
            case SUCCESS_CHANGE_PAYMENT:
                model.addAttribute('success', SUCCESS_CHANGE_PAYMENT_MESSAGE)
                break
            case SUCCESS_PROCESS_DEPOSIT_PAYMENT:
                model.addAttribute('success', SUCCESS_PROCESS_DEPOSIT_PAYMENT_MESSAGE)
                break
        }

        def bookings = bookingRepository.findAllByPassengerEmail(SecurityContextHolder.context.authentication.name)
        def processedBookings = bookings.stream()
                .filter({Booking booking -> booking.payments.size() > 0 })
                .collect(Collectors.toList())

        processedBookings.forEach({Booking booking -> booking.payments.sort( { Payment payment -> payment.paymentAmount})})
        model.addAttribute('bookings', processedBookings)

        return VIEW_LIST_BOOKING
    }

    @GetMapping('/{bookingId}/details')
    details(@PathVariable('bookingId') UUID bookingId, Model model) {
        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }
        model.addAttribute('booking', booking)
        return VIEW_BOOKING
    }

    @GetMapping('/{bookingId}/change')
    change(@PathVariable('bookingId') UUID bookingId, Model model) {
        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (!booking.isEditable()) {
            return REDIRECT_BOOKING
        }

        if(booking != null && booking.payments != null && booking.payments.size()>0 && booking.payments[0].serviceType != null && booking.payments[0].serviceType == PaymentServiceType.AFTERPAY) {
            return REDIRECT_BOOKING
        }
        
        model.addAttribute('booking', booking)
        model.addAttribute('updateBooking', new UpdateBooking())
        return VIEW_CHANGE_BOOKING
    }

    @PostMapping('/{bookingId}/change')
    changeProcess(@PathVariable('bookingId') UUID bookingId, @Validated() @ModelAttribute('updateBooking') UpdateBooking updateBooking, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute('updateBooking', updateBooking)
            model.addAttribute('error', ERROR_FORM_MESSAGE)
            return VIEW_CHANGE_BOOKING
        }

        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (!booking.isEditable()) {
            return REDIRECT_BOOKING
        }

        model.addAttribute('booking', booking)

        BookingChangeRequest bookingChangeRequest = BookingChangeRequest.fromBooking(booking, CHANGE_REASON_QUESTION, updateBooking.reason, 'change')

        BookingChangeResponse operatorResponse = tourOperatorClientService.requestBookingChange(booking.operatorCode, bookingChangeRequest)
        if (operatorResponse.errorMessage) {
            model.addAttribute('updateBooking', updateBooking)
            model.addAttribute('error', ERROR_SEND_EMAIL_MESSAGE)
            return VIEW_CHANGE_BOOKING
        }

        BookingChangeResponse confirmationResponse = emailService.sendChangeBookingConfirmationEmail(SecurityContextHolder.context.authentication.name, bookingChangeRequest)
        if (confirmationResponse.errorMessage) {
            model.addAttribute('updateBooking', updateBooking)
            model.addAttribute('error', ERROR_SEND_EMAIL_MESSAGE)
            return VIEW_CHANGE_BOOKING
        }

        return REDIRECT_BOOKING + '?success=' + SUCCESS_CHANGE_BOOKING
    }

    @GetMapping('/{bookingId}/cancel')
    cancel(@PathVariable('bookingId') UUID bookingId, Model model) {
        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (!booking.isEditable()) {
            return REDIRECT_BOOKING
        }

        model.addAttribute('booking', booking)
        model.addAttribute('updateBooking', new UpdateBooking())
        return VIEW_CANCEL_BOOKING
    }

    @PostMapping('/{bookingId}/cancel')
    cancelProcess(@PathVariable('bookingId') UUID bookingId, @Validated() @ModelAttribute('updateBooking') UpdateBooking updateBooking, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute('updateBooking', updateBooking)
            model.addAttribute('error', ERROR_FORM_MESSAGE)
            return VIEW_CANCEL_BOOKING
        }

        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (!booking.isEditable()) {
            return REDIRECT_BOOKING
        }

        model.addAttribute('booking', booking)

        BookingChangeRequest bookingChangeRequest = BookingChangeRequest.fromBooking(booking, CANCEL_REASON_QUESTION, updateBooking.reason, 'cancel')

        BookingChangeResponse operatorResponse = tourOperatorClientService.requestBookingChange(booking.operatorCode, bookingChangeRequest)
        if (operatorResponse.errorMessage) {
            model.addAttribute('updateBooking', updateBooking)
            model.addAttribute('error', ERROR_SEND_EMAIL_MESSAGE)
            return VIEW_CANCEL_BOOKING
        }

        BookingChangeResponse confirmationResponse = emailService.sendChangeBookingConfirmationEmail(SecurityContextHolder.context.authentication.name, bookingChangeRequest)
        if (confirmationResponse.errorMessage) {
            model.addAttribute('updateBooking', updateBooking)
            model.addAttribute('error', ERROR_SEND_EMAIL_MESSAGE)
            return VIEW_CANCEL_BOOKING
        }

        return REDIRECT_BOOKING + '?success=' + SUCCESS_CANCEL_BOOKING
    }

    @GetMapping('/{bookingId}/updatePayment')
    updatePayment(@PathVariable('bookingId') UUID bookingId, Model model) {
        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (booking.isPaid() || !booking.isEditable()) {
            return REDIRECT_BOOKING
        }

        model.addAttribute('booking', booking)
        model.addAttribute('stripeKey', stripePaymentService.stripePublishKey)
        return VIEW_UPDATE_PAYMENT
    }

    @PostMapping('/{bookingId}/updatePayment')
    updatePaymentProcess(@PathVariable('bookingId') UUID bookingId, @Validated(Booking.PaymentStep) @ModelAttribute('payment') Payment payment, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute('error', ERROR_FORM_MESSAGE)
            return VIEW_UPDATE_PAYMENT
        }

        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (booking.isPaid() || !booking.isEditable()) {
            return REDIRECT_BOOKING
        }

        stripePaymentService.createStripePayment(booking, payment)

        return REDIRECT_BOOKING + '?success=' + SUCCESS_CHANGE_PAYMENT
    }

    @GetMapping('/{bookingId}/processFinalDepositPayment')
    finalDepositPayment(@PathVariable('bookingId') UUID bookingId, Model model) {
        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (booking.isPaid() || !booking.editable || !booking.finalDepositPaymentAvailable) {
            return REDIRECT_BOOKING
        }

        Payment payment = booking.payments.find({Payment payment -> payment.status == PaymentStatus.UNPROCESSED && payment.paymentType == PaymentType.DEPOSIT_FINAL && payment.paymentDueDate != null})
        if (payment == null) {
            return REDIRECT_BOOKING
        }

        model.addAttribute('payment', payment)
        model.addAttribute('booking', booking)

        return VIEW_PROCESS_DEPOSIT_PAYMENT
    }

    @PostMapping('/{bookingId}/processFinalDepositPayment')
    finalDepositPaymentProcess(@PathVariable('bookingId') UUID bookingId) {
        def booking = bookingRepository.findOne(bookingId)
        if (!booking || booking.partnerId != SecurityUtils.authenticatedUserInfo.partnerId) {
            throw new BookingNotFoundException(bookingId.toString())
        }

        if (booking.isPaid() || !booking.editable || !booking.finalDepositPaymentAvailable) {
            return REDIRECT_BOOKING
        }

        stripePaymentService.processFinalDepositPaymentForBooking(booking)

        return REDIRECT_BOOKING + '?success=' + SUCCESS_PROCESS_DEPOSIT_PAYMENT
    }
}
