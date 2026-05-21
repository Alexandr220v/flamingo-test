package com.flamingo.qa.api.restapi.bookings;

import com.flamingo.qa.api.restapi.bookings.BookingDataProvider.InvalidCreateBookingData;
import com.flamingo.qa.api.restapi.bookings.BookingDataProvider.InvalidUpdateBookingData;
import com.flamingo.qa.api.restapi.client.BookingClient;
import com.flamingo.qa.api.restapi.dto.Booking;
import com.flamingo.qa.api.restapi.dto.BookingDates;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
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
@Feature("Booking API - negative scenarios")
class BookingNegativeScenariosTests {

    private final BookingClient bookingClient = new BookingClient();

    @ParameterizedTest(name = "Invalid data: {0}")
    @ArgumentsSource(InvalidCreateBookingData.class)
    @DisplayName("POST /booking with invalid data.")
    void verifyCreateBookingWithInvalidData(String scenario, String firstName, String lastName,
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

        Response resp = bookingClient.createBookingError(booking);

        //No validation message in response body, but at least some error content expected
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(resp.statusCode())
                .isEqualTo(HttpStatus.SC_BAD_REQUEST);
        softly.assertThat(resp.body().asString())
                .as("body has error content")
                .isNotBlank();
        softly.assertAll();
    }

    @ParameterizedTest(name = "Invalid data: {0}")
    @ArgumentsSource(InvalidUpdateBookingData.class)
    @DisplayName("PUT /booking/{id} with invalid data")
    void verifyUpdateBookingWithInvalidData(String scenario, String firstName, String lastName,
                                            Integer totalPrice, Boolean depositPaid,
                                            String checkin, String checkout,
                                            String additionalNeeds) {
        int id = bookingClient.createBooking(BookingDataProvider.sampleBooking()).getBookingId();

        Booking payload = Booking.builder()
                .firstName(firstName)
                .lastName(lastName)
                .totalPrice(totalPrice)
                .depositPaid(depositPaid)
                .bookingDates(new BookingDates(checkin, checkout))
                .additionalNeeds(additionalNeeds)
                .build();

        Response resp = bookingClient.updateBookingError(id, payload);

//No validation implemented for some invalid scenarios
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(resp.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        softly.assertThat(resp.body().asString()).as("body has error content").isNotBlank();
        softly.assertAll();
    }

    @Test
    @DisplayName("GET /booking/{id} with a non-existent id.")
    void verifyGetNonExistentBooking() {
        Response resp = bookingClient.getBookingByIdError(BookingDataProvider.nonExistentId());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(resp.statusCode()).as("status").isEqualTo(HttpStatus.SC_NOT_FOUND);
        softly.assertThat(resp.body().asString()).as("body").containsIgnoringCase("not found");
        softly.assertAll();
    }

    @Test
    @DisplayName("POST /booking with a malformed payload.")
    void verifyCreateWithInvalidPayload() {
        Response resp = bookingClient.createBookingError(BookingDataProvider.invalidBookingPayload());

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(resp.statusCode())
                .as("status malformed payload")
                .isGreaterThanOrEqualTo(HttpStatus.SC_BAD_REQUEST);
        softly.assertThat(resp.body().asString())
                .as("body has some error content")
                .isNotBlank();
        softly.assertAll();
    }

    @Test
    @DisplayName("PUT /booking/{id} without  authentification.")
    void verifyUpdateWithoutAuthIsForbidden() {
        int id = bookingClient.createBooking(BookingDataProvider.sampleBooking()).getBookingId();

        Response resp = bookingClient.updateBookingNoAuth(id, BookingDataProvider.updatedBooking());

        assertThat(resp.statusCode())
                .as("PUT without token must not succeed")
                .isEqualTo(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("DELETE /booking/{id} without authentification.")
    void verifyDeleteWithoutAuthIsForbidden() {
        int id = bookingClient.createBooking(BookingDataProvider.sampleBooking()).getBookingId();

        Response deleteResp = bookingClient.deleteBookingNoAuth(id);
        Response getResp = bookingClient.getBookingByIdError(id);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(deleteResp.statusCode())
                .as("anonymous DELETE rejected")
                .isEqualTo(HttpStatus.SC_FORBIDDEN);
        softly.assertThat(getResp.statusCode())
                .as("record still exists after rejected delete")
                .isEqualTo(HttpStatus.SC_OK);
        softly.assertAll();
    }
}
