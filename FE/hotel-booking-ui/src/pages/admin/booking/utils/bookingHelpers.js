



/** Tổng số lượng dịch vụ còn hiệu lực trong đơn */
export const getServiceQuantity = (booking) =>
  (booking?.bookingServices || [])
    .filter((s) => s.status !== 'CANCELLED')
    .reduce((sum, s) => sum + (s.quantity || 0), 0);

/** Tổng tiền dịch vụ còn hiệu lực (bỏ qua dịch vụ đã hủy) */
export const getServiceAmount = (booking) =>
  (booking?.bookingServices || [])
    .filter((s) => s.status !== 'CANCELLED')
    .reduce((sum, s) => {
      const total = Number(s.totalPrice ?? 0);
      const fallback = Number(s.quantity || 0) * Number(s.priceAtOrder || 0);
      return sum + (total > 0 ? total : fallback);
    }, 0);

/** Tổng giá trị hóa đơn = Tiền phòng + Dịch vụ hiệu lực + Phụ phí */
export const getTotalInvoiceAmount = (booking) => {
  if (!booking) return 0;
  const roomAmount      = Number(booking.roomAmount      || 0);
  const surchargeAmount = Number(booking.surchargeAmount || 0);
  return roomAmount + getServiceAmount(booking) + surchargeAmount;
};

/**
 * Số tiền còn lại phải xử lý.
 *  > 0  → Khách còn nợ (thu thêm)
 *  < 0  → Khách dư tiền (cần hoàn lại)
 *  = 0  → Đã quyết toán đủ
 *
 * Với đơn CANCELLED / PENDING_REFUND: trả về refundAmount - depositAmount
 */
export const getRemainingAmount = (booking) => {
  if (!booking) return 0;

  const deposit = Number(booking.depositAmount || 0);
  const refund = Number(booking.refundAmount || 0);
  const total = getTotalInvoiceAmount(booking);

  
  if (['CANCELLED', 'PENDING_REFUND'].includes(booking.status)) {
    return refund - deposit; 
  }

  
  return total - (deposit - refund);
};

/**
 * Số đêm lưu trú.
 * Ưu tiên checkInDate/checkOutDate (tên field backend mới),
 * fallback sang arrivalDate/departureDate (tên field cũ).
 */
export const getBookingNights = (booking) => {
  const start =
    booking?.checkInDate  || booking?.arrivalDate  ||
    booking?.checkIn      || booking?.startDate;
  const end =
    booking?.checkOutDate || booking?.departureDate ||
    booking?.checkOut     || booking?.endDate;

  if (!start || !end) return 1;
  const diff = Math.ceil((new Date(end) - new Date(start)) / 86_400_000);
  return diff > 0 ? diff : 1;
};

/** Map trạng thái → màu Ant Design Tag */
export const STATUS_COLOR_MAP = {
  PENDING_DEPOSIT: 'orange',
  CONFIRMED:       'cyan',
  CHECKED_IN:      'blue',
  CHECKED_OUT:     'green',
  CANCELLED:       'red',
  PENDING_REFUND:  'gold',
};
