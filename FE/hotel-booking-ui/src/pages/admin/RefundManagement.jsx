import React, { useState, useEffect, useCallback } from 'react';
import { 
  Table, Card, message, Tag, Typography, Tabs, Input, 
  Space, Button, Popconfirm, Avatar, DatePicker, Select, Flex
} from 'antd'; 
import { 
  WalletOutlined, CheckCircleOutlined, UserOutlined, SearchOutlined,
  CheckCircleFilled, ClockCircleFilled, CloseCircleFilled , ArrowLeftOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import * as ApiService from '../../api/ApiService';

const { Text, Title } = Typography;
const { RangePicker } = DatePicker;

const RefundManagement = () => {
  const navigate = useNavigate();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [activeTab, setActiveTab] = useState('PENDING'); 
  const [dateRange, setDateRange] = useState(null);
  const [filterMethod, setFilterMethod] = useState(null);
  
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  const loadData = useCallback(async (page, pageSize, status, keyword, method, range) => {
    setLoading(true);
    try {
      const res = await ApiService.getPendingRefunds({ 
        page: page - 1, 
        size: pageSize,
        status: status,
        keyword: keyword,
        method: method,
        startDate: range ? range[0].format('YYYY-MM-DD') : null,
        endDate: range ? range[1].format('YYYY-MM-DD') : null
      });
      
      const result = res?.result || res;
      setData(result.content || []);
      setPagination(prev => ({
        ...prev,
        current: page,
        pageSize: pageSize,
        total: result.totalElements || 0
      }));
    } catch (error) {
      console.error("Lỗi fetch dữ liệu:", error);
      message.error("Không thể tải danh sách hoàn tiền");
      setData([]); 
    } finally {
      setLoading(false);
    }
  }, []);

  
  useEffect(() => {
    const handler = setTimeout(() => {
      loadData(1, pagination.pageSize, activeTab, searchText, filterMethod, dateRange);
    }, 400);
    return () => clearTimeout(handler);
  }, [searchText]); 

  
  useEffect(() => {
    loadData(1, pagination.pageSize, activeTab, searchText, filterMethod, dateRange);
  }, [activeTab, filterMethod, dateRange]);

  const handleTableChange = (pag) => {
    loadData(pag.current, pag.pageSize, activeTab, searchText, filterMethod, dateRange);
  };

  const handleTabChange = (key) => {
    setActiveTab(key);
    setSearchText("");
    setFilterMethod(null);
    setDateRange(null);
  };

  const handleProcessRefund = async (record) => {
    try {

      await ApiService.approveManualRefund(
        record.bookingId,
        record.method
      );

      message.success(`Đã duyệt đơn #${record.bookingId}`);

      setData(prevData => prevData.filter(item => item.bookingId !== record.bookingId));

      await loadData(
        pagination.current,
        pagination.pageSize,
        activeTab,
        searchText,
        filterMethod,
        dateRange
      );

    } catch (error) {
      console.error(error);
      message.error(
        error?.response?.data?.message || "Có lỗi xảy ra"
      );
    }
  };

  const columns = [
    { title: 'Mã đơn', dataIndex: 'bookingId', width: 100, render: (id) => <Text copyable>{`#${id}`}</Text> },
    { 
      title: 'Khách hàng', key: 'customer', width: 220,
      render: (_, record) => (
        <Flex gap="small" align="center">
          <Avatar style={{ backgroundColor: '#e6f7ff', color: '#1890ff' }} icon={<UserOutlined />} />
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <Text strong style={{ fontSize: '14px' }}>{record.contactName}</Text>
            <Text type="secondary" style={{ fontSize: '12px' }}>{record.contactPhone}</Text>
          </div>
        </Flex>
      )
    },
    { title: 'Số tiền', dataIndex: 'amount', align: 'right', render: (val) => <Text strong style={{ color: '#cf1322' }}>{Number(val).toLocaleString('vi-VN')} ₫</Text> },
    { 
      title: 'Phương thức', dataIndex: 'method', align: 'center',
      render: (method) => (
        <Tag color={method === 'CASH' ? 'green' : 'blue'} style={{ borderRadius: '12px', padding: '0 10px' }}>
          {method === 'CASH' ? 'Tiền mặt' : 'Chuyển khoản'}
        </Tag>
      )
    },
    { title: 'Thời gian', dataIndex: 'createdAt', render: (date) => <Text type="secondary"> {date ? new Date(date).toLocaleString('vi-VN', { 
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit', second: '2-digit'
      }) : 'N/A'}</Text> },
    { 
      title: 'Trạng thái', dataIndex: 'status', align: 'center',
      render: (status) => {
        const statusConfig = {
          SUCCESS: { color: 'success', icon: <CheckCircleFilled />, text: 'THÀNH CÔNG' },
          PENDING: { color: 'warning', icon: <ClockCircleFilled />, text: 'ĐANG CHỜ' },
          FAILED: { color: 'error', icon: <CloseCircleFilled />, text: 'ĐÃ HỦY' }
        };
        const config = statusConfig[status] || { color: 'default', icon: null, text: status };
        return (
          <Tag 
            color={config.color} 
            icon={config.icon} 
            style={{ borderRadius: '12px', padding: '4px 12px', fontWeight: 600, border: 'none' }}
          >
            {config.text}
          </Tag>
        );
      } 
    },
    {
        title: 'Hành động', key: 'action', align: 'center',
        render: (_, record) => (
          (record.status === 'PENDING' && activeTab === 'PENDING') ? (
            <Popconfirm title="Duyệt hoàn tiền?" onConfirm={() => handleProcessRefund(record, 'APPROVED')}>
              <Button type="primary" shape="round" size="small" icon={<CheckCircleOutlined />}>Duyệt</Button>
            </Popconfirm>
          ) : <Text type="secondary" disabled>Đã đóng</Text>
        ),
    },
  ];

  return (
    <div className="refund-management-page" style={{ padding: '24px', background: '#f5f7fa', minHeight: '100vh' }}>

      {/* 🌟 Khu vực Tiêu đề trang và nút Quay lại thiết kế chuẩn bộ nhận diện */}
     <Flex justify="space-between" align="center" style={{ marginBottom: '24px' }} wrap="wrap" gap="middle">
        <div>
          <Title level={3} style={{ margin: 0, fontWeight: 700, color: '#1f1f1f' }}>
            Quản lý Giao dịch Hoàn tiền
          </Title>
          <Text type="secondary" style={{ fontSize: '14px' }}>
            Theo dõi danh sách, phê duyệt yêu cầu và xử lý hoàn trả tiền cọc/tiền phòng cho khách hàng do hủy đơn hoặc thay đổi phòng.
          </Text>
        </div>

       <Button
    icon={<ArrowLeftOutlined />}
    onClick={() => navigate('/admin/bookings')}
    className="btn-back"
>
    Quay lại đơn đặt phòng
</Button>
      </Flex>
      
      <Card 
        bordered={false} 
        style={{ borderRadius: '16px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}
        bodyStyle={{ padding: '24px' }}
      >
      

        {/* Đưa bộ lọc vào tabBarExtraContent của Tabs */}
        <Tabs 
          activeKey={activeTab}
          onChange={handleTabChange}
          type="card"
          tabBarExtraContent={
            <Flex gap="small" align="center">
              <Input 
                placeholder="Tìm mã đơn, SĐT..." 
                allowClear 
                prefix={<SearchOutlined />}
                style={{ width: 200, borderRadius: '8px' }}
                onChange={(e) => setSearchText(e.target.value)} 
              />
              <Select 
                placeholder="Phương thức" 
                allowClear 
                style={{ width: 140, borderRadius: '8px' }} 
                onChange={setFilterMethod}
              >
                <Select.Option value="CASH">Tiền mặt</Select.Option>
                <Select.Option value="BANK_TRANSFER">Chuyển khoản</Select.Option>
              </Select>
              <RangePicker 
                format="DD/MM/YYYY" 
                style={{ borderRadius: '8px', width: 220 }} 
                onChange={setDateRange} 
              />
            </Flex>
          }
          items={[
            { 
              key: 'PENDING', 
              label: 'Chờ xử lý', 
              children: <Table columns={columns} dataSource={data} rowKey="id" pagination={{...pagination, showSizeChanger: true}} onChange={handleTableChange} loading={loading} /> 
            },
            { 
              key: 'HISTORY', 
              label: 'Lịch sử hệ thống', 
              children: <Table columns={columns} dataSource={data} rowKey="id" pagination={{...pagination, showSizeChanger: true}} onChange={handleTableChange} loading={loading} /> 
            }
          ]}
        />
      </Card>
     <style>{`
      .refund-management-page .ant-table-thead > tr > th {
        background: #f8f9fa !important;
        font-weight: 600 !important;
      }

      .ant-select-selector,
      .ant-input-affine-wrapper,
      .ant-picker {
        border-radius: 8px !important;
      }

      .ant-tabs-nav {
        margin-bottom: 16px !important;
      }

      /* ===========================
          Nút Quay lại
      ============================ */

      .refund-management-page .btn-back{
          height:44px;
          padding:0 22px;
          border-radius:12px;

          display:flex;
          align-items:center;
          gap:8px;

          font-size:15px;
          font-weight:600;

          color:#1677ff;
          background:#edf5ff;
          border:1px solid #b7d7ff;

          box-shadow:0 2px 8px rgba(22,119,255,.08);

          transition:all .25s ease;
      }

      .refund-management-page .btn-back:hover{
          color:#fff !important;
          background:#1677ff !important;
          border-color:#1677ff !important;

          transform:translateY(-2px);

          box-shadow:0 8px 18px rgba(22,119,255,.28);
      }

      .refund-management-page .btn-back:active{
          transform:translateY(0);
      }

      .refund-management-page .btn-back .anticon{
          font-size:16px;
          transition:transform .25s;
      }

      .refund-management-page .btn-back:hover .anticon{
          transform:translateX(-4px);
      }

      .refund-management-page .btn-back:focus{
          color:#1677ff !important;
          background:#edf5ff !important;
          border-color:#1677ff !important;
      }
    `}</style>
    </div>
    
  );
  
};


export default RefundManagement;