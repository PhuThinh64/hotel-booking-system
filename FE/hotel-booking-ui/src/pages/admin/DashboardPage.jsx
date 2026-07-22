import React, { useEffect, useState, useCallback, useMemo } from 'react';
import {
  Row, Col, Card, Statistic, Progress, Table, Typography, Tag,
  DatePicker, Spin, Empty, Alert, Space, Tooltip, Badge, Divider
} from 'antd';
import {
  DollarOutlined, HomeOutlined, UserOutlined, LoginOutlined, LogoutOutlined,
  BarChartOutlined, PieChartOutlined, AppstoreOutlined, PhoneOutlined
} from '@ant-design/icons';
import {
  ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell,
  CartesianGrid, XAxis, YAxis, Tooltip as RechartsTooltip, Legend
} from 'recharts';
import dayjs from 'dayjs';
import * as ApiService from '../../api/ApiService';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;


const STATUS_LABELS = { PENDING_DEPOSIT: 'Chờ cọc', CONFIRMED: 'Đã xác nhận', CHECKED_IN: 'Đang lưu trú', CHECKED_OUT: 'Đã trả phòng', CANCELLED: 'Đã huỷ', REJECTED: 'Từ chối' , PENDING_REFUND: 'Chờ hoàn tiền'};
const STATUS_COLORS = { PENDING_DEPOSIT: '#faad14', CONFIRMED: '#1677ff', CHECKED_IN: '#13c2c2', CHECKED_OUT: '#52c41a', CANCELLED: '#ff4d4f', REJECTED: '#722ed1', PENDING_REFUND: '#fa8c16' };

const ROOM_STATUS_CONFIG = {
  AVAILABLE: { label: 'Trống', color: '#389e0d', bg: '#f6ffed', border: '#b7eb8f', badge: 'success' },
  OCCUPIED: { label: 'Có khách', color: '#0958d9', bg: '#e6f4ff', border: '#91caff', badge: 'processing' },
  CLEANING: { label: 'Đang dọn', color: '#d46b08', bg: '#fff7e6', border: '#ffd591', badge: 'warning' },
  MAINTENANCE: { label: 'Bảo trì', color: '#cf1322', bg: '#fff1f0', border: '#ffa39e', badge: 'error' }
};

const cardStyle = { borderRadius: 16, border: 0, boxShadow: '0 4px 20px rgba(0,0,0,0.03)', height: '100%' };

const DashboardPage = () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user?.role === 'ROLE_ADMIN';

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [operational, setOperational] = useState({});
  const [analytical, setAnalytical] = useState({});
  const [lastUpdated, setLastUpdated] = useState(null);
  const [dateRange, setDateRange] = useState([dayjs().startOf('year'), dayjs()]);

  const fetchDashboard = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const requests = [ApiService.getOperationalStats()];
      if (isAdmin) {
        requests.push(ApiService.getAnalyticalStats(dateRange[0].format('YYYY-MM-DD'), dateRange[1].format('YYYY-MM-DD')));
      }
      const responses = await Promise.all(requests);
      setOperational(responses[0] || {});
      if (isAdmin) setAnalytical(responses[1] || {});
      setLastUpdated(new Date());
    } catch (err) {
      console.error(err);
      setError(err?.response?.data?.message || 'Không thể tải Dashboard');
    } finally {
      setLoading(false);
    }
  }, [isAdmin, dateRange]);

  useEffect(() => { fetchDashboard(); }, [fetchDashboard]);

  const diffDays = useMemo(() => dateRange[1].diff(dateRange[0], 'day'), [dateRange]);

  const revenueChartData = useMemo(() => {
    if (!analytical) return [];
    if (diffDays <= 60) {
      return (analytical.dailyRevenues || []).map(item => ({
        label: dayjs(item.date).format('DD/MM'), revenue: Number(item.totalAmount || 0)
      }));
    }
    return (analytical.monthlyRevenues || []).map(item => ({
      label: `T${item.month}/${item.year}`, revenue: Number(item.totalAmount || 0)
    }));
  }, [analytical, diffDays]);

  const statusChartData = useMemo(() => {
    return (analytical.statusDistributions || []).map(item => ({
      name: STATUS_LABELS[item.status] || item.status,
      value: item.count,
      color: STATUS_COLORS[item.status] || '#8c8c8c'
    }));
  }, [analytical]);

  const bookingColumns = [
    { title: 'Mã đơn', dataIndex: 'bookingCode', width: 120, render: val => <Tag color="blue">{val}</Tag> },
    { title: 'Khách hàng', dataIndex: 'customerName', render: val => <b>{val}</b> },
    { title: 'Liên hệ', dataIndex: 'contactPhone', render: val => <Space><PhoneOutlined style={{color: '#bfbfbf'}}/>{val}</Space> },
    { title: 'Tổng tiền', dataIndex: 'totalAmount', align: 'right', render: val => <Text strong type="success">{Number(val || 0).toLocaleString('vi-VN')}₫</Text> }
  ];

  return (
    <Spin spinning={loading} size="large">
      <div style={{ padding: 24, background: '#f4f7fe', minHeight: '100vh' }}>
        
        {/* HEADER */}
        <Row justify="space-between" align="middle" style={{ marginBottom: 24 }}>
          <Col>
            <Title level={3} style={{ margin: 0, color: '#1f2937', fontWeight: 700 }}>
              {isAdmin ? '📊 Hotel Analytics Dashboard' : '🛎️ Reception Desk'}
            </Title>
            <Text type="secondary" style={{ fontSize: 13 }}>
              {lastUpdated ? `Cập nhật lần cuối: ${dayjs(lastUpdated).format('HH:mm:ss - DD/MM/YYYY')}` : 'Đang lấy dữ liệu...'}
            </Text>
          </Col>
          {isAdmin && (
            <Col><RangePicker value={dateRange} allowClear={false} onChange={(val) => { if (val) setDateRange(val); }} style={{ borderRadius: 8 }} /></Col>
          )}
        </Row>

        {error && <Alert message="Lỗi hệ thống" description={error} type="error" showIcon style={{ marginBottom: 24, borderRadius: 12 }} />}

        {/* ========================= */}
        {/* ADMIN KPI ROW */}
        {/* ========================= */}
        {isAdmin && (
          <>
            <Row gutter={[16, 16]}>
              {[
                { title: "Doanh Thu Thực Tế", value: analytical.totalRevenue || 0, prefix: <DollarOutlined />, isMoney: true, color: '#1677ff' },
                { title: "Hiệu Suất Phòng (Occ)", value: analytical.occupancyRate || 0, prefix: <HomeOutlined />, isPercent: true, color: '#52c41a' },
                { title: "ADR (Giá trung bình)", value: analytical.adr || 0, isMoney: true, color: '#fa8c16' },
                { title: "RevPAR", value: analytical.revPar || 0, isMoney: true, color: '#eb2f96' },
              ].map((kpi, idx) => (
                <Col xs={24} md={12} xl={6} key={idx}>
                  <Card style={cardStyle} bodyStyle={{ padding: 20 }}>
                    <Statistic 
                      title={<span style={{ color: '#8c8c8c', fontWeight: 500 }}>{kpi.title}</span>}
                      value={kpi.value} 
                      prefix={kpi.prefix && <span style={{ color: kpi.color, marginRight: 8 }}>{kpi.prefix}</span>}
                      suffix={kpi.isPercent ? "%" : ""}
                      formatter={v => kpi.isMoney ? Number(v).toLocaleString('vi-VN') + ' ₫' : Number(v).toLocaleString('vi-VN')}
                      valueStyle={{ fontSize: 28, fontWeight: 700, color: '#1f2937' }}
                    />
                    {kpi.isPercent && <Progress percent={kpi.value} showInfo={false} strokeColor={kpi.color} trailColor="#f0f0f0" style={{ marginTop: 8 }} />}
                  </Card>
                </Col>
              ))}
            </Row>

            <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
              {[
                { title: "Tổng Đơn", val: analytical.totalBookings },
                { title: "Đã Xác Nhận", val: analytical.confirmedBookings },
                { title: "Đang Lưu Trú", val: analytical.checkedInBookings },
                { title: "Đã Hủy", val: analytical.cancelledBookings }
              ].map((subKpi, idx) => (
                <Col xs={24} md={12} xl={6} key={idx}>
                  <Card style={{ ...cardStyle, background: '#fff', borderLeft: `4px solid ${['#1677ff', '#52c41a', '#13c2c2', '#ff4d4f'][idx]}` }}>
                    <Statistic title={<Text type="secondary">{subKpi.title}</Text>} value={subKpi.val || 0} valueStyle={{ fontSize: 22, fontWeight: 600 }} />
                  </Card>
                </Col>
              ))}
            </Row>

            {/* CHARTS */}
            <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
              <Col xs={24} xl={16}>
                <Card style={cardStyle} title={<><BarChartOutlined style={{color: '#1677ff'}}/> Tăng trưởng doanh thu</>}>
                  <ResponsiveContainer width="100%" height={320}>
                    <BarChart data={revenueChartData}>
                      <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e5e7eb" />
                      <XAxis dataKey="label" axisLine={false} tickLine={false} />
                      <YAxis axisLine={false} tickLine={false} tickFormatter={v => `${(v/1000000).toFixed(0)}M`} />
                      <RechartsTooltip formatter={v => [Number(v).toLocaleString('vi-VN') + ' ₫', 'Doanh thu']} cursor={{fill: 'transparent'}} />
                      <Bar dataKey="revenue" fill="url(#colorRevenue)" radius={[6, 6, 0, 0]}>
                         {/* Gradient effect for bars */}
                         <defs>
                           <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                             <stop offset="5%" stopColor="#1677ff" stopOpacity={0.9}/>
                             <stop offset="95%" stopColor="#1677ff" stopOpacity={0.6}/>
                           </linearGradient>
                         </defs>
                      </Bar>
                    </BarChart>
                  </ResponsiveContainer>
                </Card>
              </Col>

              <Col xs={24} xl={8}>
                <Card style={cardStyle} title={<><PieChartOutlined style={{color: '#fa8c16'}}/> Tỉ lệ trạng thái đơn</>}>
                  <ResponsiveContainer width="100%" height={320}>
                    <PieChart>
                      <Pie data={statusChartData} dataKey="value" nameKey="name" innerRadius={80} outerRadius={110} paddingAngle={5} label={({ name, percent }) => `${name} (${(percent * 100).toFixed(0)}%)`}
  labelLine={true}>
                        {statusChartData.map((item, idx) => <Cell key={idx} fill={item.color} stroke="none" />)}
                      </Pie>
                      <Legend iconType="circle" />
                      <RechartsTooltip contentStyle={{ borderRadius: 8, border: 'none', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }} />
                    </PieChart>
                  </ResponsiveContainer>
                </Card>
              </Col>
            </Row>
          </>
        )}

        {/* ========================= */}
        {/* RECEPTION KPI ROW */}
        {/* ========================= */}
        {!isAdmin && (
          <Row gutter={[16, 16]}>
            {[
              { title: "Check-In Hôm Nay", val: operational.todayCheckInCount, icon: <LoginOutlined style={{color: '#1677ff'}}/>, bg: '#e6f4ff' },
              { title: "Check-Out Hôm Nay", val: operational.todayCheckOutCount, icon: <LogoutOutlined style={{color: '#fa8c16'}}/>, bg: '#fff7e6' },
              { title: "Phòng Trống", val: operational.availableRooms, icon: <HomeOutlined style={{color: '#52c41a'}}/>, bg: '#f6ffed' },
              { title: "Đang Có Khách", val: operational.occupiedRooms, icon: <UserOutlined style={{color: '#722ed1'}}/>, bg: '#f9f0ff' }
            ].map((kpi, idx) => (
              <Col xs={24} sm={12} xl={6} key={idx}>
                <Card style={{ ...cardStyle, display: 'flex', alignItems: 'center' }} bodyStyle={{ padding: 20, width: '100%' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                    <div>
                      <Text type="secondary" style={{ fontSize: 14 }}>{kpi.title}</Text>
                      <div style={{ fontSize: 28, fontWeight: 700, color: '#1f2937', marginTop: 4 }}>{kpi.val || 0}</div>
                    </div>
                    <div style={{ width: 48, height: 48, borderRadius: '50%', background: kpi.bg, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 24 }}>
                      {kpi.icon}
                    </div>
                  </div>
                </Card>
              </Col>
            ))}
          </Row>
        )}

        {/* ========================= */}
        {/* ROOM INVENTORY BOARD */}
        {/* ========================= */}
        <Card style={{ ...cardStyle, marginTop: 24 }} bodyStyle={{ padding: '24px 24px 8px 24px' }} title={<Space><AppstoreOutlined /> Tình trạng phòng thực tế</Space>}>
          {operational.roomTypeGroups?.length ? operational.roomTypeGroups.map((group) => (
            <div key={group.roomTypeName} style={{ marginBottom: 24 }}>
              <Divider orientation="left" plain style={{ margin: '0 0 16px 0' }}>
                <Text strong style={{ fontSize: 16, color: '#4b5563' }}>{group.roomTypeName}</Text>
              </Divider>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16 }}>
                {group.rooms?.map((room) => {
                  const config = ROOM_STATUS_CONFIG[room.status] || { color: '#8c8c8c', bg: '#f5f5f5', border: '#d9d9d9', badge: 'default', label: 'Không rõ' };
                  return (
                    <Tooltip key={room.id} title={`${config.label} - ${room.roomNumber}`}>
                      <div style={{
                        width: 95, height: 80, borderRadius: 12, padding: 8, cursor: 'pointer',
                        background: config.bg, border: `1px solid ${config.border}`,
                        display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.02)', transition: 'all 0.2s'
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-2px)'}
                      onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                      >
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                           <Badge status={config.badge} />
                           <span style={{ fontSize: 10, color: config.color, fontWeight: 600 }}>{config.label}</span>
                        </div>
                        <div style={{ textAlign: 'center', fontSize: 18, fontWeight: 700, color: '#1f2937' }}>
                          {room.roomNumber}
                        </div>
                      </div>
                    </Tooltip>
                  );
                })}
              </div>
            </div>
          )) : <Empty description="Không có dữ liệu phòng" />}
        </Card>

        {/* ========================= */}
        {/* TABLES */}
        {/* ========================= */}
        <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
          <Col xs={24} lg={12}>
            <Card style={cardStyle} title={<><LoginOutlined style={{color: '#1677ff'}}/> Khách đến hôm nay</>} bodyStyle={{ padding: 0 }}>
              <Table rowKey="id" columns={bookingColumns} dataSource={operational.todayCheckInList || []} pagination={{ pageSize: 5 }} size="middle" />
            </Card>
          </Col>
          <Col xs={24} lg={12}>
            <Card style={cardStyle} title={<><LogoutOutlined style={{color: '#fa8c16'}}/> Khách đi hôm nay</>} bodyStyle={{ padding: 0 }}>
              <Table rowKey="id" columns={bookingColumns} dataSource={operational.todayCheckOutList || []} pagination={{ pageSize: 5 }} size="middle" />
            </Card>
          </Col>
        </Row>

      </div>
    </Spin>
  );
};

export default DashboardPage;