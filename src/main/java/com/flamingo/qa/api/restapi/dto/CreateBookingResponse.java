package com.flamingo.qa.api.restapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    @JsonProperty("bookingid")
    private Integer bookingId;
    private Booking booking;
}
