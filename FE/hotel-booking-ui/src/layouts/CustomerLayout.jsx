import React from 'react';
import { Outlet } from 'react-router-dom';
import AppHeader from '../components/AppHeader';
import AppFooter from '../components/AppFooter'; 
import ScrollToTop from '../components/ScrollToTop';
import { Layout } from 'antd';

const { Content } = Layout;

const CustomerLayout = () => {
  return (
    <Layout style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Thanh điều hướng */}
      <ScrollToTop />
      <AppHeader />
      
      {/* Nội dung trang thay đổi theo Router */}
      <Content style={{ padding: '20px 50px', backgroundColor: '#f5f5f5', flex: 1 }}>
        <Outlet /> 
      </Content>

      {/* Footer thông tin khách sạn */}
      <AppFooter />
    </Layout>
  );
};

export default CustomerLayout;