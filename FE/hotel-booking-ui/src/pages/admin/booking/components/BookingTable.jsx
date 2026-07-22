import React from 'react';
import { Table, Tag, Button, Space, Tooltip } from 'antd';
import { EyeOutlined, CheckCircleOutlined, LogoutOutlined, PrinterOutlined } from '@ant-design/icons';
import { getTotalInvoiceAmount, STATUS_COLOR_MAP } from '../utils/bookingHelpers';
import '../styles/bookingStyles.css';

const BookingTable = ({
  bookings,
  loading,
  totalElements,
  currentPage,
  onPageChange,
  onOpenDetail,
  onCheckIn,
  onCheckOutPrompt,
  onPrintInvoice, // <-- Thêm prop xử lý in hóa đơn
}) => {
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      render: (id) => <span style={{ fontWeight: 600, color: '#64748b' }}>#{id}</span>,
    },
    {
      title: 'Khách hàng',
      dataIndex: 'customerName',
      key: 'customerName',
      render: (name) => <span style={{ fontWeight: 600, color: '#1e293b' }}>{name}</span>,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      render: (st) => {
        const color = STATUS_COLOR_MAP[st] || 'default';
        return (
          <Tag
            color={color}
            style={{
              borderRadius: '20px',
              padding: '2px 10px',
              fontWeight: 600,
              textTransform: 'uppercase',
              fontSize: '11px',
            }}
          >
            {st}
          </Tag>
        );
      },
    },
    {
      title: 'Tổng tiền',
      dataIndex: 'totalAmount',
      render: (v, record) => (
        <span style={{ color: '#e11d48', fontWeight: 700, fontSize: '15px' }}>
          {Number(getTotalInvoiceAmount(record) || 0).toLocaleString()}₫
        </span>
      ),
    },
    {
      title: 'Thao tác',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Tooltip title="Xem chi tiết">
            <Button
              shape="circle"
              icon={<EyeOutlined />}
              onClick={() => onOpenDetail(record.id)}
            />
          </Tooltip>
          {record.status === 'CONFIRMED' && (
            <Tooltip title="Check-in (Nhận phòng)">
              <Button
                shape="circle"
                type="primary"
                ghost
                icon={<CheckCircleOutlined />}
                onClick={() => onCheckIn(record.id)}
                disabled={!record.bookingRooms || record.bookingRooms.length === 0}
              />
            </Tooltip>
          )}
          {record.status === 'CHECKED_IN' && (
            <Tooltip title="Check-out (Trả phòng & thanh toán)">
              <Button
                shape="circle"
                danger
                ghost
                icon={<LogoutOutlined />}
                onClick={() => onCheckOutPrompt(record.id)}
              />
            </Tooltip>
          )}
          {/* Nút Xuất/In hóa đơn chỉ hiển thị khi đã CHECKED_OUT */}
          {record.status === 'CHECKED_OUT' && (
            <Tooltip title="In hóa đơn nhanh">
              <Button
                shape="circle"
                style={{ color: '#059669', borderColor: '#10b981' }}
                ghost
                icon={<PrinterOutlined />}
                onClick={() => onPrintInvoice(record.id)}
              />
            </Tooltip>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div className="pms-table-wrapper">
      <Table
        dataSource={bookings}
        columns={columns}
        rowKey="id"
        loading={loading}
        pagination={{
          current: currentPage + 1,
          total: totalElements,
          pageSize: 10,
          onChange: (p) => onPageChange(p - 1),
        }}
      />
    </div>
  );
};

export default BookingTable;