package com.flamingo.qa.api.restapi.client;

import com.flamingo.qa.api.BaseClient;
import com.flamingo.qa.api.restapi.dto.Booking;
import com.flamingo.qa.api.restapi.dto.CreateBookingResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import static com.flamingo.qa.api.restapi.RestAssuredSpecification.bookerSpec;
import static com.flamingo.qa.api.restapi.RestAssuredSpecification.bookerAuthenticatedSpec;
import static java.lang.String.format;

public class BookingClient extends BaseClient {

    private static final String BOOKINGS = "/booking";
    private static final String BOOKING_BY_ID = BOOKINGS + "/%d";

    @Step("Create booking")
    public CreateBookingResponse createBooking(Booking booking) {
        return post(bookerSpec(), BOOKINGS, booking, HttpStatus.SC_OK, CreateBookingResponse.class);
    }

    @Step("Get booking by id={id}")
    public Booking getBookingById(int id) {
        return get(bookerSpec(), format(BOOKING_BY_ID, id), HttpStatus.SC_OK, Booking.class);
    }

    @Step("Update booking id={id}")
    public Booking updateBooking(int id, Booking booking) {
        return put(bookerAuthenticatedSpec(), format(BOOKING_BY_ID, id), booking, HttpStatus.SC_OK, Booking.class);
    }

    @Step("Update booking id={id}")
    public Response updateBookingError(int id, Booking booking) {
        return put(bookerAuthenticatedSpec(), format(BOOKING_BY_ID, id), booking);
    }

    @Step("Delete booking id={id}")
    public void deleteBooking(int id) {
        delete(bookerAuthenticatedSpec(), format(BOOKING_BY_ID, id), HttpStatus.SC_CREATED);
    }

    @Step("Get booking by id={id} error.")
    public Response getBookingByIdError(int id) {
        return get(bookerSpec(), format(BOOKING_BY_ID, id));
    }

    @Step("Create booking error.")
    public Response createBookingError(Object body) {
        return post(bookerSpec(), BOOKINGS, body);
    }

    @Step("Update booking id={id} without authentification.")
    public Response updateBookingNoAuth(int id, Booking booking) {
        return put(bookerSpec(), format(BOOKING_BY_ID, id), booking);
    }

    @Step("Delete booking id={id} without authentification.")
    public Response deleteBookingNoAuth(int id) {
        return delete(bookerSpec(), format(BOOKING_BY_ID, id));
    }
}
