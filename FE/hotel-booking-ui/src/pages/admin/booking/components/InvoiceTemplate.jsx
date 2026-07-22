import React from 'react';

const InvoiceTemplate = React.forwardRef(({ invoiceData }, ref) => {
  if (!invoiceData) return null;

  const bookingRooms = invoiceData.bookingRooms || [];
  const extraServices = invoiceData.bookingServices || [];

  // Tối ưu hóa lấy các trường tổng tiền từ API
  const roomPriceAmount = invoiceData.roomAmount ?? invoiceData.roomTotalAmount ?? 0;
  const servicePriceAmount = invoiceData.serviceAmount ?? invoiceData.serviceTotalAmount ?? 0;
  const depositAmount = invoiceData.depositAmount ?? invoiceData.deposit ?? 0;
  const totalAmount = invoiceData.totalAmount ?? invoiceData.totalPrice ?? 0;
  const surchargeAmount = invoiceData.surchargeAmount ?? invoiceData.surcharge ?? 0;

  const brandColor = '#1e3a8a';
  const textDark = '#1e293b';
  const textMuted = '#64748b';
  const borderLight = '#e2e8f0';

  return (
    <>
      {/* CSS quản lý chế độ In: Trên web thì ẩn đi, khi IN thực tế thì mới hiển thị */}
      <style>{`
        @media screen {
          .invoice-print-area {
            display: none !important; /* Ẩn hoàn toàn trên giao diện web thường để không vỡ layout */
          }
        }
      `}</style>

      <div 
        ref={ref} 
        className="invoice-print-area" 
        style={{
          fontFamily: 'system-ui, -apple-system, sans-serif',
          color: textDark,
          backgroundColor: '#ffffff',
          padding: '24px 32px',
          maxWidth: '800px',
          margin: '0 auto',
          lineHeight: '1.4'
        }}
      >
        {/* 1. HEADER BRANDING */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: `3px solid ${brandColor}`, paddingBottom: '20px', marginBottom: '24px' }}>
          <div>
            <h1 style={{ fontSize: '26px', fontWeight: '900', color: brandColor, margin: '0 0 6px 0', letterSpacing: '0.5px' }}>
              LOTUS HOTEL 
            </h1>
            <p style={{ fontSize: '13px', color: textMuted, margin: '0 0 3px 0', fontWeight: '500' }}>
              📍 180 Cao Lỗ, Phường 4, Quận 8, TP.HCM
            </p>
            <p style={{ fontSize: '13px', color: textMuted, margin: '0', fontWeight: '500' }}>
              📞 Điện thoại: 0943 026 086
            </p>
          </div>
          <div style={{ textAlign: 'right' }}>
            <h2 style={{ fontSize: '20px', fontWeight: '800', color: brandColor, margin: '0 0 6px 0', textTransform: 'uppercase', letterSpacing: '1px' }}>
              HÓA ĐƠN THANH TOÁN
            </h2>
            <p style={{ fontSize: '13px', fontWeight: '700', color: '#475569', margin: '0 0 3px 0' }}>
              Mã đơn: <span style={{ color: brandColor }}>BOOK-{invoiceData.id || invoiceData.bookingId}</span>
            </p>
            <p style={{ fontSize: '12px', color: textMuted, margin: '0' }}>
              Ngày in: {new Date().toLocaleDateString('vi-VN')}
            </p>
          </div>
        </div>

        {/* 2. THÔNG TIN KHÁCH HÀNG */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '24px', padding: '16px', backgroundColor: '#f8fafc', borderRadius: '8px', border: `1px solid ${borderLight}` }}>
          <div>
            <h3 style={{ fontSize: '11px', fontWeight: '800', color: brandColor, textTransform: 'uppercase', letterSpacing: '1px', margin: '0 0 8px 0' }}>
              Thông tin khách hàng
            </h3>
            <p style={{ fontSize: '15px', fontWeight: '700', margin: '0 0 6px 0', color: '#0f172a' }}>
              {invoiceData.customerName}
            </p>
            <p style={{ fontSize: '12px', color: '#475569', margin: '0 0 4px 0' }}>
              SĐT: <span style={{ fontWeight: '600', color: textDark }}>{invoiceData.customerPhone || 'Không có'}</span>
            </p>
            <p style={{ fontSize: '12px', color: '#475569', margin: '0' }}>
              Email: <span style={{ fontWeight: '600', color: textDark }}>{invoiceData.customerEmail || 'Không có'}</span>
            </p>
          </div>
          <div style={{ textAlign: 'right' }}>
            <h3 style={{ fontSize: '11px', fontWeight: '800', color: brandColor, textTransform: 'uppercase', letterSpacing: '1px', margin: '0 0 8px 0' }}>
              Thời gian lưu trú
            </h3>
            <p style={{ fontSize: '12px', color: '#475569', margin: '0 0 4px 0' }}>
              Nhận phòng (Check-in): <span style={{ fontWeight: '600', color: textDark }}>{invoiceData.arrivalDate ? new Date(invoiceData.arrivalDate).toLocaleDateString('vi-VN') : '---'}</span>
            </p>
            <p style={{ fontSize: '12px', color: '#475569', margin: '0 0 6px 0' }}>
              Trả phòng (Check-out): <span style={{ fontWeight: '600', color: textDark }}>{invoiceData.departureDate ? new Date(invoiceData.departureDate).toLocaleDateString('vi-VN') : '---'}</span>
            </p>
            <p style={{ fontSize: '12px', color: '#475569', margin: '0' }}>
              Thanh toán: <span style={{ fontSize: '11px', fontWeight: '800', padding: '3px 8px', backgroundColor: '#e0f2fe', color: '#0369a1', borderRadius: '4px', textTransform: 'uppercase' }}>{invoiceData.paymentMethod || 'CASH'}</span>
            </p>
          </div>
        </div>

        {/* 3. BẢNG CHI TIẾT */}
        <table style={{ width: '100%', borderCollapse: 'collapse', marginBottom: '24px' }}>
          <thead>
            <tr style={{ borderBottom: `2px solid ${brandColor}`, backgroundColor: '#f1f5f9' }}>
              <th style={{ padding: '12px 16px', textAlign: 'left', fontSize: '12px', fontWeight: '800', color: '#475569', textTransform: 'uppercase' }}>Danh mục thanh toán</th>
              <th style={{ padding: '12px 16px', textAlign: 'center', fontSize: '12px', fontWeight: '800', color: '#475569', textTransform: 'uppercase' }}>Số lượng / Đêm</th>
              <th style={{ padding: '12px 16px', textAlign: 'right', fontSize: '12px', fontWeight: '800', color: '#475569', textTransform: 'uppercase' }}>Đơn giá</th>
              <th style={{ padding: '12px 16px', textAlign: 'right', fontSize: '12px', fontWeight: '800', color: '#475569', textTransform: 'uppercase' }}>Thành tiền</th>
            </tr>
          </thead>
          <tbody>
           {/* A. Thống kê Danh mục Phòng */}
          {bookingRooms.map((br, index) => {
            // 1. Kiểm tra trạng thái phòng bị hủy
            const isRoomCancelled = br.status === 'REJECTED' || br.status === 'CANCELLED';
            const rNights = invoiceData.nights || 1;

            // 2. Đếm tổng số phòng đang hoạt động (không bị hủy) trong đơn này
            const activeRoomsCount = bookingRooms.filter(
              (r) => r.status !== 'REJECTED' && r.status !== 'CANCELLED'
            ).length || 1;

            // 3. Tính toán đơn giá phòng dự phòng (Sử dụng tổng tiền phòng gốc chia cho số phòng hoạt động và chia tiếp số đêm)
            let rPrice = 0;
            if (!isRoomCancelled) {
              rPrice = br.roomPrice || 
                       br.price || 
                       br.room?.price || 
                       br.roomType?.price || 
                       // Thuật toán chia đều từ tổng tiền phòng gốc:
                       (roomPriceAmount / activeRoomsCount / rNights) || 
                       0;
            } else {
              // Đối với phòng đã hủy, ta lấy giá trị tượng trưng từ API hoặc mặc định hiển thị giá gốc để gạch ngang
              rPrice = br.roomPrice || 
                       br.price || 
                       br.room?.price || 
                       br.roomType?.price || 
                       1200000; // Giá tượng trưng nếu API trả về trống rỗng
            }
            
            // Thành tiền: Nếu phòng hủy thì bằng 0, phòng thường = Đơn giá * Số đêm
            const rTotal = isRoomCancelled ? 0 : (rPrice * rNights);

            return (
              <tr 
                key={`room-${index}`} 
                style={{ 
                  borderBottom: `1px solid ${borderLight}`,
                  backgroundColor: isRoomCancelled ? '#fef2f2' : 'transparent',
                  opacity: isRoomCancelled ? 0.7 : 1
                }}
              >
                <td style={{ padding: '14px 16px' }}>
                  <span style={{ fontWeight: '700', fontSize: '14px', display: 'block', color: isRoomCancelled ? '#ef4444' : '#0f172a' }}>
                    Phòng {br.roomNumber || 'Chưa xếp'} {isRoomCancelled && ' [ĐÃ HỦY]'}
                  </span>
                  <span style={{ fontSize: '12px', color: textMuted, marginTop: '2px', display: 'block' }}>
                    Loại phòng: {br.roomType?.name || br.roomType || 'Standard Twin'}
                  </span>
                </td>
                <td style={{ padding: '14px 16px', textAlign: 'center', fontSize: '13px', fontWeight: '600' }}>
                  {rNights} đêm
                </td>
                <td style={{ 
                  padding: '14px 16px', 
                  textAlign: 'right', 
                  fontSize: '13px', 
                  fontWeight: '500',
                  textDecoration: isRoomCancelled ? 'line-through' : 'none',
                  color: isRoomCancelled ? textMuted : textDark
                }}>
                  {rPrice.toLocaleString()}₫
                </td>
                <td style={{ 
                  padding: '14px 16px', 
                  textAlign: 'right', 
                  fontSize: '14px', 
                  fontWeight: '700', 
                  color: isRoomCancelled ? '#ef4444' : '#0f172a' 
                }}>
                  {rTotal.toLocaleString()}₫
                </td>
              </tr>
            );
          })}

            {/* B. Thống kê Danh mục Dịch vụ */}
            {extraServices.map((service, index) => {
              const isServiceCancelled = service.status === 'REJECTED' || service.status === 'CANCELLED';
              const sQty = service.quantity || 1;
              const sTotal = isServiceCancelled ? 0 : (service.totalPrice || 0);

              // Truy vết lấy đơn giá thực tế dịch vụ
              const sPrice = service.servicePrice || 
                             service.price || 
                             service.service?.price || 
                             (sTotal / sQty) || 
                             0;

              return (
                <tr 
                  key={`service-${index}`} 
                  style={{ 
                    borderBottom: `1px solid ${borderLight}`, 
                    backgroundColor: isServiceCancelled ? '#fef2f2' : '#f8fafc',
                    opacity: isServiceCancelled ? 0.7 : 1
                  }}
                >
                  <td style={{ padding: '10px 16px 10px 24px' }}>
                    <span style={{ fontSize: '13px', fontWeight: '600', color: isServiceCancelled ? '#ef4444' : '#334155' }}>
                      🛠️ {service.serviceName || service.service?.name || service.name} {isServiceCancelled && ' [ĐÃ HỦY]'}
                    </span>
                  </td>
                  <td style={{ padding: '10px 16px', textAlign: 'center', fontSize: '12px', fontWeight: '600', color: '#475569' }}>
                    {sQty}
                  </td>
                  <td style={{ 
                    padding: '10px 16px', 
                    textAlign: 'right', 
                    fontSize: '12px', 
                    color: '#475569',
                    textDecoration: isServiceCancelled ? 'line-through' : 'none' 
                  }}>
                    {sPrice.toLocaleString()}₫
                  </td>
                  <td style={{ padding: '10px 16px', textAlign: 'right', fontSize: '13px', fontWeight: '700', color: isServiceCancelled ? '#ef4444' : '#334155' }}>
                    {sTotal.toLocaleString()}₫
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>

        {/* 4. TỔNG KẾT CHI PHÍ */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '40px' }}>
          <div style={{ width: '300px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', fontSize: '13px', color: textMuted }}>
              <span>Tiền phòng gốc:</span>
              <span style={{ fontWeight: '600', color: textDark }}>{roomPriceAmount.toLocaleString()}₫</span>
            </div>

            {extraServices.length > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', fontSize: '13px', color: textMuted }}>
                <span>Tổng phí dịch vụ:</span>
                <span style={{ fontWeight: '600', color: textDark }}>{servicePriceAmount.toLocaleString()}₫</span>
              </div>
            )}

            {surchargeAmount > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', fontSize: '13px', color: '#d97706', fontWeight: '500' }}>
                <span>Phụ phí phát sinh:</span>
                <span>+{surchargeAmount.toLocaleString()}₫</span>
              </div>
            )}

            {depositAmount > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', fontSize: '13px', color: '#16a34a', fontWeight: '500' }}>
                <span>Tiền cọc trước:</span>
                <span>-{depositAmount.toLocaleString()}₫</span>
              </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0 0 0', marginTop: '8px', borderTop: `2px solid ${brandColor}` }}>
              <span style={{ fontSize: '14px', fontWeight: '900', color: '#dc2626' }}>TỔNG THANH TOÁN:</span>
              <span style={{ fontSize: '18px', fontWeight: '900', color: '#dc2626' }}>{totalAmount.toLocaleString()}₫</span>
            </div>
          </div>
        </div>

        {/* 5. CHỮ KÝ */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginTop: '20px', paddingTop: '20px', borderTop: `1px dashed ${textMuted}`, textAlign: 'center', fontSize: '12px', color: textMuted }}>
          <div>
            <p style={{ fontWeight: '700', color: textDark, margin: '0 0 60px 0', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Khách hàng ký nhận
            </p>
            <p style={{ fontStyle: 'italic', margin: '0' }}>(Ký và ghi rõ họ tên)</p>
          </div>
          <div>
            <p style={{ fontWeight: '700', color: textDark, margin: '0 0 60px 0', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Lễ tân xác nhận
            </p>
            <p style={{ fontWeight: '700', color: textDark, margin: '0' }}>
              {invoiceData.receiverName || 'Nhân viên trực quầy'}
            </p>
          </div>
        </div>
      </div>
    </>
  );
});

export default InvoiceTemplate;