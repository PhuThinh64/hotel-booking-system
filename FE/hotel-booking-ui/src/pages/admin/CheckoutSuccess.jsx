import React, { useEffect, useState } from 'react';
import { Result, Button, Card, Space, Typography, Progress } from 'antd';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { CheckCircleFilled, CloseCircleFilled, InfoCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';

const { Text, Title } = Typography;

const CheckoutSuccess = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const paymentStatus = searchParams.get('status');
  
  
  const [countdown, setCountdown] = useState(3);

  useEffect(() => {
    if (paymentStatus === 'success') {
      
      const channel = new BroadcastChannel('vnpay_payment_channel');
      channel.postMessage({ type: 'PAYMENT_SUCCESS' });
      channel.close();

      
      const interval = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(interval);
            window.close(); 
            return 0;
          }
          return prev - 1;
        });
      }, 1000);

      return () => clearInterval(interval);
    }
  }, [paymentStatus]);

  
  const progressPercent = (countdown / 3) * 100;

  
  
  
  if (paymentStatus === 'success') {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '100vh', 
        background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
        padding: '20px'
      }}>
        <Card 
          style={{ 
            width: 520, 
            borderRadius: 16, 
            boxShadow: '0 10px 30px rgba(0,0,0,0.08)',
            textAlign: 'center',
            overflow: 'hidden'
          }}
          bodyStyle={{ padding: '40px 30px' }}
        >
          <Result
            icon={<CheckCircleFilled style={{ color: '#52c41a', fontSize: 72 }} />}
            title={<Title level={3} style={{ color: '#1a1a1a', marginTop: 0 }}>Thanh Toán Thành Công!</Title>}
            subTitle={
              <Space direction="vertical" size={2} style={{ width: '100%' }}>
                <Text type="secondary" style={{ fontSize: 14 }}>
                  Hệ thống quản lý khách sạn đã ghi nhận dòng tiền thành công qua cổng thanh toán liên kết <Text strong style={{ color: '#005baa' }}>VNPAY</Text>.
                </Text>
              </Space>
            }
          />

          {/* Hộp thông tin chi tiết biên nhận giả lập */}
          <div style={{ 
            background: '#f6ffed', 
            border: '1px solid #b7eb8f', 
            borderRadius: 8, 
            padding: '16px',
            marginBottom: 24,
            textAlign: 'left'
          }}>
            <Space direction="vertical" size={6} style={{ width: '100%' }}>
              <div style={{ display: 'flex', justifyContent: 'between', width: '100%' }}>
                <Text type="secondary"><InfoCircleOutlined /> Trạng thái giao dịch:</Text>
                <Text strong style={{ color: '#52c41a', marginLeft: 'auto' }}>Hoàn tất xử lý</Text>
              </div>
              <div style={{ display: 'flex', justifyContent: 'between', width: '100%' }}>
                <Text type="secondary"><ClockCircleOutlined /> Phương thức xử lý:</Text>
                <Text strong style={{ marginLeft: 'auto' }}>Cổng điện tử VNPay QR</Text>
              </div>
            </Space>
          </div>

          {/* Vòng đếm ngược tự động đóng tab */}
          <Space direction="vertical" size={12} style={{ marginBottom: 12 }}>
            <Progress 
              type="circle" 
              percent={progressPercent} 
              format={() => `${countdown}s`} 
              size={50} 
              strokeColor="#52c41a"
            />
            <Text type="secondary" style={{ fontSize: 13, italic: true }}>
              Cửa sổ này sẽ tự động đóng sau khi đồng bộ dữ liệu...
            </Text>
          </Space>

          <div style={{ marginTop: 24 }}>
            <Button type="primary" size="large" block onClick={() => window.close()} style={{ borderRadius: 8, height: 45, fontWeight: 600 }}>
              Đóng cửa sổ ngay lập tức
            </Button>
          </div>
        </Card>
      </div>
    );
  }

  
  
  
  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      minHeight: '100vh', 
      background: 'linear-gradient(135deg, #f5f7fa 0%, #fcd9d9 100%)',
      padding: '20px'
    }}>
      <Card 
        style={{ 
          width: 520, 
          borderRadius: 16, 
          boxShadow: '0 10px 30px rgba(0,0,0,0.08)',
          textAlign: 'center'
        }}
        bodyStyle={{ padding: '40px 30px' }}
      >
        <Result
          icon={<CloseCircleFilled style={{ color: '#ff4d4f', fontSize: 72 }} />}
          title={<Title level={3} style={{ color: '#1a1a1a', marginTop: 0 }}>Thanh Toán Thất Bại</Title>}
          subTitle={
            <Text type="secondary" style={{ fontSize: 14 }}>
              Giao dịch đã bị huỷ bỏ bởi người dùng hoặc tài khoản thử nghiệm của bạn không đủ số dư để thực hiện quyết toán.
            </Text>
          }
        />

        <div style={{ 
          background: '#fff2f0', 
          border: '1px solid #ffccc7', 
          borderRadius: 8, 
          padding: '14px',
          marginBottom: 30,
          textAlign: 'center',
          color: '#ff4d4f'
        }}>
          Vui lòng quay lại màn hình tổng để kiểm tra hoặc chọn phương thức thu tiền mặt.
        </div>

        <Space direction="vertical" size={12} style={{ width: '100%' }}>
          <Button type="primary" danger size="large" block onClick={() => window.close()} style={{ borderRadius: 8, height: 45, fontWeight: 600 }}>
            Đóng cửa sổ hiện tại
          </Button>
          <Button type="text" block onClick={() => navigate('/admin/bookings')} style={{ color: '#666' }}>
            Quay lại quản lý đặt phòng
          </Button>
        </Space>
      </Card>
    </div>
  );
};

export default CheckoutSuccess;