import React from 'react';
import { Modal, Row, Col, Select, InputNumber } from 'antd';

const AddServiceModal = ({
  open,
  onCancel,
  availableServices,
  selectedServiceId,
  onChangeSelectedServiceId,
  quantityToAdd,
  onChangeQuantityToAdd,
  onConfirm,
}) => {
  return (
    <Modal
      title="Thêm dịch vụ phát sinh"
      open={open}
      onOk={onConfirm}
      onCancel={onCancel}
    >
      <Row gutter={[16, 16]}>
        <Col span={16}>
          <label>Dịch vụ:</label>
          <Select
            style={{ width: '100%', marginTop: 5 }}
            placeholder="Chọn dịch vụ"
            onChange={onChangeSelectedServiceId}
            value={selectedServiceId}
          >
            {availableServices.map((s) => (
              <Select.Option key={s.id} value={s.id}>
                {s.name} - {Number(s.price || 0).toLocaleString()}₫
              </Select.Option>
            ))}
          </Select>
        </Col>
        <Col span={8}>
          <label>Số lượng:</label>
          <InputNumber
            min={1}
            style={{ width: '100%', marginTop: 5 }}
            value={quantityToAdd}
            onChange={onChangeQuantityToAdd}
          />
        </Col>
      </Row>
    </Modal>
  );
};

export default AddServiceModal;
