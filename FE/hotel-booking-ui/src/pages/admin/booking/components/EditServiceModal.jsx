import React from 'react';
import { Modal, InputNumber } from 'antd';

const EditServiceModal = ({ open, onCancel, newQuantity, onChangeNewQuantity, onConfirm }) => {
  return (
    <Modal
      title="Chỉnh sửa số lượng"
      open={open}
      onOk={onConfirm}
      onCancel={onCancel}
    >
      <InputNumber
        min={1}
        value={newQuantity}
        onChange={onChangeNewQuantity}
        style={{ width: '100%' }}
      />
    </Modal>
  );
};

export default EditServiceModal;
