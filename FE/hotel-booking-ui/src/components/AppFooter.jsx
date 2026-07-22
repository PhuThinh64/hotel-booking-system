import React from 'react';
import { Layout, Row, Col, Divider } from 'antd';
import { Link } from 'react-router-dom'; 
import { 
  EnvironmentOutlined, 
  PhoneOutlined, 
  MailOutlined, 
  ClockCircleOutlined, 
  FileTextOutlined 
} from '@ant-design/icons'; 

const { Footer } = Layout;

const AppFooter = () => {
  return (
    <Footer style={{ 
      backgroundColor: '#030d1a', 
      color: 'rgba(255, 255, 255, 0.7)', 
      padding: '50px 50px 25px 50px',
      fontSize: '14px'
    }}>
      <style>{`
        .footer-link {
          color: rgba(255, 255, 255, 0.65) !important;
          transition: all 0.3s ease;
          display: inline-flex;
          align-items: center;
          gap: 8px;
        }
        .footer-link:hover {
          color: #1890ff !important; 
          transform: translateX(4px); 
        }
      `}</style>

      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <Row gutter={[40, 32]}>
          
          {/* Cột 1: Giới thiệu */}
          <Col xs={24} sm={12} md={8}>
            <h3 style={{ color: '#fff', marginBottom: '20px', fontSize: '20px', fontWeight: '700', letterSpacing: '0.5px' }}>
              LOTUS HOTEL
            </h3>
            <p style={{ lineHeight: '1.7', color: 'rgba(255, 255, 255, 0.55)' }}>
              Trải nghiệm dịch vụ nghỉ dưỡng đẳng cấp quốc tế với không gian sang trọng, 
              tiện nghi bậc nhất và đội ngũ nhân viên chuyên nghiệp phục vụ 24/7.
            </p>
          </Col>

          {/* Cột 2: Thông tin liên hệ */}
          <Col xs={24} sm={12} md={8}>
            <h3 style={{ color: '#fff', marginBottom: '20px', fontSize: '16px', fontWeight: '600' }}>
              THÔNG TIN LIÊN HỆ
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <p style={{ margin: 0 }}><EnvironmentOutlined style={{ color: '#1890ff' }} /> <b>Địa chỉ:</b> 123 Đường Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh</p>
              <p style={{ margin: 0 }}><PhoneOutlined style={{ color: '#1890ff' }} /> <b>Hotline:</b> +84 123 456 789</p>
              <p style={{ margin: 0 }}><MailOutlined style={{ color: '#1890ff' }} /> <b>Email:</b> info@lotushotel.com</p>
              <p style={{ margin: 0 }}><ClockCircleOutlined style={{ color: '#1890ff' }} /> <b>Giờ mở cửa:</b> Hoạt động 24/7</p>
            </div>
          </Col>

          {/* Cột 3: Chính sách & Liên kết */}
          <Col xs={24} sm={12} md={8}>
            <h3 style={{ color: '#fff', marginBottom: '20px', fontSize: '16px', fontWeight: '600' }}>
              CHÍNH SÁCH & QUY ĐỊNH
            </h3>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
              <li>
                {/* 🔥 ĐÃ FIX: Thêm onClick để tự động cuộn lên đầu trang lập tức khi bấm */}
                <Link to="/booking-policy" className="footer-link">
                    <FileTextOutlined /> Chính sách đặt phòng và hoàn tiền
                </Link>
              </li>   
            </ul>
          </Col>

        </Row>

        <Divider style={{ borderColor: 'rgba(255, 255, 255, 0.08)', margin: '40px 0 20px 0' }} />
        
        {/* Bản quyền */}
        <div style={{ textAlign: 'center', color: 'rgba(255, 255, 255, 0.4)', fontSize: '13px' }}>
          © {new Date().getFullYear()} Lotus Hotel. Tất cả các quyền được bảo lưu.
        </div>
      </div>
    </Footer>
  );
};

export default AppFooter;