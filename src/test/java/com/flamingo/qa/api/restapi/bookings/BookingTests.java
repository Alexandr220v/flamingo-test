package com.flamingo.qa.api.restapi.bookings;

import com.flamingo.qa.api.restapi.bookings.BookingDataProvider.ValidBookingData;
import com.flamingo.qa.api.restapi.client.BookingClient;
import com.flamingo.qa.api.restapi.dto.Booking;
import com.flamingo.qa.api.restapi.dto.BookingDates;
import io.qameta.allure.Epic;
import lombok.val;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;


@Tag("api")
@Execution(ExecutionMode.CONCURRENT)
public class BookingTests {

    private final BookingClient bookingClient = new BookingClient();

    @ParameterizedTest(name = "Booking data: {0} {1}, price={2}, depositPaid={3}, checkin={4}, checkout={5}, additionalNeeds={6}")
    @ArgumentsSource(ValidBookingData.class)
    @DisplayName("POST /booking -  creates bookings.")
    void verifyCreateBooking(String firstName, String lastName,
                             Integer totalPrice, Boolean depositPaid,
                             String checkin, String checkout,
                             String additionalNeeds) {
        Booking booking = Booking.builder()
                .firstName(firstName)
                .lastName(lastName)
                .totalPrice(totalPrice)
                .depositPaid(depositPaid)
                .bookingDates(new BookingDates(checkin, checkout))
                .additionalNeeds(additionalNeeds)
                .build();

        val resp = bookingClient.createBooking(booking);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(resp.getBookingId()).isPositive();
        softly.assertThat(resp.getBooking())
                .usingRecursiveComparison()
                .isEqualTo(booking);
        softly.assertAll();
    }

    @Test
    @DisplayName("GET /booking/{id} - returns created booking")
    void verifyGetBookingById() {
        Booking expected = BookingDataProvider.sampleBooking();
        int id = bookingClient.createBooking(expected).getBookingId();

        Booking actual = bookingClient.getBookingById(id);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("PUT /booking/{id} - update booking.")
    void verifyUpdateBooking() {
        int id = bookingClient.createBooking(BookingDataProvider.sampleBooking()).getBookingId();

        Booking payload = Booking.builder()
                .firstName("Janet")
                .lastName("Doe")
                .totalPrice(250)
                .depositPaid(true)
                .bookingDates(new BookingDates("2025-01-10", "2025-01-15"))
                .additionalNeeds("Breakfast")
                .build();

        Booking returned = bookingClient.updateBooking(id, payload);

        assertThat(returned)
                .usingRecursiveComparison()
                .isEqualTo(payload);
    }

    @Test
    @DisplayName("DELETE /booking/{id} - remove existing booking")
    void verifyDeleteBooking() {
        int id = bookingClient.createBooking(BookingDataProvider.sampleBooking()).getBookingId();

        bookingClient.deleteBooking(id);

        assertThat(bookingClient.getBookingByIdError(id).statusCode())
                .as("Status code should be 404 after deletion")
                .isEqualTo(HttpStatus.SC_NOT_FOUND);
    }
}
