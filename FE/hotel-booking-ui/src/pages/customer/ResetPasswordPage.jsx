import React, { useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Form, Input, Button, message, Card, Typography, Space } from 'antd';
import { LockOutlined, CheckCircleOutlined } from '@ant-design/icons';
import * as ApiService from '../../api/ApiService';

const { Title, Text } = Typography;

const ResetPasswordPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const onFinish = async (values) => {
        setLoading(true);
        try {
            await ApiService.resetPassword({
                token,
                newPassword: values.newPassword,
            });
            message.success('Đổi mật khẩu thành công!');
            navigate('/auth');
        } catch (error) {
            console.error('Reset password error:', error);
            message.error('Token không hợp lệ hoặc đã hết hạn!');
        } finally {
            setLoading(false);
        }
    };

    if (!token) {
        return (
            <div
                style={{
                    minHeight: '100vh',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    background: 'radial-gradient(circle at top, rgba(255,255,255,0.95), rgba(225,235,255,0.95) 45%), linear-gradient(135deg, #0d2f66 0%, #3162c4 100%)',
                    padding: 24,
                }}
            >
                <Card
                    bordered={false}
                    style={{
                        width: '100%',
                        maxWidth: 520,
                        borderRadius: 24,
                        textAlign: 'center',
                        boxShadow: '0 24px 60px rgba(15, 23, 42, 0.18)',
                    }}
                >
                    <CheckCircleOutlined style={{ fontSize: 48, color: '#faad14', marginBottom: 16 }} />
                    <Title level={3}>Liên kết không hợp lệ</Title>
                    <Text type="secondary">Mã xác thực không tồn tại hoặc đã hết hạn. Vui lòng thử lại bằng đường dẫn mới.</Text>
                    <Button type="primary" style={{ marginTop: 24 }} onClick={() => navigate('/auth')}>
                        Quay lại đăng nhập
                    </Button>
                </Card>
            </div>
        );
    }

    return (
        <div
            style={{
                minHeight: '100vh',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                background: 'radial-gradient(circle at top, rgba(255,255,255,0.95), rgba(225,235,255,0.95) 45%), linear-gradient(135deg, #0d2f66 0%, #3162c4 100%)',
                padding: 24,
            }}
        >
            <Card
                bordered={false}
                style={{
                    width: '100%',
                    maxWidth: 560,
                    borderRadius: 28,
                    overflow: 'hidden',
                    boxShadow: '0 28px 80px rgba(15, 23, 42, 0.18)',
                }}
            >
                <div style={{ display: 'flex', flexDirection: 'column', gap: 32, padding: '40px 32px' }}>
                    <div>
                        <Text style={{ color: '#3a6fd2', fontWeight: 700, letterSpacing: 1.1 }}>LOTUS HOTEL</Text>
                        <Title level={2} style={{ margin: '16px 0 0', color: '#102345' }}>
                            Thiết lập mật khẩu mới
                        </Title>
                        <Text type="secondary">
                            Nhập mật khẩu mới để tiếp tục truy cập tài khoản của bạn. Mật khẩu phải có ít nhất 6 ký tự.
                        </Text>
                    </div>

                    <Card
                        bordered={false}
                        style={{
                            borderRadius: 22,
                            background: 'rgba(15, 35, 79, 0.92)',
                            color: '#ffffff',
                        }}
                    >
                        <Form layout="vertical" onFinish={onFinish}>
                            <Form.Item
                                name="newPassword"
                                label={<Text strong style={{ color: '#ffffff' }}>Mật khẩu mới</Text>}
                                rules={[
                                    { required: true, message: 'Vui lòng nhập mật khẩu mới!' },
                                    { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự.' },
                                ]}
                            >
                                <Input.Password
                                    prefix={<LockOutlined style={{ color: 'rgba(255,255,255,0.65)' }} />}
                                    placeholder="Nhập mật khẩu mới"
                                    size="large"
                                />
                            </Form.Item>
                            <Form.Item
                                name="confirmPassword"
                                label={<Text strong style={{ color: '#ffffff' }}>Xác nhận mật khẩu</Text>}
                                dependencies={[ 'newPassword' ]}
                                rules={[
                                    { required: true, message: 'Vui lòng xác nhận mật khẩu!' },
                                    ({ getFieldValue }) => ({
                                        validator(_, value) {
                                            if (!value || getFieldValue('newPassword') === value) {
                                                return Promise.resolve();
                                            }
                                            return Promise.reject(new Error('Mật khẩu xác nhận không khớp!'));
                                        },
                                    }),
                                ]}
                            >
                                <Input.Password
                                    prefix={<LockOutlined style={{ color: 'rgba(255,255,255,0.65)' }} />}
                                    placeholder="Xác nhận mật khẩu"
                                    size="large"
                                />
                            </Form.Item>
                            <Form.Item>
                                <Button type="primary" htmlType="submit" size="large" block>
                                    Xác nhận
                                </Button>
                            </Form.Item>
                        </Form>
                    </Card>

                    <Space direction="vertical" size="small" style={{ width: '100%', textAlign: 'center' }}>
                        <Text type="secondary">Hoặc</Text>
                        <Button type="link" style={{ padding: 0 }} onClick={() => navigate('/auth')}>
                            Quay lại trang đăng nhập
                        </Button>
                    </Space>
                </div>
            </Card>
        </div>
    );
};

export default ResetPasswordPage;
