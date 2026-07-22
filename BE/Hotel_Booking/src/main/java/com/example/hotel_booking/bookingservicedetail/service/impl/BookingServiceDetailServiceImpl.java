package com.example.hotel_booking.bookingservicedetail.service.impl;

import com.example.hotel_booking.audit.annotation.LogAction;
import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.booking.repository.BookingRepository;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceCreateRequest;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceUpdateRequest;
import com.example.hotel_booking.bookingservicedetail.entity.BookingServiceDetail;
import com.example.hotel_booking.bookingservicedetail.mapper.BookingServiceDetailMapper;
import com.example.hotel_booking.bookingservicedetail.repository.BookingServiceDetailRepository;
import com.example.hotel_booking.bookingservicedetail.service.BookingServiceDetailService;
import com.example.hotel_booking.common.BookingServiceStatus;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.service.entity.ExtraService;
import com.example.hotel_booking.service.repository.ExtraServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceDetailServiceImpl implements BookingServiceDetailService {

    private final BookingServiceDetailRepository detailRepo;
    private final BookingRepository bookingRepo;
    private final ExtraServiceRepository extraServiceRepo;
    private final BookingServiceDetailMapper detailMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public BookingServiceDetailResponse addService(BookingServiceCreateRequest request) {
        
        Booking booking = findAndValidateBooking(request.getBookingId());

        
        Optional<BookingServiceDetail> existingDetailOpt = detailRepo.findByBookingIdAndExtraServiceIdAndStatus(
                request.getBookingId(),
                request.getServiceId(),
                BookingServiceStatus.ACTIVE
        );

        BookingServiceDetail detail;

        if (existingDetailOpt.isPresent()) {
            
            detail = existingDetailOpt.get();
            detail.setQuantity(detail.getQuantity() + request.getQuantity());
            
            
        } else {
            
            ExtraService service = extraServiceRepo.findById(request.getServiceId())
                    .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

            detail = detailMapper.toEntity(request, booking, service);
            detail.setStatus(BookingServiceStatus.ACTIVE);
        }

        
        detail = detailRepo.save(detail);

        
        updateBookingTotalMoney(booking);

        String logDesc = String.format("Thêm dịch vụ [%s] (Số lượng: %d) vào đơn đặt phòng #%d.",
                detail.getExtraService().getName(), request.getQuantity(), booking.getId());
        auditLogService.saveLog("BOOKING", "ADD_SERVICE", booking.getId(), logDesc);

        return detailMapper.toResponse(detail);
    }

    @Override
    @Transactional
    @LogAction(module = "BOOKING", action = "UPDATE_SERVICE", targetId = "#id", entityClass = BookingServiceDetail.class, resolveParent = true)
    public BookingServiceDetailResponse updateService(Long id, BookingServiceUpdateRequest request) {
        BookingServiceDetail detail = detailRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_DETAIL_NOT_FOUND));

        
        if (detail.getStatus() == BookingServiceStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }

        findAndValidateBooking(detail.getBooking().getId());

        detail.setQuantity(request.getQuantity());
        detail = detailRepo.save(detail);

        updateBookingTotalMoney(detail.getBooking());
        return detailMapper.toResponse(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) 
    public BookingServiceDetailResponse cancelService(Long id) {
        BookingServiceDetail detail = detailRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_DETAIL_NOT_FOUND));

        
        if (BookingServiceStatus.CANCELLED.equals(detail.getStatus())) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS, "Dịch vụ đã được hủy trước đó.");
        }

        findAndValidateBooking(detail.getBooking().getId());

        
        detail.setStatus(BookingServiceStatus.CANCELLED);
        detail = detailRepo.save(detail);

        
        updateBookingTotalMoney(detail.getBooking());

        String logDesc = String.format("Hủy dịch vụ [%s] khỏi đơn đặt phòng #%d.",
                detail.getExtraService().getName(), detail.getBooking().getId());
        auditLogService.saveLog("BOOKING", "CANCEL_SERVICE", detail.getBooking().getId(), logDesc);

        return detailMapper.toResponse(detail);
    }

    private Booking findAndValidateBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }
        return booking;
    }

    private void updateBookingTotalMoney(Booking booking) {
        List<BookingServiceDetail> details = detailRepo.findAllByBookingId(booking.getId());

        
        
        BigDecimal totalService = details.stream()
                .filter(d -> d.getStatus() != BookingServiceStatus.CANCELLED) 
                .map(d -> d.getPriceAtOrder().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        booking.setServiceAmount(totalService);

        BigDecimal deposit = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal finalTotal = booking.getRoomAmount().add(totalService).subtract(deposit);

        booking.setTotalAmount(finalTotal);
        bookingRepo.save(booking);
    }

    @Override
    public List<BookingServiceDetailResponse> getByBookingId(Long bookingId) {
        
        
        return detailRepo.findAllByBookingId(bookingId).stream()

                .map(detailMapper::toResponse)

                .collect(Collectors.toList());
    }
}