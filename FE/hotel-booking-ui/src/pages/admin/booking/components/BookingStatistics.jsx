import React from 'react';
import {
  FileSearchOutlined,
  WalletOutlined,
  CheckOutlined,
  HomeOutlined,
  CoffeeOutlined,
  RollbackOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons';
import '../styles/bookingStyles.css';

const BookingStatistics = ({ totalElements, bookings }) => {
  const countByStatus = (status) => bookings.filter((b) => b.status === status).length;

  return (
    <div className="pms-kpi-row">
      <div className="pms-kpi-card">
        <div className="pms-kpi-title">Tổng đơn</div>
        <div className="pms-kpi-value-row">
          <div className="pms-kpi-value">{totalElements}</div>
        </div>
        <FileSearchOutlined className="pms-kpi-icon" />
      </div>

      <div className="pms-kpi-card pending-deposit">
        <div className="pms-kpi-title">Chờ cọc</div>
        <div className="pms-kpi-value-row">
          <div className="pms-kpi-value" style={{ color: '#fa8c16' }}>
            {countByStatus('PENDING_DEPOSIT')}
          </div>
        </div>
        <WalletOutlined className="pms-kpi-icon" />
      </div>

      <div className="pms-kpi-card confirmed">
        <div className="pms-kpi-title">Xác nhận</div>
        <div className="pms-kpi-value-row">
          <div className="pms-kpi-value" style={{ color: '#13c2c2' }}>
            {countByStatus('CONFIRMED')}
          </div>
        </div>
        <CheckOutlined className="pms-kpi-icon" />
      </div>

      <div className="pms-kpi-card checked-in">
        <div className="pms-kpi-title">Đang ở</div>
        <div className="pms-kpi-value-row">
          <div className="pms-kpi-value" style={{ color: '#1677ff' }}>
            {countByStatus('CHECKED_IN')}
          </div>
        </div>
        <HomeOutlined className="pms-kpi-icon" />
      </div>

      <div className="pms-kpi-card checked-out">
        <div className="pms-kpi-title">Hoàn tất</div>
        <div className="pms-kpi-value-row">
          <div className="pms-kpi-value" style={{ color: '#52c41a' }}>
            {countByStatus('CHECKED_OUT')}
          </div>
        </div>
        <CoffeeOutlined className="pms-kpi-icon" />
      </div>

      <div className="pms-kpi-card pending-refund">
        <div className="pms-kpi-title">Chờ hoàn</div>
        <div className="pms-kpi-value-row">
          <div className="pms-kpi-value" style={{ color: '#722ed1' }}>
            {countByStatus('PENDING_REFUND')}
          </div>
        </div>
        <RollbackOutlined className="pms-kpi-icon" />
      </div>

      <div className="pms-kpi-card cancelled">
        <div className="pms-kpi-title">Đã hủy</div>
        <div className="pms-kpi-value-row">
          <div className="pms-kpi-value" style={{ color: '#ff4d4f' }}>
            {countByStatus('CANCELLED')}
          </div>
        </div>
        <CloseCircleOutlined className="pms-kpi-icon" />
      </div>
    </div>
  );
};

export default BookingStatistics;
