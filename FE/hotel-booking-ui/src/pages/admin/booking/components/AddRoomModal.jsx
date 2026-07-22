import React from 'react';
import { Modal, Alert, Row, Col, Select } from 'antd';

const AddRoomModal = ({
  open,
  onCancel,
  availableRoomTypes,
  selectedRoomTypeToAdd,
  onChangeSelectedRoomType,
  onConfirm,
}) => {
  const selectedType = availableRoomTypes.find((rt) => rt.id === selectedRoomTypeToAdd);
  const isOkDisabled = !selectedRoomTypeToAdd || (selectedType && selectedType.availableCount <= 0);

  return (
    <Modal
      title="Chọn loại phòng muốn thêm"
      open={open}
      onCancel={onCancel}
      okButtonProps={{ disabled: isOkDisabled }}
      onOk={onConfirm}
    >
      <div style={{ marginBottom: 16 }}>
        <Alert
          message="Hệ thống sẽ thêm loại phòng mới vào đơn đặt phòng này. Vui lòng kiểm tra lại số lượng phòng trống trước khi xác nhận."
          type="info"
          showIcon
        />
      </div>

      <Row gutter={[16, 16]}>
        <Col span={24}>
          <label>Chọn Loại Phòng:</label>
          <Select
            style={{ width: '100%', marginTop: 8 }}
            placeholder="-- Chọn loại phòng --"
            onChange={onChangeSelectedRoomType}
            value={selectedRoomTypeToAdd}
          >
            {availableRoomTypes.map((rt) => (
              <Select.Option key={rt.id} value={rt.id} disabled={rt.availableCount <= 0}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span>
                    {rt.name} — {Number(rt.price || 0).toLocaleString()}₫
                  </span>
                  <span
                    style={{
                      color: rt.availableCount <= 0 ? 'red' : 'green',
                      fontWeight: 'bold',
                    }}
                  >
                    {rt.availableCount <= 0 ? '(Hết phòng)' : `(Còn ${rt.availableCount} phòng)`}
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

export default AddRoomModal;
