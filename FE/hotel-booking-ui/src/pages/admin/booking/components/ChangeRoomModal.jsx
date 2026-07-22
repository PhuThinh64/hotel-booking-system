import React from 'react';
import { Modal, Alert, Row, Col, Select } from 'antd';

const ChangeRoomModal = ({
  open,
  onCancel,
  roomTypesList,
  selectedRoomTypeIdToChange,
  onChangeSelectedRoomTypeId,
  onConfirm,
}) => {
  return (
    <Modal
      title="Thay đổi loại phòng đặt trước"
      open={open}
      onOk={onConfirm}
      onCancel={onCancel}
      okText="Xác nhận thay đổi"
      cancelText="Hủy bỏ"
    >
      <div style={{ marginBottom: 16 }}>
        <Alert
          message="Hệ thống sẽ cập nhật loại phòng và tính lại tổng tiền hóa đơn. Phòng vật lý hiện tại (nếu có) sẽ bị hủy gán để xếp lại sau lúc check-in."
          type="warning"
          showIcon
        />
      </div>
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <label>Chọn Loại Phòng Mới:</label>
          <Select
            style={{ width: '100%', marginTop: 8 }}
            placeholder="-- Chọn loại phòng --"
            onChange={onChangeSelectedRoomTypeId}
            value={selectedRoomTypeIdToChange}
          >
            {roomTypesList.map((type) => (
              <Select.Option key={type.id} value={type.id} disabled={type.availableCount <= 0}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span>
                    {type.name} — {Number(type.price || 0).toLocaleString()}₫
                  </span>
                  <span
                    style={{
                      color: type.availableCount <= 0 ? 'red' : 'green',
                      fontWeight: 'bold',
                    }}
                  >
                    {type.availableCount <= 0 ? '(Hết phòng)' : `(Còn ${type.availableCount} phòng)`}
                  </span>
                </div>
              </Select.Option>
            ))}
          </Select>
        </Col>
      </Row>
    </Modal>
  );
};

export default ChangeRoomModal;
