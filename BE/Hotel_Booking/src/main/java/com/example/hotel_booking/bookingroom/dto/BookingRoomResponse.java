package com.example.hotel_booking.bookingroom.dto;

import com.example.hotel_booking.common.BookingRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing booking room details")
public class BookingRoomResponse {

    @Schema(description = "Unique identifier of the booking room item", example = "10")
    private Long bookingRoomId;

    @Schema(description = "Assigned physical room ID", example = "101")
    private Long roomId;

    @Schema(description = "Assigned room number", example = "301")
    private String roomNumber;

    @Schema(description = "Price per night recorded at the time of booking", example = "500000.00")
    private BigDecimal priceAtOrder;

    @Schema(description = "Name of the room type", example = "DELUXE")
    private String roomType;

    @Schema(description = "Code of the room type", example = "1")
    private Long roomTypeId;


    @Schema(description = "Current status of the booking room item", example = "PENDING")
    private BookingRoomStatus status;
}