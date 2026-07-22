
import React from 'react';
import { Menu, Layout } from 'antd';
import { Link } from 'react-router-dom';
import { 
  CoffeeOutlined, BankOutlined, DashboardOutlined, TeamOutlined,
  AppstoreOutlined, HomeOutlined, CalendarOutlined, WalletOutlined, HistoryOutlined
} from '@ant-design/icons';

const { Sider } = Layout;

const Sidebar = ({ currentPath, collapsed }) => {
  
  const user = JSON.parse(localStorage.getItem('user')) || {};
  const isAdmin = user.role === 'ROLE_ADMIN';

  
  const allMenuItems = [
    { key: '/admin/dashboard', icon: <DashboardOutlined />, label: <Link to="/admin/dashboard">Dashboard</Link> },
    { key: '/admin/bookings', icon: <CalendarOutlined />, label: <Link to="/admin/bookings">Quản lý Đặt phòng</Link> },
    { key: '/admin/rooms', icon: <HomeOutlined />, label: <Link to="/admin/rooms">Quản lý Phòng</Link> },
    { key: '/admin/room-types', icon: <AppstoreOutlined />, label: <Link to="/admin/room-types">Quản lý Loại Phòng</Link> },
    { key: '/admin/services', icon: <CoffeeOutlined />, label: <Link to="/admin/services">Dịch vụ đi kèm</Link> },
    { key: '/admin/customers', icon: <TeamOutlined />, label: <Link to="/admin/customers">Khách hàng</Link> },
    { key: '/admin/audit-logs', icon: <HistoryOutlined />, label: <Link to="/admin/audit-logs">Nhật ký hệ thống</Link> },
    { key: '/admin/refund-management', icon: <WalletOutlined />, label: <Link to="/admin/refund-management">Quản lý hoàn tiền</Link> },
    
    ...(isAdmin ? [{ 
        key: '/admin/employees', 
        icon: <TeamOutlined />, 
        label: <Link to="/admin/employees">Quản lý nhân viên</Link> 
    }] : []),
  ];

  return (
    <Sider trigger={null} collapsible collapsed={collapsed}>
     <Link 
        to="/" 
        style={{ 
          display: 'block', 
          height: 32, 
          margin: 16, 
          background: 'rgba(255, 255, 255, 0.2)', 
          color: '#fff', 
          textAlign: 'center', 
          lineHeight: '32px', 
          fontWeight: 'bold',
          textDecoration: 'none', 
          fontSize: collapsed ? '12px' : '16px' 
        }}
      >
        {collapsed ? 'LOTUS' : 'LOTUS HOTEL'}
      </Link>
      <Menu 
        theme="dark" 
        mode="inline" 
        selectedKeys={[currentPath]} 
        items={allMenuItems} 
      />
    </Sider>
  );
};

export default Sidebar;