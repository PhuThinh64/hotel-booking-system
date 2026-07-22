import React from 'react';
import { Modal, Space, Button, Alert, Input, Radio, Typography } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';

const { Text } = Typography;

const CancelRoomModal = ({
  open,
  onCancel,
  selectedBooking,
  cancelingRoomRecord,
  cancelReason,
  onChangeCancelReason,
  refundMethod,
  onChangeRefundMethod,
  onSubmit,
  loading,
}) => {
  const activeRooms = selectedBooking?.bookingRooms?.filter((r) => r.status !== 'CANCELLED') || [];
  const isLastRoom =
    activeRooms.length === 1 &&
    cancelingRoomRecord &&
    activeRooms[0].bookingRoomId === cancelingRoomRecord.bookingRoomId;
  const isManualBooking = selectedBooking?.paymentMethod !== 'VNPAY';
  const hasDeposit = Number(selectedBooking?.depositAmount || 0) > 0;

  return (
    <Modal
      title={
        <Space>
          <CloseCircleOutlined style={{ color: '#ff4d4f' }} />
          <Text strong>Xác nhận hủy phòng</Text>
        </Space>
      }
      open={open}
      onCancel={onCancel}
      footer={[
        <Button key="back" onClick={onCancel}>
          Quay lại
        </Button>,
        <Button key="submit" danger type="primary" loading={loading} onClick={onSubmit}>
          Xác nhận hủy
        </Button>,
      ]}
      centered
      width={450}
    >
      <div style={{ padding: '10px 0' }}>
        <Typography.Paragraph>
          Phòng sẽ bị hủy:{' '}
          <b>
            {cancelingRoomRecord?.roomType}{' '}
            {cancelingRoomRecord?.roomNumber && `(Số ${cancelingRoomRecord.roomNumber})`}
          </b>
        </Typography.Paragraph>

        <div style={{ margin: '16px 0' }}>
          <Typography.Paragraph strong>
            <span style={{ color: 'red' }}>*</span> Lý do hủy phòng này:
          </Typography.Paragraph>
          <Input.TextArea
            rows={3}
            placeholder="Nhập lý do hủy phòng lẻ..."
            value={cancelReason}
            onChange={(e) => onChangeCancelReason(e.target.value)}
          />
        </div>

        {isLastRoom && isManualBooking && hasDeposit ? (
          <div style={{ marginTop: 16 }}>
            <Alert
              message="⚠️ Đây là phòng cuối cùng của đơn hàng!"
              description="Hủy phòng này đồng nghĩa với việc hủy hoàn toàn đơn hàng này. Vui lòng lựa chọn phương thức hoàn trả tiền cọc phát sinh:"
              type="warning"
              showIcon
              style={{ marginBottom: 16 }}
            />
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
            {refundMethod === 'BANK_TRANSFER' && (
              <div style={{ marginTop: 12, color: '#1890ff', fontStyle: 'italic' }}>
                * Lưu ý: Đơn sẽ chuyển sang trạng thái "Chờ hoàn tiền". Hãy vào mục "Quản lý hoàn
                tiền" để duyệt sau khi chuyển khoản thành công.
              </div>
            )}
          </div>
        ) : (
          <Typography.Paragraph type="secondary">
            Tiền phòng này sẽ được khấu trừ khỏi tổng giá trị hóa đơn. Nếu phát sinh tiền dư, hệ thống
            sẽ hỗ trợ cấn trừ hoặc hoàn lại tại bước Check-out.
          </Typography.Paragraph>
        )}
      </div>
    </Modal>
  );
};

export default CancelRoomModal;
