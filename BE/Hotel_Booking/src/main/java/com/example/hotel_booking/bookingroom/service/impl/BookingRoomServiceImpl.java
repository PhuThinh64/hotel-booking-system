package com.example.hotel_booking.bookingroom.service.impl;

import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.booking.repository.BookingRepository;
import com.example.hotel_booking.bookingroom.dto.BookingRoomCreateRequest;
import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;
import com.example.hotel_booking.bookingroom.entity.BookingRoom;
import com.example.hotel_booking.bookingroom.mapper.BookingRoomMapper;
import com.example.hotel_booking.bookingroom.repository.BookingRoomRepository;
import com.example.hotel_booking.bookingroom.service.BookingRoomService;
import com.example.hotel_booking.bookingservicedetail.entity.BookingServiceDetail;
import com.example.hotel_booking.bookingservicedetail.repository.BookingServiceDetailRepository;
import com.example.hotel_booking.common.BookingRoomStatus;
import com.example.hotel_booking.common.BookingServiceStatus;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.RoomStatus;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.room.entity.Room;
import com.example.hotel_booking.room.repository.RoomRepository;
import com.example.hotel_booking.roomtype.entity.RoomType;
import com.example.hotel_booking.roomtype.repository.RoomTypeRepository;
import com.example.hotel_booking.roomtype.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingRoomServiceImpl implements BookingRoomService {

    private final BookingRoomRepository bookingRoomRepo;
    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;
    private final RoomTypeRepository roomTypeRepo;
    private final BookingServiceDetailRepository bookingServiceRepo;
    private final BookingRoomMapper bookingRoomMapper;
    private final RoomTypeService  roomTypeService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public BookingRoomResponse addRoom(BookingRoomCreateRequest request) {
        
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        validateBookingStatus(booking);

        
        RoomType roomType = roomTypeRepo.findById(request.getRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        
        long available = roomTypeService.getAvailableCount(
                request.getRoomTypeId(),
                booking.getArrivalDate(),
                booking.getDepartureDate(),
                booking.getId()
        );

        
        
        long existingRoomsCount = booking.getBookingRooms().stream()
                .filter(item -> item.getRoomType() != null
                        && item.getRoomType().getId().equals(request.getRoomTypeId()))
                .count();

        
        if ((existingRoomsCount + 1) > available) {
            throw new AppException(ErrorCode.ROOM_TYPE_SOLD_OUT);
        }

        
        BookingRoom newBookingRoom = BookingRoom.builder()
                .booking(booking)
                .roomType(roomType)
                .priceAtOrder(roomType.getPrice())
                .status(BookingRoomStatus.ACTIVE)
                .build();

        newBookingRoom = bookingRoomRepo.save(newBookingRoom);

        
        recalculateTotalPrice(booking.getId());

        String logDesc = String.format("Thêm phòng loại [%s] vào đơn đặt phòng #%d.",
                roomType.getName(), booking.getId());
        auditLogService.saveLog("BOOKING", "ADD_ROOM_TYPE", booking.getId(), logDesc);

        return bookingRoomMapper.toResponse(newBookingRoom);
    }

    @Override
    @Transactional
    
    public BookingRoomResponse assignRoom(Long id, Long roomId) {
        BookingRoom br = bookingRoomRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_ROOM_NOT_FOUND));

        Room newRoom = roomRepo.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        
        if (newRoom.getStatus() != RoomStatus.AVAILABLE &&
                (br.getRoom() == null || !br.getRoom().getId().equals(newRoom.getId()))) {
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        
        if (!br.getRoomType().getId().equals(newRoom.getRoomType().getId())) {
            throw new AppException(ErrorCode.INVALID_ROOM_TYPE);
        }

        
        Room oldRoom = br.getRoom();
        boolean isChangingRoom = (oldRoom != null && !oldRoom.getId().equals(newRoom.getId()));

        
        if (isChangingRoom) {
            oldRoom.setStatus(RoomStatus.AVAILABLE);
            roomRepo.save(oldRoom);
        }

        
        br.setRoom(newRoom);
        newRoom.setStatus(RoomStatus.OCCUPIED);
        roomRepo.save(newRoom);

        br = bookingRoomRepo.save(br);

        
        recalculateTotalPrice(br.getBooking().getId());

        
        
        
        if (isChangingRoom) {
            
            String logDesc = String.format("Đổi số phòng cho bản ghi phòng #%d từ phòng [%s] sang phòng [%s].",
                    id, oldRoom.getRoomNumber(), newRoom.getRoomNumber());
            auditLogService.saveLog("BOOKING", "CHANGE_ROOM_NUMBER", br.getBooking().getId(), logDesc);
        } else if (oldRoom == null) {
            
            String logDesc = String.format("Xếp số phòng [%s] cho bản ghi phòng #%d.",
                    newRoom.getRoomNumber(), id);
            auditLogService.saveLog("BOOKING", "ASSIGN_ROOM", br.getBooking().getId(), logDesc);
        }

        return bookingRoomMapper.toResponse(br);
    }


    @Transactional
    public void recalculateTotalPrice(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        
        long rawNights = java.time.temporal.ChronoUnit.DAYS.between(booking.getArrivalDate(), booking.getDepartureDate());
        final long nights = (rawNights <= 0) ? 1 : rawNights;

        
        List<BookingRoom> rooms = bookingRoomRepo.findAllByBookingId(bookingId);
        BigDecimal roomTotal = rooms.stream()
                .filter(br -> br.getStatus() != BookingRoomStatus.CANCELLED)
                .map(br -> {
                    BigDecimal price = (br.getPriceAtOrder() != null) ? br.getPriceAtOrder() : BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(nights));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        
        List<BookingServiceDetail> services = bookingServiceRepo.findAllByBookingId(bookingId); 
        BigDecimal serviceTotal = services.stream()
                .filter(bs -> bs.getStatus() != BookingServiceStatus.CANCELLED)
                .map(bs -> {
                    BigDecimal price = (bs.getPriceAtOrder() != null) ? bs.getPriceAtOrder() : BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(bs.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        
        booking.setRoomAmount(roomTotal);
        booking.setTotalAmount(roomTotal.add(serviceTotal));

        bookingRepo.save(booking);
    }

    @Override
    @Transactional
    public BookingRoomResponse changeRoomType(Long id, Long newRoomTypeId) {
        BookingRoom br = bookingRoomRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_ROOM_NOT_FOUND));

        if (br.getRoomType() != null && br.getRoomType().getId().equals(newRoomTypeId)) {
            return bookingRoomMapper.toResponse(br);
        }

        
        long available = roomTypeService.getAvailableCount(
                newRoomTypeId,
                br.getBooking().getArrivalDate(),
                br.getBooking().getDepartureDate(),
                br.getBooking().getId()
        );

        long myExistingRooms = br.getBooking().getBookingRooms().stream()
                .filter(item -> item.getRoomType() != null
                        && item.getRoomType().getId().equals(newRoomTypeId)
                        && !item.getId().equals(id))
                .count();

        if ((myExistingRooms + 1) > available) {
            throw new AppException(ErrorCode.ROOM_TYPE_SOLD_OUT);
        }

        
        RoomType newRoomType = roomTypeRepo.findById(newRoomTypeId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        br.setRoomType(newRoomType);
        br.setPriceAtOrder(newRoomType.getPrice());
        bookingRoomRepo.save(br); 

        
        
        recalculateTotalPrice(br.getBooking().getId());

        String logDesc = String.format("Đổi loại phòng cho bản ghi #%d sang loại ID: %d.", id, newRoomTypeId);
        auditLogService.saveLog("BOOKING", "CHANGE_ROOM_TYPE", br.getBooking().getId(), logDesc);

        return bookingRoomMapper.toResponse(br);
    }



    private void validateDates(LocalDateTime in, LocalDateTime out) {
        if (out.isBefore(in) || out.isEqual(in)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private void validateBookingStatus(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }
    }

}