

const errorMapping = {
    
    "FULL_NAME_REQUIRED": "Vui lòng nhập họ tên",
    "NAME_INVALID": "Họ tên không được chứa số hoặc ký tự đặc biệt",
    "PHONE_NUMBER_REQUIRED": "Vui lòng nhập số điện thoại",
    "PHONE_INVALID": "Số điện thoại không hợp lệ (10-11 số)",
    "EMAIL_REQUIRED": "Vui lòng nhập email",
    "EMAIL_INVALID": "Định dạng email không đúng",
    "ROLE_ID_REQUIRED": "Vui lòng chọn quyền",

    
    "VALIDATION_ERROR": "Dữ liệu đầu vào không hợp lệ",
    "UNCATEGORIZED_EXCEPTION": "Có lỗi hệ thống xảy ra",

    
    "ROOM_NOT_FOUND": "Không tìm thấy phòng",
    "ROOM_ALREADY_EXISTS": "Số phòng này đã tồn tại",
    "ROOM_NOT_AVAILABLE": "Phòng hiện tại không còn trống",
    "ROOM_TYPE_NOT_FOUND": "Không tìm thấy loại phòng",
    "ROOM_TYPE_ALREADY_EXISTS": "Tên loại phòng đã tồn tại",
    "INVALID_ROOM_STATUS": "Trạng thái phòng không hợp lệ",

    
    "CUSTOMER_NOT_FOUND": "Không tìm thấy thông tin khách hàng",
    "CUSTOMER_ALREADY_EXISTS": "Khách hàng này đã tồn tại",
    "PHONE_NUMBER_EXISTED": "Số điện thoại đã được đăng ký",
    "IDENTITY_CARD_EXISTED": "Số CCCD/Passport đã tồn tại",

    
    "BOOKING_NOT_FOUND": "Không tìm thấy đơn đặt phòng",
    "INVALID_DATE_RANGE": "Ngày trả phòng phải sau ngày nhận phòng",
    "BOOKING_ALREADY_CANCELLED": "Đơn hàng đã bị hủy",
    "CHECKIN_REQUIRED": "Vui lòng thực hiện Check-in trước",

    
    "USER_NOT_FOUND": "Không tìm thấy người dùng",
    "USER_EXISTED": "Tài khoản này đã tồn tại",
    "EMPLOYEE_NOT_FOUND": "Không tìm thấy thông tin nhân viên",
    "ROLE_NOT_FOUND": "Quyền truy cập không hợp lệ",
};

/**
 * Hàm lấy thông báo lỗi thân thiện
 * @param {string} errorCode - Mã lỗi trả về từ Backend
 * @returns {string} - Thông báo hiển thị cho người dùng
 */
export const getErrorMessage = (errorCode) => {
    return errorMapping[errorCode] || "Có lỗi xảy ra, vui lòng thử lại sau";
};