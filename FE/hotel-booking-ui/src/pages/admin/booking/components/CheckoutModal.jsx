import React, { useMemo } from 'react';
import { Modal, Descriptions, Tag, Divider, Radio, Typography } from 'antd';
import { getServiceAmount } from '../utils/bookingHelpers';

const { Text } = Typography;

const CheckoutModal = ({
  open,
  onCancel,
  selectedBooking,
  paymentMethod,
  onChangePaymentMethod,
  onConfirmCheckout,
  refundMethod,
  onChangeRefundMethod,
}) => {
  
  const booking = useMemo(() => {
    if (!selectedBooking) return null;
    return selectedBooking.result ? selectedBooking.result : selectedBooking;
  }, [selectedBooking]);

  
  const financialData = useMemo(() => {
    if (!booking) return null;

    const isOriginalVnpay = booking?.originalVnpay === true;
    const room = Number(booking.roomAmount || 0);
    const service = Number(
      booking.serviceAmount ??
      getServiceAmount(booking) ??
      0
    );
    const surcharge = Number(booking.surchargeAmount || 0);
    const deposit = Number(booking.depositAmount || 0);
    
   const total = Number(booking.totalAmount || 0);

    const remaining =
      Number(booking.remainingAmount || 0);

    const refundAmount =
      Number(booking.refundAmount || 0);

    return { room, service, surcharge, deposit, total, remaining, refundAmount, isOriginalVnpay };
  }, [booking]);

  if (!booking || !financialData) return null;

  const { room, service, surcharge, deposit, total, remaining, refundAmount, isOriginalVnpay } = financialData;

  return (
    <Modal
      title={`Hóa đơn thanh toán #${booking.id || booking.bookingCode}`}
      open={open}
      onCancel={onCancel}
      onOk={onConfirmCheckout}
      okText="Xác nhận"
      cancelText="Hủy"
      width={600}
      zIndex={1001}
    >
      <Descriptions column={1} bordered size="small">
        <Descriptions.Item label="Tiền phòng">{room.toLocaleString()}₫</Descriptions.Item>
        <Descriptions.Item label="Tiền dịch vụ">{service.toLocaleString()}₫</Descriptions.Item>
        
        {surcharge > 0 && (
          <Descriptions.Item label="Phụ phí trả phòng trễ">
            <Text type="danger">+ {surcharge.toLocaleString()}₫</Text>
          </Descriptions.Item>
        )}

        <Descriptions.Item label="Tổng cộng">{total.toLocaleString()}₫</Descriptions.Item>

        <Descriptions.Item label="Tiền cọc đã trả">
          <Text type="success">- {deposit.toLocaleString()}₫</Text>
        </Descriptions.Item>

        <Descriptions.Item label="Trạng thái">
          {remaining > 0 ? (
            <Text type="danger" strong style={{ fontSize: '15px' }}>Còn phải thu: {remaining.toLocaleString()}₫</Text>
          ) : refundAmount > 0 ? (
            <Text type="secondary" strong style={{ fontSize: '15px', color: '#1890ff' }}>Khách dư: {refundAmount.toLocaleString()}₫</Text>
          ) : (
            <Tag color="success" style={{ fontSize: '14px' }}>ĐÃ THANH TOÁN ĐỦ</Tag>
          )}
        </Descriptions.Item>
      </Descriptions>

      {/* --- PHẦN THU THÊM TIỀN --- */}
      {remaining > 0 && (
        <div style={{ marginTop: '20px' }}>
          <Divider orientation="left">Phương thức thanh toán còn thiếu</Divider>
          <Radio.Group
            value={paymentMethod}
            onChange={(e) => onChangePaymentMethod(e.target.value)}
            optionType="button"
            buttonStyle="solid"
            style={{ width: '100%', textAlign: 'center' }}
          >
            <Radio.Button value="CASH" style={{ width: '50%' }}>Tiền mặt</Radio.Button>
            <Radio.Button value="VNPAY" style={{ width: '50%' }}>VNPay</Radio.Button>
          </Radio.Group>
        </div>
      )}

      {/* --- PHẦN HOÀN TIỀN --- */}
      {refundAmount > 0 && (
        <div style={{ marginTop: 20, padding: 15, background: '#f6ffed', borderRadius: 8, border: '1px solid #b7eb8f' }}>
          <Text strong style={{ color: '#389e0d' }}>Khách đang dư {refundAmount.toLocaleString()}₫</Text>
          
          {isOriginalVnpay ? (
            <div style={{ marginTop: '10px' }}>
              <Text type="secondary">Hệ thống sẽ tự động hoàn tiền qua cổng thanh toán VNPAY.</Text>
            </div>
          ) : (
            <div style={{ marginTop: '10px' }}>
              <Text>Chọn phương thức hoàn tiền:</Text>
              <Radio.Group 
                value={refundMethod} 
                onChange={(e) => onChangeRefundMethod(e.target.value)} 
                style={{ display: 'block', marginTop: '5px' }}
              >
                <Radio value="CASH">Tiền mặt tại quầy</Radio>
                <Radio value="BANK_TRANSFER">Chuyển khoản</Radio>
              </Radio.Group>
            </div>
          )}
        </div>
      )}
    </Modal>
  );
};

export default CheckoutModal;