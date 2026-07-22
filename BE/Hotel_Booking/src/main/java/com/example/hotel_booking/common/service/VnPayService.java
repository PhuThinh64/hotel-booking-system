package com.example.hotel_booking.common.service;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.common.PaymentType;
import com.example.hotel_booking.config.VnPayConfig;
import com.example.hotel_booking.payment.entity.Payment;
import com.example.hotel_booking.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VnPayService {

    private final PaymentRepository paymentRepository;
    private final String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
    private final String vnp_HashSecret = VnPayConfig.vnp_HashSecret;
    private final String vnp_PayUrl = VnPayConfig.vnp_PayUrl;
    private final String vnp_ReturnUrl = "http://localhost:8080/api/v1/payments/vnpay-callback";

    public VnPayService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Tạo mã Transaction ID duy nhất
     */
    private String generateTransactionId(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + new Random().nextInt(1000);
    }

    /**
     * Hàm dùng chung để tạo URL thanh toán VNPay
     */
    @Transactional
    public String createPaymentUrl(Booking booking, BigDecimal amount, PaymentType paymentType, String ipAddress, String orderInfo) {
        // 1. Tạo bản ghi Payment mới và lưu vào DB
        String txnRef = generateTransactionId("VNPAY");

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(amount)
                .paymentType(paymentType)
                .method(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .transactionId(txnRef)
                .paidAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        // 2. Build params cho VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);

        long amountInVND = amount.multiply(new BigDecimal(100)).longValue();
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        return buildVnPayUrl(vnp_Params);
    }

    /**
     * Hàm helper để build URL và Hash
     */
    private String buildVnPayUrl(Map<String, String> vnp_Params) {
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            try {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()).replace("+", "%20");
                hashData.append(fieldName).append('=').append(encodedValue);
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString())).append('=').append(encodedValue);
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        String vnp_SecureHash = hmacSHA512(vnp_HashSecret.trim(), hashData.toString()).toUpperCase();
        return vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    public boolean verifySignature(Map<String, String> fields, String secureHash) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=').append(fieldValue);
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        String calculatedSign = hmacSHA512(vnp_HashSecret.trim(), hashData.toString()).toUpperCase();
        return calculatedSign.equalsIgnoreCase(secureHash);
    }

    private String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) return "";
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) { sb.append(String.format("%02x", b & 0xff)); }
            return sb.toString();
        } catch (Exception ex) { return ""; }
    }

    public Map<String, Object> refund(String vnp_TransactionType, String vnp_TxnRef, BigDecimal amount,
                                      String vnp_TransactionNo, LocalDateTime vnp_TransactionDate, String createBy,
                                      String vnp_OrderInfo, String vnp_IpAddr) {
        try {
            String vnp_RequestId = String.valueOf(System.currentTimeMillis());
            String vnp_Version = "2.1.0";
            String vnp_Command = "refund";

            long amountInVnPayUnits = amount.setScale(0, java.math.RoundingMode.HALF_UP).multiply(new java.math.BigDecimal(100)).longValue();
            String vnp_Amount = String.valueOf(amountInVnPayUnits);
            String transactionDateStr = vnp_TransactionDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            String hashData = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TransactionType,
                    vnp_TxnRef, vnp_Amount, vnp_TransactionNo, transactionDateStr, createBy,
                    vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

            String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("vnp_RequestId", vnp_RequestId);
            requestBody.put("vnp_Version", vnp_Version);
            requestBody.put("vnp_Command", vnp_Command);
            requestBody.put("vnp_TmnCode", vnp_TmnCode);
            requestBody.put("vnp_TransactionType", vnp_TransactionType);
            requestBody.put("vnp_TxnRef", vnp_TxnRef);
            requestBody.put("vnp_Amount", vnp_Amount);
            requestBody.put("vnp_TransactionNo", vnp_TransactionNo);
            requestBody.put("vnp_TransactionDate", transactionDateStr);
            requestBody.put("vnp_CreateBy", createBy);
            requestBody.put("vnp_CreateDate", vnp_CreateDate);
            requestBody.put("vnp_IpAddr", vnp_IpAddr);
            requestBody.put("vnp_OrderInfo", vnp_OrderInfo);
            requestBody.put("vnp_SecureHash", vnp_SecureHash);

            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            org.springframework.http.HttpEntity<Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            String refundUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

            return restTemplate.postForObject(refundUrl, entity, Map.class);
        } catch (Exception e) {
            Map<String, Object> errRes = new HashMap<>();
            errRes.put("vnp_ResponseCode", "99");
            errRes.put("vnp_Message", "Lỗi xử lý: " + e.getMessage());
            return errRes;
        }
    }
}