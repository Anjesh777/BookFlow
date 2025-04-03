package com.BookFlow.bookflow.dto;

import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.BookFlow.bookflow.dto.BookingDto;
import com.BookFlow.bookflow.model.Booking;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;


@Component
public class QueMapper {

    @Autowired  // Add UserRepository to fetch user details
    private UserRepo userRepository;

    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto dto = new BookingDto();

        dto.setBookingId(booking.getBooking_id() != null ? booking.getBooking_id().toString() : null);
        dto.setServiceId(booking.getService() != null ? booking.getService().getService_id().toString() : null);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        dto.setAppointmentDate(booking.getAppointment_date() != null ?
                booking.getAppointment_date().format(formatter) : null);
        dto.setBookingDate(booking.getBooking_date() != null ?
                booking.getBooking_date().format(formatter) : null);

        dto.setPaymentStatus(booking.getPayment_status());
        dto.setBookingNotes(booking.getBooking_notes());
        dto.setBookingStatus(String.valueOf(booking.getBooking_status()));

        dto.setFixedDatedTime(booking.getFixedDatedTime() != null ?
                booking.getFixedDatedTime().format(formatter) : null);
        dto.setPaymentMethod(booking.getPaymentMethod());

        if (booking.getService() != null) {
            dto.setServiceName(booking.getService().getServiceName());
            dto.setServiceDescription(booking.getService().getServiceDescription());
            dto.setServicePrice(booking.getService().getPrice());
            dto.setServiceCategory(booking.getService().getCategory());
        }

        dto.setDuration(booking.getDuration());
        dto.setEndTime(booking.getEndTime() != null ?
                booking.getEndTime().format(formatter) : null);

        dto.setBookingPrice(booking.getExpectedAmount() != null ?
                booking.getExpectedAmount().toString() : null);
        dto.setExpectedAmount(booking.getExpectedAmount());


        // Get user fullname properly
        String userName = userRepository.findFullnameById(booking.getUser_id())
                .orElse("Unknown");
        dto.setUserName(userName);

        return dto;
    }
}
