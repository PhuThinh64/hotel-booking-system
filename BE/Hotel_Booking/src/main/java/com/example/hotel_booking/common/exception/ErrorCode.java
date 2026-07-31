package com.example.hotel_booking.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // SYSTEM & COMMON MODULE (0000 - 0999)
    SUCCESS(1000, "Success", HttpStatus.OK),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1006, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(5000, "Lỗi máy chủ nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TIME_RANGE(6000, "Khoảng thời gian không hợp lệ", HttpStatus.BAD_REQUEST),

    // ROOM & ROOM TYPE MODULE (1000 - 1999)
    ROOM_NOT_FOUND(1001, "Không tìm thấy phòng", HttpStatus.NOT_FOUND),
    ROOM_TYPE_NOT_FOUND(1002, "Không tìm thấy loại phòng", HttpStatus.NOT_FOUND),
    ROOM_ALREADY_EXISTS(1003, "Số phòng này đã tồn tại trên hệ thống", HttpStatus.BAD_REQUEST),
    ROOM_NOT_AVAILABLE(1004, "Phòng hiện tại không còn trống", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_ALREADY_EXISTS(1005, "Tên loại phòng đã tồn tại", HttpStatus.BAD_REQUEST),
    ROOM_ID_NOT_EMPTY(1007,"Id phòng không được trống",HttpStatus.BAD_REQUEST),
    INVALID_ROOM_STATUS(1010, "Trạng thái phòng không hợp lệ để thực hiện hành động này", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(1011, "Tên loại phòng không được để trống", HttpStatus.BAD_REQUEST),
    PRICE_REQUIRED(1012, "Giá phòng không được để trống", HttpStatus.BAD_REQUEST),
    PRICE_INVALID(1013, "Giá phòng phải lớn hơn hoặc bằng 0", HttpStatus.BAD_REQUEST),
    MAX_GUEST_REQUIRED(1014, "Số lượng khách tối đa không được để trống", HttpStatus.BAD_REQUEST),
    MAX_GUEST_INVALID(1015, "Số lượng khách phải ít nhất là 1 người", HttpStatus.BAD_REQUEST),
    DESCRIPTION_REQUIRED(1016, "Mô tả không được để trống", HttpStatus.BAD_REQUEST),
    IMAGE_URL_REQUIRED(1017, "Đường dẫn ảnh phòng không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_FORMAT(1018, "Định dạng ảnh không hợp lệ (chỉ chấp nhận jpg, jpeg, png, gif, webp)", HttpStatus.BAD_REQUEST),
    INVALID_ROOM_TYPE(1019, "Danh sách loại phòng không hợp lệ", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_IN_USE(1020, "Không thể xóa do vẫn còn phòng đang hoạt động thuộc loại phòng này", HttpStatus.BAD_REQUEST),
    ROOM_NOT_ASSIGNED(1021,"Phòng chưa được chỉ định",HttpStatus.BAD_REQUEST),


    // CUSTOMER MODULE (2000 - 2999)
    CUSTOMER_NOT_FOUND(2001, "Không tìm thấy thông tin khách hàng", HttpStatus.NOT_FOUND),
    CUSTOMER_ALREADY_EXISTS(2003, "Khách hàng này đã tồn tại trên hệ thống", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(2004, "Số điện thoại này đã được đăng ký", HttpStatus.BAD_REQUEST),
    IDENTITY_CARD_EXISTED(2005, "Số CCCD/Passport này đã tồn tại", HttpStatus.BAD_REQUEST),
    GENDER_REQUIRED(2006, "Vui lòng chọn giới tính hợp lệ", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(2007, "Email này đã được sử dụng cho tài khoản khác", HttpStatus.BAD_REQUEST),
    CUSTOMER_NAME_MISMATCH(2008, "Họ tên không trùng khớp với thông tin khách hàng đã lưu trên hệ thống", HttpStatus.BAD_REQUEST),

    // BOOKING & CHECK-IN/OUT MODULE (3000 - 3999)
    BOOKING_NOT_FOUND(3001, "Không tìm thấy đơn đặt phòng", HttpStatus.NOT_FOUND),
    BOOKING_ALREADY_EXISTS(3003, "Đơn đặt phòng này đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_BOOKING_DATES(3004, "Ngày trả phòng không hợp lệ", HttpStatus.BAD_REQUEST),
    CHECKIN_DATE_PAST(3005, "Ngày nhận phòng không được ở trong quá khứ", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE(3011, "Ngày trả phòng phải sau ngày nhận phòng", HttpStatus.BAD_REQUEST),
    DEPOSIT_TOO_HIGH(3006, "Tiền cọc không được lớn hơn tổng tiền phòng", HttpStatus.BAD_REQUEST),
    BOOKING_ALREADY_CANCELLED(3007, "Đơn hàng đã bị hủy, không thể thực hiện thao tác", HttpStatus.BAD_REQUEST),
    INVALID_BOOKING_STATUS(3008, "Trạng thái đơn hàng không hợp lệ cho thao tác này", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_CONFIRMED(3009, "Đơn hàng chưa được xác nhận tiền cọc", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_CHECKED_IN(3010, "Khách hàng chưa nhận phòng (Check-in)", HttpStatus.BAD_REQUEST),
    CHECKIN_REQUIRED(3012, "Khách hàng cần làm thủ tục Check-in trước khi thực hiện thao tác này", HttpStatus.BAD_REQUEST),

    // SERVICE MODULE (4000 - 4999)
    SERVICE_NOT_FOUND(4001, "Không tìm thấy dịch vụ yêu cầu", HttpStatus.NOT_FOUND),
    SERVICE_NAME_EXISTED(4002, "Tên dịch vụ này đã tồn tại", HttpStatus.BAD_REQUEST),
    SERVICE_DETAIL_NOT_FOUND(4004, "Không tìm thấy chi tiết dịch vụ đã đặt", HttpStatus.NOT_FOUND),

    // BOOKING ROOM MODULE(5000 - 5999)
    ROOM_ALREADY_BOOKED(5001, "Phòng đã có người đặt hoặc đang sử dụng trong khoảng thời gian này", HttpStatus.BAD_REQUEST),
    BOOKING_ROOM_NOT_FOUND(5002, "Không tìm thấy chi tiết phòng trong đơn đặt", HttpStatus.NOT_FOUND),
    CHECKOUT_DATE_INVALID(5003, "Ngày trả phòng phải sau ngày nhận phòng ít nhất 1 ngày", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_SOLD_OUT(5004, "Hạng phòng này đã hết chỗ trong khoảng thời gian đã chọn", HttpStatus.BAD_REQUEST),

    //PAYMENT (6000 - 7999)
    PAYMENT_NOT_FOUND(6001,"Không tìm thấy payment",HttpStatus.NOT_FOUND),
    PAYMENT_FAILED(6002,"Thanh toán thất bại",HttpStatus.BAD_REQUEST),
    REFUND_FAILED(6003,"Hoàn tiền VNPay thất bại",HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_TYPE(6004, "Loại giao dịch thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED(6005, "Giao dịch thanh toán này đã được xử lý thành công trước đó", HttpStatus.BAD_REQUEST),
    REFUND_ALREADY_EXISTS(6006, "Yêu cầu hoàn tiền cho đơn này đang được xử lý, không thể tạo thêm.", HttpStatus.BAD_REQUEST),
    LACK_OF_PAYMENT_METHODS(6007, "Thiếu phương thức thanh toán",HttpStatus.BAD_REQUEST),

    //USER (8000 - 8999)
    USER_NOT_FOUND(8001,"Không tìm thấy user",HttpStatus.NOT_FOUND),
    USER_EXISTED(8002, "Tên tài khoản này đã tồn tại trên hệ thống", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(8003, "Tài khoản hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    USER_NOT_EXISTED(8004,"User không tồn tại",HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(8005, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),

    // EMPLOYEE MODULE (9000 - 9999)
    EMPLOYEE_NOT_FOUND(9001, "Không tìm thấy thông tin nhân viên", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(9002, "Không tìm thấy quyền (role) được yêu cầu", HttpStatus.NOT_FOUND), // Thêm dòng này
    ROLE_NOT_SUPPORTED(9003, "Hệ thống không hỗ trợ quyền truy cập này", HttpStatus.BAD_REQUEST),
    INVALID_ROLE(9004, "Quyền truy cập không hợp lệ", HttpStatus.FORBIDDEN),
    PROFILE_LINK_NOT_FOUND(9005, "Tài khoản chưa được liên kết với hồ sơ cá nhân", HttpStatus.NOT_FOUND),
    ADMIN_REQUIRED(9006, "Chức năng này yêu cầu quyền quản trị (ADMIN)", HttpStatus.FORBIDDEN),

    ;



    private final int code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}