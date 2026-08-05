package com.example.hotel_booking.booking.mapper;

import com.example.hotel_booking.booking.dto.BookingCreateRequest;
import com.example.hotel_booking.booking.dto.BookingResponse;
import com.example.hotel_booking.booking.dto.CheckoutResponse;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;
import com.example.hotel_booking.bookingroom.entity.BookingRoom;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import com.example.hotel_booking.bookingservicedetail.entity.BookingServiceDetail;
import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.roomtype.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BookingMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "arrivalDate", source = "request.checkIn")
    @Mapping(target = "departureDate", source = "request.checkOut")
    @Mapping(target = "contactName", source = "request.contactName")
    @Mapping(target = "contactPhone", source = "request.contactPhone")
    @Mapping(target = "roomAmount", source = "totalRoomAmount")
    @Mapping(target = "serviceAmount", source = "serviceAmount")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "depositAmount", source = "deposit")
    @Mapping(target = "expiryDate", expression = "java(java.time.LocalDateTime.now().plusMinutes(15))") // Nên để 15p cho khách kịp cọc
    @Mapping(target = "status", constant = "PENDING_DEPOSIT")
    @Mapping(target = "bookingRooms", ignore = true)
    @Mapping(target = "bookingServices", ignore = true)
    Booking toBooking(BookingCreateRequest request,
                      Customer customer,
                      BigDecimal totalRoomAmount,
                      BigDecimal serviceAmount,
                      BigDecimal totalAmount,
                      BigDecimal deposit);


    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomNumber", target = "roomNumber")
    @Mapping(source = "roomType.name", target = "roomType")
    @Mapping(source = "roomType.id", target = "roomTypeId")
    @Mapping(source = "id", target = "bookingRoomId")
    BookingRoomResponse toBookingRoomResponse(BookingRoom bookingRoom);

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "extraService.name", target = "serviceName")
    @Mapping(target = "totalPrice", expression = "java(bookingServiceDetail.getPriceAtOrder().multiply(BigDecimal.valueOf(bookingServiceDetail.getQuantity())))")
    BookingServiceDetailResponse toBookingServiceDetailResponse(BookingServiceDetail bookingServiceDetail);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "customer.phoneNumber", target = "customerPhone")
    @Mapping(target = "remainingAmount", ignore = true)
    BookingResponse toBookingResponse(Booking booking);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "customer.phoneNumber", target = "customerPhone")
    @Mapping(target = "remainingAmount", ignore = true)
    @Mapping(target = "bookingRooms", ignore = true)
    @Mapping(target = "bookingServices", ignore = true)
    BookingResponse toBookingHistoryResponse(Booking booking);


    // Mapper cho bảng con - Nhận vào Request, Room và Booking để map 1 lần
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "booking", target = "booking")
    @Mapping(source = "roomType", target = "roomType")
    @Mapping(target = "room", ignore = true) // chưa assign phòng
    @Mapping(source = "roomType.price", target = "priceAtOrder")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    BookingRoom toBookingRoom(RoomType roomType, Booking booking);

    @Mapping(source = "id", target = "bookingId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "arrivalDate", target = "checkInDate")
    @Mapping(source = "departureDate", target = "checkOutDate")
    CheckoutResponse toCheckoutResponse(Booking booking);


}