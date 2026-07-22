package com.example.hotel_booking.booking;

import com.example.hotel_booking.booking.dto.BookingCancelResponse;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.booking.repository.BookingRepository;
import com.example.hotel_booking.booking.service.impl.BookingServiceImpl;
import com.example.hotel_booking.bookingroom.entity.BookingRoom;
import com.example.hotel_booking.bookingroom.repository.BookingRoomRepository;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.common.PaymentType;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.common.service.VnPayService;
import com.example.hotel_booking.payment.entity.Payment;
import com.example.hotel_booking.payment.repository.PaymentRepository;
import com.example.hotel_booking.room.entity.Room;
import com.example.hotel_booking.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingCancellationTests {

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private BookingRoomRepository bookingRoomRepo;

    @Mock
    private PaymentRepository paymentRepo;

    @Mock
    private RoomRepository roomRepo;

    @Mock
    private VnPayService vnPayService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingRoom bookingRoom;
    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(101L);
        room.setStatus(com.example.hotel_booking.common.RoomStatus.OCCUPIED);

        bookingRoom = new BookingRoom();
        bookingRoom.setId(201L);
        bookingRoom.setPriceAtOrder(new BigDecimal("1000000"));
        bookingRoom.setRoom(room);
        bookingRoom.setStatus(BookingStatus.CONFIRMED);

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setArrivalDate(LocalDateTime.now().plusDays(8));
        booking.setDepartureDate(LocalDateTime.now().plusDays(9));
        booking.setDepositAmount(new BigDecimal("1000000"));
        booking.setRoomAmount(new BigDecimal("1000000"));
        booking.setTotalAmount(new BigDecimal("1000000"));
        booking.setPaymentCode("PAY-123");
        booking.setContactName("Nguyen Van A");

        bookingRoom.setBooking(booking);
        booking.setBookingRooms(new ArrayList<>(Collections.singletonList(bookingRoom)));
    }

    @Test
    void cancelSingleRoom_MoreThan7Days_Refund100Percent_Success() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRoomRepo.findById(201L)).thenReturn(Optional.of(bookingRoom));

        Payment payment = Payment.builder()
                .status(PaymentStatus.SUCCESS)
                .method(PaymentMethod.VNPAY)
                .transactionId("123456")
                .amount(new BigDecimal("1000000"))
                .paidAt(LocalDateTime.now())
                .build();
        when(paymentRepo.findByBookingId(1L)).thenReturn(Collections.singletonList(payment));

        Map<String, Object> mockRefundResult = new HashMap<>();
        mockRefundResult.put("vnp_ResponseCode", "00");
        when(vnPayService.refund(anyString(), anyString(), any(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenReturn(mockRefundResult);

        BookingCancelResponse response = bookingService.cancelSingleRoom(1L, 201L);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(new BigDecimal("1000000"), response.getRefundedAmount());
        assertEquals(BookingStatus.CANCELLED, bookingRoom.getStatus());
        assertEquals(com.example.hotel_booking.common.RoomStatus.AVAILABLE, room.getStatus());
        assertEquals(BigDecimal.ZERO, booking.getTotalAmount());
        assertEquals(BookingStatus.CANCELLED, booking.getStatus()); 
    }

    @Test
    void cancelSingleRoom_Between3And7Days_Refund50Percent_Success() {
        booking.setArrivalDate(LocalDateTime.now().plusDays(5));
        booking.setDepartureDate(LocalDateTime.now().plusDays(6));

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRoomRepo.findById(201L)).thenReturn(Optional.of(bookingRoom));

        Payment payment = Payment.builder()
                .status(PaymentStatus.SUCCESS)
                .method(PaymentMethod.VNPAY)
                .transactionId("123456")
                .amount(new BigDecimal("1000000"))
                .paidAt(LocalDateTime.now())
                .build();
        when(paymentRepo.findByBookingId(1L)).thenReturn(Collections.singletonList(payment));

        Map<String, Object> mockRefundResult = new HashMap<>();
        mockRefundResult.put("vnp_ResponseCode", "00");
        when(vnPayService.refund(anyString(), anyString(), any(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenReturn(mockRefundResult);

        BookingCancelResponse response = bookingService.cancelSingleRoom(1L, 201L);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(new BigDecimal("500000.0"), response.getRefundedAmount()); 
    }

    @Test
    void cancelSingleRoom_LessThan3Days_Refund0Percent_Success() {
        booking.setArrivalDate(LocalDateTime.now().plusDays(2));
        booking.setDepartureDate(LocalDateTime.now().plusDays(3));

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRoomRepo.findById(201L)).thenReturn(Optional.of(bookingRoom));

        BookingCancelResponse response = bookingService.cancelSingleRoom(1L, 201L);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getRefundedAmount()); 
        verifyNoInteractions(vnPayService);
    }

    @Test
    void cancelSingleRoom_CheckedIn_ThrowsException() {
        bookingRoom.setStatus(BookingStatus.CHECKED_IN);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRoomRepo.findById(201L)).thenReturn(Optional.of(bookingRoom));

        AppException exception = assertThrows(AppException.class, () ->
                bookingService.cancelSingleRoom(1L, 201L)
        );
        assertEquals(ErrorCode.INVALID_BOOKING_STATUS, exception.getErrorCode());
    }

    @Test
    void cancelFullBooking_MoreThan7Days_Refund100Percent_Success() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        Payment payment = Payment.builder()
                .status(PaymentStatus.SUCCESS)
                .method(PaymentMethod.VNPAY)
                .transactionId("123456")
                .amount(new BigDecimal("1000000"))
                .paidAt(LocalDateTime.now())
                .build();
        when(paymentRepo.findByBookingId(1L)).thenReturn(Collections.singletonList(payment));

        Map<String, Object> mockRefundResult = new HashMap<>();
        mockRefundResult.put("vnp_ResponseCode", "00");
        when(vnPayService.refund(anyString(), anyString(), any(), anyString(), any(), anyString(), anyString(), anyString()))
                .thenReturn(mockRefundResult);

        BookingCancelResponse response = bookingService.cancelFullBooking(1L, PaymentMethod.VNPAY);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(new BigDecimal("1000000"), response.getRefundedAmount());
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(BookingStatus.CANCELLED, bookingRoom.getStatus());
        assertEquals(com.example.hotel_booking.common.RoomStatus.AVAILABLE, room.getStatus());
    }
}
