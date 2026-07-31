package com.example.hotel_booking.booking.service.impl;

import com.example.hotel_booking.audit.annotation.LogAction;
import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.booking.dto.*;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.booking.mapper.BookingMapper;
import com.example.hotel_booking.booking.repository.BookingRepository;
import com.example.hotel_booking.booking.repository.BookingSpecifications;
import com.example.hotel_booking.booking.service.BookingService;
import com.example.hotel_booking.bookingroom.entity.BookingRoom;
import com.example.hotel_booking.bookingroom.repository.BookingRoomRepository;
import com.example.hotel_booking.bookingservicedetail.entity.BookingServiceDetail;
import com.example.hotel_booking.bookingservicedetail.repository.BookingServiceDetailRepository;
import com.example.hotel_booking.common.*;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.common.service.VnPayService;
import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.customer.repository.CustomerRepository;
import com.example.hotel_booking.payment.entity.Payment;
import com.example.hotel_booking.payment.mapper.PaymentMapper;
import com.example.hotel_booking.payment.repository.PaymentRepository;
import com.example.hotel_booking.room.entity.Room;
import com.example.hotel_booking.room.repository.RoomRepository;
import com.example.hotel_booking.roomtype.entity.RoomType;
import com.example.hotel_booking.roomtype.repository.RoomTypeRepository;
import com.example.hotel_booking.service.entity.ExtraService;
import com.example.hotel_booking.service.repository.ExtraServiceRepository;
import com.example.hotel_booking.user.entity.User;
import com.example.hotel_booking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final BookingRoomRepository bookingRoomRepo;
    private final RoomRepository roomRepo;
    private final RoomTypeRepository roomTypeRepo;
    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;
    private final ExtraServiceRepository extraServiceRepo;
    private final BookingServiceDetailRepository bookingServiceDetailRepo;
    private final PaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;
    private final PaymentRepository paymentRepo;
    private final PaymentMapper paymentMapper;
    private final VnPayService vnPayService;
    private final AuditLogService auditLogService;

    @Value("${app.vnpay.simulation:false}")
    private boolean isVnPaySimulation;

    private BookingResponse enrichWithRefundAmount(BookingResponse response) {

        if (response.getBookingRooms() != null) {
            response.setBookingRooms(
                    response.getBookingRooms()
                            .stream()

                            .toList()
            );
        }

        List<Payment> payments =
                paymentRepo.findByBookingId(response.getId());

        BigDecimal refundAmount = payments.stream()
                .filter(p -> p.getPaymentType() == PaymentType.REFUND)
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setRefundAmount(refundAmount);

        payments.stream()
                .filter(p ->
                        p.getPaymentType() == PaymentType.DEPOSIT
                                || p.getPaymentType() == PaymentType.FINAL_PAYMENT)
                .findFirst()
                .ifPresent(p ->
                        response.setPaymentMethod(p.getMethod().name())
                );

        payments.stream()
                .filter(p -> p.getPaymentType() == PaymentType.REFUND)
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .findFirst()
                .ifPresent(p ->
                        response.setRefundMethod(p.getMethod().name())
                );



        return response;
    }

    private BookingResponse enrichWithRefundAmount(BookingResponse response, List<Payment> payments) {
        if (response.getBookingRooms() != null) {
            response.setBookingRooms(
                    response.getBookingRooms()
                            .stream()
                            .toList()
            );
        }

        BigDecimal refundAmount = payments.stream()
                .filter(p -> p.getPaymentType() == PaymentType.REFUND)
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setRefundAmount(refundAmount);

        payments.stream()
                .filter(p -> p.getPaymentType() == PaymentType.DEPOSIT || p.getPaymentType() == PaymentType.FINAL_PAYMENT)
                .findFirst()
                .ifPresent(p -> response.setPaymentMethod(p.getMethod().name()));

        payments.stream()
                .filter(p -> p.getPaymentType() == PaymentType.REFUND && p.getStatus() == PaymentStatus.SUCCESS)
                .findFirst()
                .ifPresent(p -> response.setRefundMethod(p.getMethod().name()));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Optional<Customer> customerOpt = customerRepo.findByUserId(user.getId());

        if (customerOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Customer customer = customerOpt.get();

        // 1. Lấy danh sách Bookings
        List<Booking> bookings = bookingRepo.findByCustomerIdWithRooms(customer.getId());
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Map sang DTO trước
        List<BookingResponse> responses = bookings.stream()
                .map(bookingMapper::toBookingResponse)
                .toList();

        // 3. Gom danh sách ID
        List<Long> bookingIds = responses.stream()
                .map(BookingResponse::getId)
                .toList();

        // 4. Query đúng 1 CÂU SQL cho toàn bộ Payment của các đơn này
        List<Payment> allPayments = paymentRepo.findByBookingIdIn(bookingIds);

        // 5. Gom nhóm Payment theo bookingId trên RAM
        Map<Long, List<Payment>> paymentMap = allPayments.stream()
                .collect(Collectors.groupingBy(p -> p.getBooking().getId()));

        // 6. Enrich bằng hàm 2 tham số (KHÔNG BẮN THÊM SQL)
        responses.forEach(response -> {
            List<Payment> payments = paymentMap.getOrDefault(response.getId(), Collections.emptyList());
            enrichWithRefundAmount(response, payments);
        });

        return responses;
    }

    @Override
    public BookingResponse getBookingByPhoneAndCode(String phone, String bookingCode) {
        return bookingRepo.findByPhoneAndBookingCode(phone, bookingCode)
                .map(bookingMapper::toBookingResponse)
                .map(this::enrichWithRefundAmount)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {

        // 1. Validate ngày tháng
        LocalDateTime nowInVietnam = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        if (request.getCheckIn().isBefore(nowInVietnam)) {
            throw new AppException(ErrorCode.CHECKIN_DATE_PAST);
        }

        if (request.getCheckOut().isBefore(request.getCheckIn())
                || request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new AppException(ErrorCode.INVALID_BOOKING_DATES);
        }

        // 2. Xác định thông tin Khách hàng
        Customer customer = resolveCustomer(request);

        if (request.getContactName() == null || request.getContactName().isBlank()) {
            request.setContactName(customer.getFullName());
        }

        if (request.getContactPhone() == null || request.getContactPhone().isBlank()) {
            request.setContactPhone(customer.getPhoneNumber());
        }

        // 3. Gom nhóm và Validate Loại phòng
        if (request.getRoomTypes() == null || request.getRoomTypes().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_ROOM_TYPE);
        }

        Map<Long, Integer> requestQtyMap = request.getRoomTypes()
                .stream()
                .collect(Collectors.toMap(
                        BookingRoomRequest::getRoomTypeId,
                        BookingRoomRequest::getQuantity,
                        Integer::sum
                ));

        List<Long> roomTypeIds = new ArrayList<>(requestQtyMap.keySet());
        List<RoomType> roomTypes = roomTypeRepo.findAllById(roomTypeIds);

        if (roomTypes.size() != roomTypeIds.size()) {
            throw new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND);
        }

        // 4. Tính số đêm
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                request.getCheckIn().toLocalDate(),
                request.getCheckOut().toLocalDate()
        );
        if (nights < 1) nights = 1;

        // 5. Kiểm tra phòng trống (CÓ LOCK CHỐNG OVERBOOKING) & Tính tiền phòng
        BigDecimal totalRoomAmount = BigDecimal.ZERO;
        List<BookingRoom> bookingRooms = new ArrayList<>();

        for (RoomType type : roomTypes) {
            long requestedQty = requestQtyMap.getOrDefault(type.getId(), 0);

            if (requestedQty <= 0) {
                throw new AppException(ErrorCode.INVALID_ROOM_TYPE);
            }

            long bookedRooms = bookingRoomRepo.countBookedByRoomType(
                    type.getId(),
                    request.getCheckIn(),
                    request.getCheckOut()
            );

            long totalRooms = roomRepo.countByRoomTypeIdAndActiveTrue(type.getId());
            long availableRooms = totalRooms - bookedRooms;

            if (availableRooms < requestedQty) {
                throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
            }

            // Cộng tiền phòng
            totalRoomAmount = totalRoomAmount.add(
                    type.getPrice()
                            .multiply(BigDecimal.valueOf(nights))
                            .multiply(BigDecimal.valueOf(requestedQty))
            );

            // Tạo danh sách BookingRoom
            for (int i = 0; i < requestedQty; i++) {
                bookingRooms.add(bookingMapper.toBookingRoom(type, null));
            }
        }

        // 6. Tính tiền Dịch vụ đi kèm (nếu có)
        BigDecimal totalServiceAmount = BigDecimal.ZERO;
        List<BookingServiceDetail> serviceDetails = new ArrayList<>();

        if (request.getServices() != null && !request.getServices().isEmpty()) {
            for (BookingServiceRequest serviceReq : request.getServices()) {
                if (serviceReq.getServiceId() == null || serviceReq.getQuantity() == null || serviceReq.getQuantity() <= 0) {
                    continue;
                }

                ExtraService service = extraServiceRepo.findById(serviceReq.getServiceId())
                        .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

                BigDecimal itemTotal = service.getPrice().multiply(BigDecimal.valueOf(serviceReq.getQuantity()));
                totalServiceAmount = totalServiceAmount.add(itemTotal);

                serviceDetails.add(
                        BookingServiceDetail.builder()
                                .extraService(service)
                                .priceAtOrder(service.getPrice())
                                .quantity(serviceReq.getQuantity())
                                .status(BookingServiceStatus.ACTIVE)
                                .build()
                );
            }
        }

        // 7. Tính tổng tiền & Tiền cọc
        BigDecimal totalAmount = totalRoomAmount.add(totalServiceAmount);
        BigDecimal deposit = totalAmount.multiply(BigDecimal.valueOf(0.4));

        // 8. Lưu đơn Đặt phòng
        Booking booking = bookingMapper.toBooking(
                request, customer, totalRoomAmount, totalServiceAmount, totalAmount, deposit
        );

        Booking savedBooking = bookingRepo.save(booking);

        // Gán mã đơn (Tự động UPDATE nhờ Hibernate Dirty Checking, KHÔNG CẦN gọi save() lại)
        savedBooking.setBookingCode("BOOK" + savedBooking.getId());

        // 9. Lưu các bảng phụ thuộc
        bookingRooms.forEach(br -> br.setBooking(savedBooking));
        bookingRoomRepo.saveAll(bookingRooms);

        if (!serviceDetails.isEmpty()) {
            serviceDetails.forEach(sd -> sd.setBooking(savedBooking));
            bookingServiceDetailRepo.saveAll(serviceDetails);
        }

        // 10. Xử lý Thanh toán
        BookingResponse response = bookingMapper.toBookingResponse(savedBooking);

        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            confirmDeposit(savedBooking.getId(), PaymentMethod.CASH, null);

            String logDesc = String.format(
                    "Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: %s. Tổng tiền: %,.0fđ. Đã thu cọc (40%%): %,.0fđ",
                    savedBooking.getBookingCode(),
                    savedBooking.getTotalAmount().doubleValue(),
                    savedBooking.getDepositAmount().doubleValue()
            );

            auditLogService.saveLog("BOOKING", "CREATE_BOOKING_CASH", savedBooking.getId(), logDesc);

            // ⭐ Bỏ truy vấn findById thừa, map trực tiếp từ savedBooking đã cập nhật
            response = bookingMapper.toBookingResponse(savedBooking);
            response.setPaymentUrl(null);

        } else {
            String paymentUrl = vnPayService.createPaymentUrl(
                    savedBooking,
                    savedBooking.getDepositAmount(),
                    PaymentType.DEPOSIT,
                    "127.0.0.1",
                    "Thanh_toan_coc_don_" + savedBooking.getId()
            );

            response.setPaymentUrl(paymentUrl);
        }

        return response;
    }

    private Customer resolveCustomer(BookingCreateRequest request) {

        if (request.getCustomerId() != null) {
            return customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() ->
                            new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        }

        return customerRepo.findByPhoneNumber(request.getPhoneNumber())
                .map(existing -> {
                    if (!existing.getFullName().equals(request.getFullName())) {
                        existing.setFullName(request.getFullName());
                        return customerRepo.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() ->
                        customerRepo.save(
                                Customer.builder()
                                        .fullName(request.getFullName())
                                        .phoneNumber(request.getPhoneNumber())
                                        .active(true)
                                        .build()
                        )
                );
    }


    @Override
    public Page<BookingResponse> getAllBookings(String code, BookingStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        Specification<Booking> spec = Specification.where(BookingSpecifications.hasBookingCode(code))
                .and(BookingSpecifications.hasStatus(status))
                .and(BookingSpecifications.hasDateRange(start, end));

        // 1. Phân trang Booking
        Page<Booking> bookingPage = bookingRepo.findAll(spec, pageable);

        // 2. Chuyển sang DTO Page
        Page<BookingResponse> responsePage = bookingPage.map(bookingMapper::toBookingResponse);

        // 3. Gom ID của các booking trong trang hiện tại
        List<Long> bookingIds = responsePage.getContent().stream()
                .map(BookingResponse::getId)
                .toList();

        if (bookingIds.isEmpty()) {
            return responsePage;
        }

        // 4. Query 1 LẦN DUY NHẤT tất cả payments của danh sách bookingIds
        List<Payment> allPayments = paymentRepo.findByBookingIdIn(bookingIds);

        // 5. Gom nhóm Payment theo bookingId bằng Map
        Map<Long, List<Payment>> paymentMap = allPayments.stream()
                .collect(Collectors.groupingBy(p -> p.getBooking().getId()));

        // 6. Gọi hàm enrichWithRefundAmount
        responsePage.getContent().forEach(response -> {
            List<Payment> paymentsOfBooking = paymentMap.getOrDefault(response.getId(), Collections.emptyList());
            enrichWithRefundAmount(response, paymentsOfBooking);
        });

        return responsePage;
    }

    @Override
    public BookingResponse getBookingById(Long id) {

        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        return enrichWithRefundAmount(
                bookingMapper.toBookingResponse(booking)
        );
    }


    private void releaseRooms(Booking booking) {
        if (booking.getBookingRooms() != null) {
            booking.getBookingRooms().forEach(br -> {


                if (br.getStatus() != BookingRoomStatus.CANCELLED && br.getRoom() != null) {
                    br.getRoom().setStatus(RoomStatus.AVAILABLE);
                    roomRepo.save(br.getRoom());
                }
            });
        }
    }

    @Override
    @Transactional
    public BookingResponse confirmDeposit(Long bookingId, PaymentMethod method, String txnId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new AppException(ErrorCode.BOOKING_ALREADY_CANCELLED);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setExpiryDate(null);

        if (booking.getBookingRooms() != null) {
            booking.getBookingRooms().forEach(br -> {
                if (br.getRoom() != null) {

                    br.getRoom().setStatus(RoomStatus.OCCUPIED);
                }
            });
        }

        String finalTxnId = (txnId == null || txnId.isBlank())
                ? "CASH-" + System.currentTimeMillis()
                : txnId;

        Payment payment = paymentMapper.toDepositPayment(booking, method, PaymentStatus.SUCCESS, finalTxnId);
        payment.setPaymentType(PaymentType.DEPOSIT);
        paymentRepo.save(payment);

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse processVnPayCallback(Map<String, String> vnpParams) {
        String responseCode = vnpParams.get("vnp_ResponseCode");
        String txnRef = vnpParams.get("vnp_TxnRef");
        String transactionNo = vnpParams.get("vnp_TransactionNo");
        String orderInfo = vnpParams.get("vnp_OrderInfo");


        if (orderInfo != null && orderInfo.startsWith("Thanh_toan_coc_don_")) {
            try {
                Long bookingId = Long.parseLong(orderInfo.replace("Thanh_toan_coc_don_", ""));
                Booking booking = bookingRepo.findById(bookingId)
                        .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
                return bookingMapper.toBookingResponse(booking);
            } catch (Exception e) {
                log.error("Lỗi khi trích xuất bookingId từ orderInfo: {}", e.getMessage());
            }
        }


        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionNo);


        if (paymentOpt.isEmpty()) {
            paymentOpt = paymentRepository.findByTransactionId(txnRef);
        }


        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();

            if (payment.getStatus() != PaymentStatus.SUCCESS && "00".equals(responseCode)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);
            }

            return bookingMapper.toBookingResponse(payment.getBooking());
        }


        throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    @Override
    @Transactional
    @LogAction(module = "BOOKING", action = "CHECK_IN", targetId = "#bookingId", entityClass = Booking.class)
    public BookingResponse checkIn(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));


        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS);
        }

        if (booking.getBookingRooms() == null || booking.getBookingRooms().isEmpty()) {
            throw new AppException(ErrorCode.ROOM_NOT_ASSIGNED);
        }


        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setActualCheckIn(LocalDateTime.now());
        bookingRepo.save(booking);


        if (booking.getBookingRooms() != null) {
            booking.getBookingRooms().forEach(bookingRoom -> {
                Room room = bookingRoom.getRoom();
                if (room != null) {
                    room.setStatus(RoomStatus.OCCUPIED);
                    roomRepo.save(room);
                }
            });
        }



        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public CheckoutPreviewResponse previewCheckout(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment vnpayPayment = getVnpayPayment(bookingId);
        boolean originalVnpay = vnpayPayment != null;


        BigDecimal roomAmount = booking.getRoomAmount() != null ? booking.getRoomAmount() : BigDecimal.ZERO;
        BigDecimal serviceAmount = booking.getServiceAmount() != null ? booking.getServiceAmount() : BigDecimal.ZERO;
        BigDecimal surchargeAmount = calculateLateCheckoutSurcharge(booking);
        BigDecimal depositAmount = booking.getDepositAmount() != null ? booking.getDepositAmount() : BigDecimal.ZERO;

        BigDecimal totalAmount = roomAmount.add(serviceAmount).add(surchargeAmount);


        BigDecimal diff = totalAmount.subtract(depositAmount);

        BigDecimal remainingAmount = BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;

        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            remainingAmount = diff;
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            refundAmount = diff.abs();
        }


        return CheckoutPreviewResponse.builder()
                .bookingId(booking.getId())
                .roomAmount(roomAmount)
                .serviceAmount(serviceAmount)
                .depositAmount(depositAmount)
                .surchargeAmount(surchargeAmount)
                .totalAmount(totalAmount)
                .remainingAmount(remainingAmount != null ? remainingAmount : BigDecimal.ZERO)
                .refundAmount(refundAmount != null ? refundAmount : BigDecimal.ZERO)
                .originalVnpay(originalVnpay)
                .build();
    }

    @Override
    @Transactional
    @LogAction(module = "BOOKING", action = "CHECK_OUT", targetId = "#bookingId", entityClass = Booking.class)
    public CheckoutResponse checkOut(Long bookingId, String paymentMethod, String refundMethod, String ipAddress) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new AppException(ErrorCode.CHECKIN_REQUIRED);
        }




        BigDecimal surcharge = calculateLateCheckoutSurcharge(booking);

        BigDecimal roomAmount =
                booking.getRoomAmount() != null
                        ? booking.getRoomAmount()
                        : BigDecimal.ZERO;

        BigDecimal serviceAmount =
                booking.getServiceAmount() != null
                        ? booking.getServiceAmount()
                        : BigDecimal.ZERO;

        BigDecimal depositAmount =
                booking.getDepositAmount() != null
                        ? booking.getDepositAmount()
                        : BigDecimal.ZERO;

        BigDecimal totalAmount =
                roomAmount.add(serviceAmount).add(surcharge);

        BigDecimal remainingAmount =
                totalAmount.subtract(depositAmount);

        booking.setActualCheckOut(LocalDateTime.now());
        booking.setSurchargeAmount(surcharge);
        booking.setTotalAmount(totalAmount);

        CheckoutResponse response = bookingMapper.toCheckoutResponse(booking);
        response.setSurchargeAmount(surcharge);




        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {

            response.setRemainingAmount(remainingAmount);
            response.setRefundAmount(BigDecimal.ZERO);

            if ("VNPAY".equalsIgnoreCase(paymentMethod)) {

                String paymentUrl =
                        vnPayService.createPaymentUrl(
                                booking,
                                remainingAmount,
                                PaymentType.FINAL_PAYMENT,
                                ipAddress,
                                "PAY_REMAINING_BOOKING_" + booking.getId()
                        );

                response.setPaymentUrl(paymentUrl);

            } else {

























                Payment payment = paymentMapper.toFinalPayment(booking, remainingAmount, PaymentMethod.valueOf(paymentMethod.toUpperCase()));
                paymentRepo.save(payment);
                closeBookingAndCleanRooms(booking);
            }
        }




        else if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {

            BigDecimal refundAmount = remainingAmount.abs();

            response.setRemainingAmount(BigDecimal.ZERO);
            response.setRefundAmount(refundAmount);

            Payment vnpayPayment = getVnpayPayment(bookingId);

            boolean isOriginalVnpay = vnpayPayment != null;

            if (isOriginalVnpay) {

                PaymentStatus refundStatus =
                        performVnPayRefund(
                                booking,
                                vnpayPayment,
                                refundAmount,
                                "Refund checkout excess"
                        );

                if (refundStatus == PaymentStatus.SUCCESS) {

                    closeBookingAndCleanRooms(booking);

                } else {

                    booking.setStatus(BookingStatus.PENDING_REFUND);

                    saveRefundPayment(
                            booking,
                            refundAmount,
                            PaymentMethod.VNPAY,
                            PaymentStatus.PENDING,
                            "RFND-VNPAY-" + System.currentTimeMillis()
                    );
                }

            } else {

                booking.setStatus(BookingStatus.PENDING_REFUND);

                PaymentMethod method =
                        refundMethod != null
                                ? PaymentMethod.valueOf(refundMethod.toUpperCase())
                                : PaymentMethod.CASH;

                Payment refundPayment = Payment.builder()
                        .booking(booking)
                        .amount(refundAmount)
                        .paymentType(PaymentType.REFUND)
                        .method(method)
                        .status(PaymentStatus.PENDING)
                        .paidAt(LocalDateTime.now())
                        .transactionId(
                                "RFND-CHECKOUT-" + System.currentTimeMillis()
                        )
                        .build();

                paymentRepo.save(refundPayment);

                cleanRooms(booking);
            }
        }




        else {

            response.setRemainingAmount(BigDecimal.ZERO);
            response.setRefundAmount(BigDecimal.ZERO);

            closeBookingAndCleanRooms(booking);
        }

        bookingRepo.save(booking);

        return response;

    }


    private void closeBookingAndCleanRooms(Booking booking) {
        booking.setStatus(BookingStatus.CHECKED_OUT);
        cleanRooms(booking);
    }

    private void cleanRooms(Booking booking) {
        booking.getBookingRooms().forEach(br -> {
            if (br.getRoom() != null) {
                br.getRoom().setStatus(RoomStatus.CLEANING);
                roomRepo.save(br.getRoom());
            }
        });
    }

    @Override
    @Transactional
    public void finalizeCheckoutVnPay(Long bookingId, String transactionNo, String amount) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            return;
        }


        Payment payment = paymentRepo.findByBookingId(bookingId)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .filter(p -> p.getPaymentType() == PaymentType.FINAL_PAYMENT)
                .filter(p -> p.getMethod() == PaymentMethod.VNPAY)
                .reduce((first, second) -> second)
                .orElse(null);

        if (payment != null) {

            BigDecimal finalAmount = new BigDecimal(amount)
                    .divide(new BigDecimal(100));

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setAmount(finalAmount);
            payment.setTransactionId(transactionNo);

            paymentRepo.save(payment);
        }


        booking.setStatus(BookingStatus.CHECKED_OUT);

        if (booking.getBookingRooms() != null) {
            booking.getBookingRooms().forEach(br -> {
                if (br.getRoom() != null) {
                    br.getRoom().setStatus(RoomStatus.CLEANING);
                    roomRepo.save(br.getRoom());
                }
            });
        }

        bookingRepo.save(booking);
    }


    private BigDecimal calculateLateCheckoutSurcharge(Booking booking) {

        LocalDateTime plannedDeparture = booking.getDepartureDate();

        if (plannedDeparture == null) {
            return BigDecimal.ZERO;
        }

        LocalDateTime now = LocalDateTime.now();

        if (!now.isAfter(plannedDeparture)) {
            return BigDecimal.ZERO;
        }

        long lateMinutes =
                java.time.temporal.ChronoUnit.MINUTES
                        .between(plannedDeparture, now);


        if (lateMinutes <= 30) {
            return BigDecimal.ZERO;
        }

        BigDecimal roomPrice =
                booking.getRoomAmount() != null
                        ? booking.getRoomAmount()
                        : BigDecimal.ZERO;


        if (lateMinutes <= 180) {
            return roomPrice.multiply(new BigDecimal("0.3"));
        }


        if (lateMinutes <= 360) {
            return roomPrice.multiply(new BigDecimal("0.5"));
        }


        return roomPrice;
    }

    private BigDecimal calculateRefundAmount(BigDecimal amount, java.time.LocalDate arrivalDate) {
        if (amount == null || arrivalDate == null) {
            return BigDecimal.ZERO;
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(today, arrivalDate);

        if (daysBetween >= 7) {
            return amount;
        } else if (daysBetween >= 3) {
            return amount.multiply(new BigDecimal("0.5"));
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getAlreadyRefundedAmount(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId)
                .stream()
                .filter(p -> p.getPaymentType() == PaymentType.REFUND)

                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS || p.getStatus() == PaymentStatus.PENDING)
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long getNights(Booking booking) {
        if (booking.getArrivalDate() == null || booking.getDepartureDate() == null) {
            return 1;
        }
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                booking.getArrivalDate().toLocalDate(),
                booking.getDepartureDate().toLocalDate()
        );

        return (nights < 1) ? 1 : nights;
    }

    private BigDecimal getTotalPaid(Long bookingId) {

        return paymentRepo.findByBookingId(bookingId)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .filter(p ->
                        p.getPaymentType() == PaymentType.DEPOSIT
                                || p.getPaymentType() == PaymentType.FINAL_PAYMENT
                )
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    private Payment getVnpayPayment(Long bookingId) {

        return paymentRepo.findByBookingId(bookingId)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .filter(p -> p.getMethod() == PaymentMethod.VNPAY)
                .filter(p ->
                        p.getPaymentType() == PaymentType.DEPOSIT
                                || p.getPaymentType() == PaymentType.FINAL_PAYMENT
                )
                .findFirst()
                .orElse(null);

    }


    private String executeVnPayRefund(
            Booking booking,
            Payment payment,
            BigDecimal amount,
            String type,
            String note
    ) {

        if (payment == null || payment.getTransactionId() == null) {
            throw new AppException(
                    ErrorCode.PAYMENT_NOT_FOUND,
                    "Không tìm thấy giao dịch gốc."
            );
        }

        Map<String, Object> result = vnPayService.refund(
                type,
                payment.getTransactionId(),
                amount,
                payment.getTransactionId(),
                payment.getPaidAt(),
                "ADMIN_SYSTEM",
                note,
                "127.0.0.1"
        );

        String responseCode = (String) result.get("vnp_ResponseCode");

        if (!"00".equals(responseCode)) {
            throw new AppException(
                    ErrorCode.REFUND_FAILED,
                    "VNPay từ chối hoàn tiền. Mã lỗi: " + responseCode
            );
        }

        return (String) result.get("vnp_TransactionNo");
    }


    private void saveRefundPayment(Booking booking, BigDecimal amount, PaymentMethod method, PaymentStatus status, String transactionId) {
        paymentRepo.save(Payment.builder()
                .booking(booking)
                .amount(amount)
                .paymentType(PaymentType.REFUND)
                .method(method)
                .status(status)
                .paidAt(status == PaymentStatus.SUCCESS ? LocalDateTime.now() : null)
                .transactionId(transactionId)
                .build());
    }


    private PaymentStatus performVnPayRefund(Booking booking, Payment payment, BigDecimal amount, String note) {
        String txId;
        PaymentStatus status;


        try {
            if (isVnPaySimulation) {
                txId = "MOCK_VNP_REFUND_" + System.currentTimeMillis();
                status = PaymentStatus.SUCCESS;
            } else {

                txId = executeVnPayRefund(booking, payment, amount, "03", note);
                status = PaymentStatus.SUCCESS;
            }
        } catch (Exception e) {

            txId = "ERR_VNPAY_" + System.currentTimeMillis();
            status = PaymentStatus.PENDING;

            log.error("Lỗi hoàn tiền VNPay: BookingId={}, Error={}", booking.getId(), e.getMessage());
        }


        saveRefundPayment(booking, amount, PaymentMethod.VNPAY, status, txId);


        return status;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingCancelResponse cancelSingleRoom(Long bookingId, Long bookingRoomId, String refundMethod, String reason) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        BookingRoom bookingRoom = bookingRoomRepo.findById(bookingRoomId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_ROOM_NOT_FOUND));

        if (!bookingRoom.getBooking().getId().equals(bookingId)) {
            throw new AppException(ErrorCode.BOOKING_ROOM_NOT_FOUND, "Phòng không thuộc đơn đặt này.");
        }

        if (bookingRoom.getStatus() == BookingRoomStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS, "Phòng này đã được hủy.");
        }

        if (booking.getArrivalDate().toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS, "Không thể hủy phòng thuộc đơn đặt phòng trong quá khứ.");
        }

        long nights = getNights(booking);
        boolean allCancelled = booking.getBookingRooms().stream().allMatch(br -> br.getStatus() == BookingRoomStatus.CANCELLED || br.getId().equals(bookingRoomId));
        BigDecimal newRoomAmount = booking.getBookingRooms().stream().filter(br -> !br.getId().equals(bookingRoomId)).filter(br -> br.getStatus() != BookingRoomStatus.CANCELLED).map(br -> br.getPriceAtOrder().multiply(BigDecimal.valueOf(nights))).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingCost = allCancelled ? BigDecimal.ZERO : newRoomAmount.add(Optional.ofNullable(booking.getServiceAmount()).orElse(BigDecimal.ZERO)).add(Optional.ofNullable(booking.getSurchargeAmount()).orElse(BigDecimal.ZERO));

        Payment vnpayPayment = getVnpayPayment(bookingId);
        List<Payment> payments = paymentRepo.findByBookingId(bookingId);
        BigDecimal totalPaid = getTotalPaid(bookingId);
        BigDecimal totalRefunded = (vnpayPayment != null)
                ? payments.stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS && !p.getId().equals(vnpayPayment.getId())).map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                : payments.stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS && p.getTransactionId() != null && p.getTransactionId().startsWith("RFND-")).map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refundBase = totalPaid.subtract(totalRefunded).compareTo(remainingCost) > 0 ? totalPaid.subtract(totalRefunded).subtract(remainingCost) : BigDecimal.ZERO;
        BigDecimal refundAmount = calculateRefundAmount(refundBase, booking.getArrivalDate().toLocalDate());

        PaymentStatus refundStatus = PaymentStatus.SUCCESS;
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (vnpayPayment != null) {
                refundStatus = performVnPayRefund(booking, vnpayPayment, refundAmount, "Refund room " + bookingRoomId);
            } else if (allCancelled) {
                saveRefundPayment(booking, refundAmount, "BANK_TRANSFER".equals(refundMethod) ? PaymentMethod.BANK_TRANSFER : PaymentMethod.CASH, PaymentStatus.PENDING, "RFND-MANUAL-" + System.currentTimeMillis());
                refundStatus = PaymentStatus.PENDING;
            }
        }

        bookingRoom.setCancelReason(reason != null && !reason.trim().isEmpty() ? reason : "Khách hủy lẻ phòng trên website");
        bookingRoom.setStatus(BookingRoomStatus.CANCELLED);
        if (bookingRoom.getRoom() != null) bookingRoom.getRoom().setStatus(RoomStatus.AVAILABLE);

        booking.setRoomAmount(newRoomAmount);
        booking.setTotalAmount(allCancelled ? BigDecimal.ZERO : remainingCost);
        if (allCancelled) {
            booking.setCancelReason("Hủy đơn do hủy phòng" + (booking.getBookingRooms().stream().filter(br -> br.getStatus() == BookingRoomStatus.CANCELLED).count() > 1 ? " lần lượt" : " duy nhất"));
            booking.setStatus(refundStatus == PaymentStatus.SUCCESS ? BookingStatus.CANCELLED : BookingStatus.PENDING_REFUND);


            if (booking.getBookingServices() != null) {
                booking.getBookingServices().forEach(bs -> {
                    bs.setStatus(BookingServiceStatus.CANCELLED);
                });
            }
            booking.setServiceAmount(BigDecimal.ZERO);
        }
        bookingRepo.save(booking);

        String logDesc = String.format("Hủy phòng (%s) trong đơn. Lý do: %s. Hình thức hoàn: %s",
                bookingRoom.getRoomType().getName(), reason, refundMethod);
        auditLogService.saveLog("BOOKING", "CANCEL_ROOM_ITEM", bookingId, logDesc);

        return BookingCancelResponse.builder().status("SUCCESS").message(allCancelled ? "Đã chuyển sang chờ hoàn tiền cọc!" : "Hủy phòng thành công.").build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogAction(module = "BOOKING", action = "CANCEL_BOOKING", targetId = "#bookingId", entityClass = Booking.class)
    public BookingCancelResponse cancelFullBooking(Long bookingId, PaymentMethod refundMethod, String reason) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));


        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.PENDING_REFUND) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS, "Đơn hàng này đã được hủy hoặc đang chờ hoàn tiền trước đó.");
        }

        if (booking.getArrivalDate().toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_BOOKING_STATUS, "Đơn hàng đã quá hạn nhận phòng.");
        }

        BigDecimal totalPaid = getTotalPaid(bookingId);
        BigDecimal finalRefundAmount = calculateRefundAmount(totalPaid, booking.getArrivalDate().toLocalDate()).subtract(getAlreadyRefundedAmount(bookingId)).setScale(0, java.math.RoundingMode.HALF_UP);

        Payment vnpayPayment = getVnpayPayment(bookingId);
        PaymentStatus refundStatus = PaymentStatus.SUCCESS;

        if (finalRefundAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (vnpayPayment != null) {
                refundStatus = performVnPayRefund(booking, vnpayPayment, finalRefundAmount, "Refund full booking");
            } else {
                saveRefundPayment(booking, finalRefundAmount, refundMethod, PaymentStatus.PENDING, "RFND-MANUAL-" + System.currentTimeMillis());
                refundStatus = PaymentStatus.PENDING;
            }
        }

        booking.setCancelReason(reason != null && !reason.trim().isEmpty() ? reason : "Lễ tân hủy toàn bộ");
        booking.setRoomAmount(BigDecimal.ZERO);
        booking.setServiceAmount(BigDecimal.ZERO);
        booking.setTotalAmount(BigDecimal.ZERO);
        booking.setStatus(refundStatus == PaymentStatus.SUCCESS ? BookingStatus.CANCELLED : BookingStatus.PENDING_REFUND);

        booking.getBookingRooms().forEach(br -> {
            if(!BookingStatus.CANCELLED.equals(br.getStatus())) {
                br.setStatus(BookingRoomStatus.CANCELLED);
                br.setCancelReason("Hủy theo đơn tổng: " + booking.getCancelReason());
                if (br.getRoom() != null) br.getRoom().setStatus(RoomStatus.AVAILABLE);
            }
        });

        if (booking.getBookingServices() != null) {
            booking.getBookingServices().forEach(bs -> {
                bs.setStatus(BookingServiceStatus.CANCELLED);
            });
        }

        releaseRooms(booking);
        bookingRepo.save(booking);

        return BookingCancelResponse.builder().status("SUCCESS").message(refundStatus == PaymentStatus.SUCCESS ? "Hủy toàn bộ thành công" : "Hủy thành công, cần kiểm tra PENDING").build();
    }

    @Override
    @Transactional
    public void approveManualRefund(Long bookingId, PaymentMethod refundMethod) {


        if (refundMethod == PaymentMethod.BANK_TRANSFER || refundMethod == PaymentMethod.BANK_TRANSFER) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {

                throw new AppException(ErrorCode.ADMIN_REQUIRED,
                        "Hành động không được phép: Chỉ có quản trị viên (ADMIN) mới được duyệt hoàn tiền qua chuyển khoản ngân hàng.");
            }
        }


        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        List<Payment> pendingRefunds = paymentRepo.findByBookingId(bookingId)
                .stream()
                .filter(p -> p.getPaymentType() == PaymentType.REFUND
                        && p.getStatus() == PaymentStatus.PENDING)
                .toList();

        if (pendingRefunds.isEmpty()) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND, "Không tìm thấy khoản hoàn tiền đang chờ.");
        }


        BigDecimal totalRefundApproved = pendingRefunds.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pendingRefunds.forEach(p -> {
            p.setMethod(refundMethod);
            p.setStatus(PaymentStatus.SUCCESS);
            p.setPaidAt(LocalDateTime.now());


            if (p.getTransactionId() == null || p.getTransactionId().trim().isEmpty()) {
                p.setTransactionId("RFND-MANUAL-" + System.currentTimeMillis());
            }

            paymentRepo.save(p);
        });


        if (booking.getActualCheckIn() != null) {

            booking.setStatus(BookingStatus.CHECKED_OUT);
        } else {

            booking.setStatus(BookingStatus.CANCELLED);
        }
        bookingRepo.save(booking);


        String logDesc = String.format("Xác nhận hoàn tiền thủ công cho đơn đặt phòng #%d. Tổng tiền hoàn trả khách: %,.0f VND bằng hình thức [%s].",
                bookingId, totalRefundApproved, refundMethod.name());

        auditLogService.saveLog("BOOKING", "REFUND", bookingId, logDesc);
    }
}






