import React, { useState, useEffect, useMemo } from 'react';
import { Card, Tabs, Form, Input, Button, message, Typography, Result, Space } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined, KeyOutlined, HomeOutlined } from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import * as ApiService from '../../api/ApiService';

const { Title, Text } = Typography;

const AuthPage = () => {
    const [loading, setLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('1');
    const [isForgotPassword, setIsForgotPassword] = useState(false);
    const [isEmailSent, setIsEmailSent] = useState(false);

    const navigate = useNavigate();
    const location = useLocation();

    
    useEffect(() => {
        if (location.state?.tab) setActiveTab(location.state.tab);
    }, [location.state?.tab]);

    
    
    
    const onFinishLogin = async (values) => {
        setLoading(true);
        try {
            const apiResponse = await ApiService.login(values);
            const userData = apiResponse.result || apiResponse;

            if (userData && userData.token) {
                localStorage.setItem('token', userData.token);
                localStorage.setItem('user', JSON.stringify(userData));
                window.dispatchEvent(new Event('user-updated'));
                message.success('Đăng nhập thành công!');

                if (userData.role === 'ROLE_ADMIN' || userData.role === 'ROLE_RECEPTIONIST' || userData.role === 'ADMIN') {
                    navigate('/admin');
                } else {
                    navigate('/');
                }
            } else {
                message.error('Đăng nhập thất bại, không tìm thấy token!');
            }
        } catch (error) {
            message.error(error.response?.data?.message || 'Sai tài khoản hoặc mật khẩu!');
        } finally {
            setLoading(false);
        }
    };

    
    
    
    const onFinishRegister = async (values) => {
        setLoading(true);
        try {
            
            await ApiService.register(values);
            message.success('Đăng ký tài khoản thành công! Mời bạn đăng nhập.');
            setActiveTab('1'); 
        } catch (error) {
            message.error(error.response?.data?.message || 'Đăng ký thất bại! Tên tài khoản hoặc Email có thể đã tồn tại.');
        } finally {
            setLoading(false);
        }
    };

    
    
    
    const onFinishForgotPassword = async (values) => {
        setLoading(true);
        try {
            
            
            await ApiService.forgotPassword(values);
            setIsEmailSent(true);
            message.success('Yêu cầu khôi phục mật khẩu thành công!');
        } catch (error) {
            
            message.error(error.response?.data?.message || 'Tên đăng nhập không tồn tại!');
        } finally {
            setLoading(false);
        }
    };

    
    const renderLoginContent = () => (
        <Form onFinish={onFinishLogin} layout="vertical" requiredMark={false} style={{ width: '100%' }}>
            <Form.Item name="username" rules={[{ required: true, message: 'Vui lòng nhập tài khoản hoặc email!' }]} style={{ marginBottom: 18 }}>
                <Input 
                    prefix={<UserOutlined style={{ color: '#94a3b8' }} />} 
                    placeholder="Tài khoản hoặc Email" 
                    size="large" 
                    style={{ borderRadius: 10, height: 46, backgroundColor: '#f8fafc', border: '1px solid #e2e8f0' }} 
                />
            </Form.Item>
            <Form.Item name="password" rules={[{ required: true, message: 'Vui lòng cung cấp mật khẩu!' }]} style={{ marginBottom: 14 }}>
                <Input.Password 
                    prefix={<LockOutlined style={{ color: '#94a3b8' }} />} 
                    placeholder="Mật khẩu" 
                    size="large" 
                    style={{ borderRadius: 10, height: 46, backgroundColor: '#f8fafc', border: '1px solid #e2e8f0' }} 
                />
            </Form.Item>
            
            <div style={{ textAlign: 'right', marginBottom: 24 }}>
                <a onClick={() => setIsForgotPassword(true)} style={{ fontSize: 13, color: '#3b82f6', fontWeight: 500 }}>
                    Quên mật khẩu?
                </a>
            </div>

            <Button 
                type="primary" 
                htmlType="submit" 
                size="large" 
                block 
                loading={loading}
                style={{ 
                    borderRadius: 10, 
                    height: 46, 
                    fontWeight: 600, 
                    fontSize: 15,
                    background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)',
                    border: 'none',
                    boxShadow: '0 4px 12px rgba(59, 130, 246, 0.3)'
                }}
            >
                Đăng Nhập
            </Button>
        </Form>
    );

    
    const renderRegisterContent = () => (
        <Form onFinish={onFinishRegister} layout="vertical" requiredMark={false} style={{ width: '100%' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                <Form.Item name="fullName" rules={[{ required: true, message: 'Họ và tên không được để trống!' }]} style={{ margin: 0 }}>
                    <Input prefix={<UserOutlined style={{ color: '#94a3b8' }} />} placeholder="Họ và tên" size="large" style={{ borderRadius: 10, height: 44, backgroundColor: '#f8fafc' }} />
                </Form.Item>
                <Form.Item name="email" rules={[{ required: true, type: 'email', message: 'Vui lòng nhập đúng định dạng Email!' }]} style={{ margin: 0 }}>
                    <Input prefix={<MailOutlined style={{ color: '#94a3b8' }} />} placeholder="Email liên hệ" size="large" style={{ borderRadius: 10, height: 44, backgroundColor: '#f8fafc' }} />
                </Form.Item>
                
                {/* 🔥 ĐÃ SỬA THÀNH phoneNumber ĐỂ KHỚP VỚI REQUESTRSET BACKEND */}
                <Form.Item name="phoneNumber" rules={[{ required: true, message: 'Số điện thoại không được để trống!' }]} style={{ margin: 0 }}>
                    <Input prefix={<PhoneOutlined style={{ color: '#94a3b8' }} />} placeholder="Số điện thoại" size="large" style={{ borderRadius: 10, height: 44, backgroundColor: '#f8fafc' }} />
                </Form.Item>
                
                <Form.Item name="username" rules={[{ required: true, message: 'Tên đăng nhập không được để trống!' }]} style={{ margin: 0 }}>
                    <Input prefix={<UserOutlined style={{ color: '#94a3b8' }} />} placeholder="Tên đăng nhập" size="large" style={{ borderRadius: 10, height: 44, backgroundColor: '#f8fafc' }} />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true, message: 'Vui lòng thiết lập mật khẩu bảo mật!' }]} style={{ margin: 0 }}>
                    <Input.Password prefix={<LockOutlined style={{ color: '#94a3b8' }} />} placeholder="Mật khẩu" size="large" style={{ borderRadius: 10, height: 44, backgroundColor: '#f8fafc' }} />
                </Form.Item>
                
                <Button 
                    type="primary" 
                    htmlType="submit" 
                    size="large" 
                    block 
                    loading={loading}
                    style={{ borderRadius: 10, height: 46, fontWeight: 600, background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)', border: 'none', marginTop: 6 }}
                >
                    Đăng Ký Tài Khoản
                </Button>
            </div>
        </Form>
    );

    
   const renderForgotPasswordContent = () => {
        if (isEmailSent) {
            return (
                <Result
                    status="success"
                    title="Yêu cầu thành công!"
                    subTitle="Hệ thống đã xác thực tài khoản và gửi liên kết đặt lại mật khẩu vào Email liên kết của bạn."
                    extra={[
                        <Button 
                            type="primary" 
                            key="back" 
                            onClick={() => { setIsForgotPassword(false); setIsEmailSent(false); }}
                            style={{ borderRadius: 10, height: 42, background: '#3b82f6' }}
                        >
                            Quay lại đăng nhập
                        </Button>
                    ]}
                />
            );
        }

        return (
            <div style={{ width: '100%' }}>
                <div style={{ marginBottom: 24 }}>
                    <Title level={3} style={{ margin: '0 0 4px 0', fontWeight: 700, color: '#0f172a' }}>
                        Khôi phục mật khẩu
                    </Title>
                    <Text type="secondary" style={{ fontSize: 14 }}>
                        Vui lòng điền Tên đăng nhập của bạn để hệ thống xác minh và tiến hành gửi mã khôi phục.
                    </Text>
                </div>
                <Form onFinish={onFinishForgotPassword} layout="vertical" requiredMark={false}>
                    
                    {/* 🔥 ĐÃ ĐỔI TỪ name="email" THÀNH name="username" */}
                    <Form.Item 
                        name="username" 
                        rules={[{ required: true, message: 'Tên đăng nhập không được để trống!' }]} 
                        style={{ marginBottom: 20 }}
                    >
                        <Input 
                            prefix={<UserOutlined style={{ color: '#94a3b8' }} />} 
                            placeholder="Nhập tên đăng nhập của bạn" 
                            size="large" 
                            style={{ borderRadius: 10, height: 46, backgroundColor: '#f8fafc', border: '1px solid #e2e8f0' }} 
                        />
                    </Form.Item>
                    
                    <Button 
                        type="primary" 
                        htmlType="submit" 
                        size="large" 
                        block 
                        loading={loading}
                        style={{ borderRadius: 10, height: 46, fontWeight: 600, background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)', border: 'none' }}
                    >
                        Xác nhận tài khoản
                    </Button>
                    <div style={{ textAlign: 'center', marginTop: 18 }}>
                        <a onClick={() => setIsForgotPassword(false)} style={{ color: '#64748b', fontWeight: 500, fontSize: 13 }}>
                            Quay lại Đăng nhập
                        </a>
                    </div>
                </Form>
            </div>
        );
    };

    const items = useMemo(() => [
        { key: '1', label: 'Đăng Nhập', children: <div style={{ paddingTop: 16 }}>{renderLoginContent()}</div> },
        { key: '2', label: 'Đăng Ký', children: <div style={{ paddingTop: 16 }}>{renderRegisterContent()}</div> }
    ], [loading]);

    return (
        <div
            style={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'radial-gradient(circle at top, rgba(255,255,255,0.95), rgba(225, 235, 255, 0.95) 45%), linear-gradient(135deg, #0d2f66 0%, #3162c4 100%)',
                padding: '24px'
            }}
        >
            <Card
                bordered={false}
                style={{ width: '100%', maxWidth: 1000, borderRadius: 24, overflow: 'hidden', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)' }}
                bodyStyle={{ padding: 0 }}
            >
                <div style={{ display: 'flex', flexWrap: 'wrap', minHeight: 640 }}>
                    
                    {/* KHỐI TRÁI: BANNER THÔNG TIN ĐỒNG BỘ */}
                    <div
                        style={{
                            flex: '1 1 400px',
                            background: 'linear-gradient(180deg, #0f172a 0%, #1e3a8a 100%)',
                            padding: '48px',
                            display: 'flex',
                            flexDirection: 'column',
                            justifyContent: 'space-between',
                            color: '#ffffff'
                        }}
                    >
                        <div>
                            <div style={{ display: 'inline-flex', alignItems: 'center', gap: 10, marginBottom: 32 }}>
                                <div style={{ width: 40, height: 40, borderRadius: 10, background: 'rgba(255, 255, 255, 0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                    <HomeOutlined style={{ fontSize: 20, color: '#38bdf8' }} />
                                </div>
                                <Text strong style={{ fontSize: 16, color: '#f8fafc', letterSpacing: '0.5px' }}>
                                    Lotus Hotel 
                                </Text>
                            </div>
                            <Title level={2} style={{ margin: 0, color: '#ffffff', fontWeight: 700, lineHeight: 1.25, fontSize: 28 }}>
                                Quản lý đặt phòng tốt hơn, trực quan hơn
                            </Title>
                            <Text style={{ display: 'block', marginTop: 16, color: '#94a3b8', lineHeight: 1.6, fontSize: 14 }}>
                                Truy cập ngay vào dashboard để kiểm tra tình trạng phòng trống và vận hành các dịch vụ lưu trú mượt mà.
                            </Text>
                        </div>

                        <Space direction="vertical" size="large" style={{ width: '100%', marginTop: 32 }}>
                            <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                                <KeyOutlined style={{ color: '#38bdf8', fontSize: 18 }} />
                                <Text style={{ color: '#cbd5e1', fontSize: 13.5 }}>Mã hóa phiên đăng nhập đạt chuẩn an toàn bảo mật.</Text>
                            </div>
                            <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                                <MailOutlined style={{ color: '#38bdf8', fontSize: 18 }} />
                                <Text style={{ color: '#cbd5e1', fontSize: 13.5 }}>Hỗ trợ khôi phục tài khoản tự động qua Email hệ thống.</Text>
                            </div>
                        </Space>
                    </div>

                    {/* KHỐI PHẢI: FORM CHỨA TABS & QUÊN MẬT KHẨU CHẠY ĐỘNG */}
                    <div
                        style={{
                            flex: '1 1 450px',
                            backgroundColor: '#ffffff',
                            padding: '48px 56px',
                            display: 'flex',
                            flexDirection: 'column',
                            justifyContent: 'center'
                        }}
                    >
                        {/* ĐIỀU KIỆN RẼ NHÁNH SỬ DỤNG TRẠNG THÁI KHÔI PHỤC TÀI KHOẢN */}
                        {isForgotPassword ? (
                            renderForgotPasswordContent()
                        ) : (
                            <>
                                <div style={{ marginBottom: 20 }}>
                                    <Title level={3} style={{ margin: '0 0 4px 0', fontWeight: 700, color: '#0f172a' }}>
                                        {activeTab === '1' ? 'Chào mừng bạn!' : 'Tạo tài khoản mới'}
                                    </Title>
                                    <Text type="secondary" style={{ fontSize: 14 }}>
                                        {activeTab === '1' ? 'Vui lòng đăng nhập hệ thống để tiếp tục công việc.' : 'Hãy điền thông tin để đăng ký thành viên khách sạn.'}
                                    </Text>
                                </div>

                                <Tabs
                                    activeKey={activeTab}
                                    onChange={(key) => setActiveTab(key)}
                                    items={items}
                                    type="line"
                                    style={{ width: '100%' }}
                                    tabBarGutter={24}
                                />
                            </>
                        )}
                    </div>

                </div>
            </Card>
        </div>
    );
};

export default AuthPage;