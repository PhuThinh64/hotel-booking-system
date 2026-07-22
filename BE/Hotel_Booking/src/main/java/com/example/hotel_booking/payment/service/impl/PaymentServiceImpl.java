package com.example.hotel_booking.payment.service.impl;

import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.booking.dto.BookingResponse;
import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.booking.repository.BookingRepository;
import com.example.hotel_booking.booking.service.BookingService;
import com.example.hotel_booking.common.*;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.common.service.VnPayService;
import com.example.hotel_booking.payment.dto.PaymentResponse;
import com.example.hotel_booking.payment.dto.PendingRefundResponse;
import com.example.hotel_booking.payment.entity.Payment;
import com.example.hotel_booking.payment.mapper.PaymentMapper;
import com.example.hotel_booking.payment.repository.PaymentRepository;
import com.example.hotel_booking.payment.repository.PaymentSpecification;
import com.example.hotel_booking.payment.service.PaymentService;
import com.example.hotel_booking.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;
    private final AuditLogService auditLogService;
    private final VnPayService vnPayService;
    private final BookingService bookingService;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processRefund(
            Long bookingId,
            BigDecimal refundAmount,
            String reason,
            PaymentMethod method
    ) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment refundPayment = Payment.builder()
                .booking(booking)
                .amount(refundAmount)
                .paymentType(PaymentType.REFUND)
                .method(method)
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .transactionId("REF-" + System.currentTimeMillis())
                .build();

        paymentRepository.save(refundPayment);

        String logDesc = String.format("Thực hiện hoàn tiền: %,.0f VND bằng [%s]. Lý do: %s",
                refundAmount, method, reason);
        auditLogService.saveLog("PAYMENT", "REFUND", bookingId, logDesc);
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        BigDecimal totalCollected = paymentRepository.sumRevenueByTypes(start, end,
                List.of(PaymentType.DEPOSIT, PaymentType.FINAL_PAYMENT, PaymentType.ADDITIONAL));

        BigDecimal totalRefunded = paymentRepository.sumRevenueByTypes(start, end,
                List.of(PaymentType.REFUND));

        totalCollected = (totalCollected != null) ? totalCollected : BigDecimal.ZERO;
        totalRefunded = (totalRefunded != null) ? totalRefunded : BigDecimal.ZERO;

        return totalCollected.subtract(totalRefunded);
    }

    @Override
    @Transactional
    public void processVnPaySuccess(String txnRef, String transactionNo, String amountStr) {
        // 1. Tìm bản ghi Payment đã tạo lúc trước bằng txnRef
        Payment payment = paymentRepository.findByTransactionId(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND, "Không tìm thấy giao dịch với mã: " + txnRef));

        // 2. Lấy booking từ payment
        Booking booking = payment.getBooking();

        // 3. Chống duplicate: nếu đã có bản ghi payment này với mã transactionNo từ bank rồi thì bỏ qua
        boolean alreadyProcessed = paymentRepository.existsByTransactionId(transactionNo);
        if (alreadyProcessed) return;

        // 4. Cập nhật trạng thái payment hiện tại thành SUCCESS (thay vì tạo mới)
        BigDecimal amount = new BigDecimal(amountStr).divide(new BigDecimal(100));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId(transactionNo); // Cập nhật mã giao dịch thật từ ngân hàng
        paymentRepository.save(payment);

        // 5. Cập nhật trạng thái booking
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setExpiryDate(null);

        // Cập nhật trạng thái phòng (nếu cần)
        if (booking.getBookingRooms() != null) {
            booking.getBookingRooms().forEach(br -> {
                if (br.getRoom() != null) {
                    br.getRoom().setStatus(RoomStatus.OCCUPIED);
                    roomRepository.save(br.getRoom());
                }
            });
        }
        bookingRepository.save(booking);
//        String logDesc = String.format("Xác nhận thanh toán thành công cho đơn #%d. Số tiền: %s VND. Mã GD: %s",
//                payment.getBooking().getId(), amountStr, transactionNo);
//        auditLogService.saveLog("PAYMENT", "SUCCESS", payment.getBooking().getId(), logDesc);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PendingRefundResponse> getPendingRefunds(
            String keyword,
            String status,
            String method,
            String startDate,
            String endDate,
            Pageable pageable
    ) {
        // Truyền đầy đủ 5 tham số vào Specification
        var spec = PaymentSpecification.filterRefunds(keyword, status, method, startDate, endDate);

        return paymentRepository.findAll(spec, pageable)
                .map(paymentMapper::toPendingRefundResponse);
    }

    @Transactional
    public String handleVnPayCallback(Map<String, String> fields, String secureHash) {

        // 1. Kiểm tra chữ ký bảo mật chống hack dữ liệu
        boolean isCorrectSignature = vnPayService.verifySignature(fields, secureHash);

        // 2. Lấy các thông tin cần thiết từ Map fields
        String responseCode = fields.get("vnp_ResponseCode");
        String txnRef = fields.get("vnp_TxnRef");
        String transactionNo = fields.get("vnp_TransactionNo");
        String amount = fields.get("vnp_Amount");
        String orderInfo = fields.get("vnp_OrderInfo");

        // =========================================================================
        // LUỒNG 1: Thanh toán nốt số tiền còn lại lúc Checkout (Trả phòng)
        // =========================================================================
        if (orderInfo != null && orderInfo.startsWith("PAY_REMAINING_BOOKING_")) {
            if (isCorrectSignature && "00".equals(responseCode)) {
                Long bookingId = Long.parseLong(orderInfo.replace("PAY_REMAINING_BOOKING_", ""));

                // Cập nhật trạng thái thanh toán hoàn tất lúc trả phòng
                bookingService.finalizeCheckoutVnPay(bookingId, transactionNo, amount);

                return "http://localhost:5173/payment-result?status=success";
            } else {
                return "http://localhost:5173/payment-result?status=failed";
            }
        }

        // =========================================================================
        // LUỒNG 2: Đặt phòng mới (Đặt cọc hoặc Thanh toán toàn bộ ban đầu)
        // =========================================================================
        if (isCorrectSignature && "00".equals(responseCode)) {
            // Gọi hàm nội bộ hoặc Repo để lưu lịch sử giao dịch thành công / duyệt đơn
            this.processVnPaySuccess(txnRef, transactionNo, amount);

            // Xử lý logic và lấy thông tin phản hồi của Booking (code cũ/mới của bạn)
            BookingResponse bookingResponse = bookingService.processVnPayCallback(fields);

            // Trả về URL kết quả kèm bookingCode và số điện thoại người ở thực tế (contactPhone)
            return "http://localhost:5173/payment-result?status=success"
                    + "&bookingCode=" + bookingResponse.getBookingCode()
                    + "&phone=" + bookingResponse.getContactPhone();
        } else {
            return "http://localhost:5173/payment-result?status=failed";
        }
    }
}