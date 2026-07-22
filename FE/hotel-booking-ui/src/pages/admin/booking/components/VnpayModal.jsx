import React from 'react';
import { Modal, Button } from 'antd';
import { CheckCircleOutlined } from '@ant-design/icons';

const VnpayModal = ({ open, onCancel }) => {
  return (
    <Modal
      title="Thanh toán qua VNPay"
      open={open}
      onCancel={onCancel}
      footer={[
        <Button key="confirm" type="primary" onClick={onCancel}>
          Đóng
        </Button>,
      ]}
      centered
    >
      <div style={{ textAlign: 'center' }}>
        <CheckCircleOutlined style={{ fontSize: '48px', color: '#1890ff', marginBottom: '16px' }} />
        <h3>Hệ thống đã mở hóa đơn VNPay ở một Tab mới!</h3>
      </div>
    </Modal>
  );
};

export default VnpayModal;
