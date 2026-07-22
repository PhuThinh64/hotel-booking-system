import React, { useState, useEffect } from 'react';
import { Layout, message } from 'antd';
import { useNavigate, Outlet, useLocation } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import AdminHeader from '../components/AdminHeader'; 

const { Content } = Layout;

const AdminLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  
  
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || 'null'));

  useEffect(() => {
    const token = localStorage.getItem('token') || localStorage.getItem('accessToken');
    if (!token) {
        message.error("Vui lòng đăng nhập!");
        navigate('/auth');
    }
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    message.success('Đăng xuất thành công!');
    navigate('/auth');
  };

  return (
    <Layout style={{ minHeight: '100vh', width: '100%' }}>
      <Sidebar collapsed={collapsed} currentPath={location.pathname} />
      
      <Layout>
        <AdminHeader 
            collapsed={collapsed} 
            onToggle={() => setCollapsed(!collapsed)} 
            user={user} 
            onLogout={handleLogout} 
        />

        <Content style={{ padding: '24px', background: '#f5f5f5' }}>
          <Outlet /> 
        </Content>
      </Layout>
    </Layout>
  );
};

export default AdminLayout;