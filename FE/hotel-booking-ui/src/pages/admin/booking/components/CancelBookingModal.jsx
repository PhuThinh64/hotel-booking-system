import React from 'react';
import { Modal, Space, Button, Alert, Input, Radio, Typography } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';

const { Text } = Typography;

const CancelBookingModal = ({
  open,
  onCancel,
  selectedBooking,
  cancelReason,
  onChangeCancelReason,
  refundMethod,
  onChangeRefundMethod,
  onSubmit,
  loading,
}) => {
  if (!selectedBooking) return null;

  return (
    <Modal
      title={
        <Space>
          <CloseCircleOutlined style={{ color: '#ff4d4f' }} />
          <Text strong>Xác nhận hủy toàn bộ đơn hàng</Text>
        </Space>
      }
      open={open}
      onCancel={onCancel}
      footer={[
        <Button key="back" onClick={onCancel}>
          Quay lại
        </Button>,
        <Button key="submit" danger type="primary" loading={loading} onClick={onSubmit}>
          Xác nhận Hủy đơn
        </Button>,
      ]}
      centered
    >
      <div style={{ padding: '10px 0' }}>
        <Alert
          message="Lưu ý"
          description="Hành động này không thể hoàn tác. Toàn bộ phòng sẽ được giải phóng lập tức."
          type="warning"
          showIcon
          style={{ marginBottom: 16 }}
        />

        <div style={{ marginBottom: 16 }}>
          <Typography.Paragraph strong>
            <span style={{ color: 'red' }}>*</span> Lý do hủy đơn hàng:
          </Typography.Paragraph>
          <Input.TextArea
            rows={3}
            placeholder="Nhập lý do chi tiết hủy đơn (Ví dụ: Khách đổi lịch, lỗi hệ thống...)"
            value={cancelReason}
            onChange={(e) => onChangeCancelReason(e.target.value)}
          />
        </div>

        {selectedBooking.depositAmount > 0 ? (
          <>
            {selectedBooking.paymentMethod === 'VNPAY' ? (
              <Alert
                message="Hoàn tiền tự động qua VNPay"
                description="Đơn hàng này thanh toán qua VNPay. Hệ thống sẽ tự động hoàn tiền về tài khoản ngân hàng của khách theo quy trình online."
                type="info"
                showIcon
                style={{ marginBottom: 16 }}
              />
            ) : (
              <>
                <Typography.Paragraph strong>
                  Hình thức hoàn trả tiền cọc cho khách:
                </Typography.Paragraph>
                <Radio.Group
                  value={refundMethod}
                  onChange={(e) => onChangeRefundMethod(e.target.value)}
                  style={{ display: 'flex', flexDirection: 'column', gap: 10 }}
                >
                  <Radio value="CASH">
                    Trả bằng Tiền mặt (Lễ tân trả trực tiếp tiền mặt)
                  </Radio>
                  <Radio value="BANK_TRANSFER">
                    Chuyển khoản Ngân hàng (Admin sẽ duyệt chuyển tiền sau)
                  </Radio>
                </Radio.Group>
                <div style={{ marginTop: 15, color: '#1890ff', fontStyle: 'italic' }}>
                  * Lưu ý: Đơn sẽ được chuyển sang trạng thái "Chờ hoàn tiền". Hãy vào "Quản lý hoàn
                  tiền" để duyệt xác nhận sau khi đưa tiền hoặc bank tiền thành công.
                </div>
              </>
            )}
          </>
        ) : (
          <Typography.Paragraph>
            Đơn hàng này chưa thanh toán cọc. Hệ thống sẽ hủy đơn trực tiếp và không phát sinh hoàn
            tiền.
          </Typography.Paragraph>
        )}
      </div>
    </Modal>
  );
};

export default CancelBookingModal;
