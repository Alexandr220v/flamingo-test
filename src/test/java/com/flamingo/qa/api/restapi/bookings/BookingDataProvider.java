package com.flamingo.qa.api.restapi.bookings;

import com.flamingo.qa.api.restapi.dto.Booking;
import com.flamingo.qa.api.restapi.dto.BookingDates;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Centralized data and argument providers for booking tests.
 * Contains static factory methods for test data and nested {@link ArgumentsProvider}
 * classes for parameterized tests.
 */
public final class BookingDataProvider {

    private BookingDataProvider() {}

    public static Booking sampleBooking() {
        return Booking.builder()
                .firstName("Jane")
                .lastName("Doe")
                .totalPrice(199)
                .depositPaid(true)
                .bookingDates(new BookingDates("2025-01-10", "2025-01-15"))
                .additionalNeeds("Breakfast")
                .build();
    }

    public static Booking uniqueBooking() {
        return sampleBooking().toBuilder()
                .firstName("Jane-" + UUID.randomUUID().toString().substring(0, 8))
                .build();
    }

    public static Booking updatedBooking() {
        return sampleBooking().toBuilder()
                .firstName("Janet")
                .totalPrice(250)
                .build();
    }

    public static Map<String, Object> invalidBookingPayload() {
        return Map.of("firstname", "Jane");
    }

    public static int nonExistentId() {
        return 9_999_999;
    }

    // ─── ArgumentsProvider nested classes ─────────────────────────────────────

    public static class ValidBookingData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("Jane", "Doe", 199, true, "2025-01-10", "2025-01-15", "Breakfast"),
                    Arguments.of("John", "Smith", 350, false, "2025-03-01", "2025-03-10", "Late checkout"),
                    Arguments.of("Alice", "Wonder", 99, true, "2025-06-20", "2025-06-25", null),
                    Arguments.of("Bob", "Builder", 500, true, "2025-12-24", "2025-12-31", "Extra pillow"),
                    Arguments.of("Jane", "Doe", 0, true, "2025-01-10", "2025-01-15", "Breakfast"),
                    Arguments.of("Jane", "Doe", Integer.MAX_VALUE, true, "2025-01-10", "2025-01-15", null),
                    Arguments.of("Jane", "Doe", 1, false, "2025-01-10", "2025-01-15", "Breakfast")
            );
        }
    }

    public static class InvalidCreateBookingData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("Null firstname", null, "Doe", 199, true, "2025-01-10", "2025-01-15", "Breakfast"),
                    Arguments.of("Null lastname", "Jane", null, 199, true, "2025-01-10", "2025-01-15", "Breakfast"),
                    Arguments.of("Null firstname and lastname", null, null, 199, true, "2025-01-10", "2025-01-15", null),
                    Arguments.of("Null total price", "Jane", "Doe", null, true, "2025-01-10", "2025-01-15", "Breakfast"),
                    Arguments.of("Null dates", "Jane", "Doe", 199, true, null, null, "Breakfast"),
                    Arguments.of("Future dates beyond range", "Jane", "Doe", 199, true, "2099-01-01", "2099-12-31", "Breakfast"),
                    Arguments.of("Checkout before checkin", "Jane", "Doe", 199, true, "2025-06-15", "2025-06-10", "Breakfast"),
                    Arguments.of("Negative total price", "Jane", "Doe", -100, true, "2025-01-10", "2025-01-15", "Breakfast"),
                    Arguments.of("Invalid date format", "Jane", "Doe", 199, true, "not-a-date", "also-not-a-date", "Breakfast"),
                    Arguments.of("Empty string names", "", "", 199, true, "2025-01-10", "2025-01-15", "Breakfast")
            );
        }
    }

    public static class InvalidUpdateBookingData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("Null firstname", null, "Doe", 250, true, "2025-01-10", "2025-01-15", "Lunch"),
                    Arguments.of("Null lastname", "Janet", null, 250, true, "2025-01-10", "2025-01-15", "Lunch"),
                    Arguments.of("Null total price", "Janet", "Doe", null, true, "2025-01-10", "2025-01-15", "Lunch"),
                    Arguments.of("Null dates", "Janet", "Doe", 250, true, null, null, "Lunch"),
                    Arguments.of("Future dates beyond range", "Janet", "Doe", 250, true, "2099-01-01", "2099-12-31", "Lunch"),
                    Arguments.of("Checkout before checkin", "Janet", "Doe", 250, true, "2025-06-15", "2025-06-10", "Lunch"),
                    Arguments.of("Negative total price", "Janet", "Doe", -500, true, "2025-01-10", "2025-01-15", "Lunch"),
                    Arguments.of("Invalid date format", "Janet", "Doe", 250, true, "invalid", "invalid", "Lunch"),
                    Arguments.of("Empty string names", "", "", 250, true, "2025-01-10", "2025-01-15", "Lunch")
            );
        }
    }
}

