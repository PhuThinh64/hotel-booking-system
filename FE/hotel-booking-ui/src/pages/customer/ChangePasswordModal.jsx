import React, { useState } from 'react';
import { Modal, Form, Input, Button, message, Typography } from 'antd';
import { LockOutlined, KeyOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { changePassword } from '../../api/ApiService'; 

const { Title, Text } = Typography;

const ChangePasswordModal = ({ visible, onClose }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);

    const onFinish = async (values) => {
        setLoading(true);
        try {
            await changePassword(values);
            message.success("Đổi mật khẩu thành công!");
            form.resetFields();
            onClose();
        } catch (error) {
            message.error(error.response?.data?.message || "Có lỗi xảy ra");
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        form.resetFields();
        onClose();
    };

    return (
        <Modal 
            open={visible} 
            onCancel={handleCancel} 
            footer={null} 
            centered
            width={450}
            bodyStyle={{ padding: '36px 32px' }}
            maskStyle={{ backgroundColor: 'rgba(15, 23, 42, 0.45)', backdropFilter: 'blur(5px)' }} 
            style={{ borderRadius: '20px', overflow: 'hidden' }}
        >
            {/* AREA HEADER: Icon chìa khóa nổi bật & Tiêu đề thiết kế riêng */}
            <div style={{ textAlign: 'center', marginBottom: 28 }}>
                <div style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    width: '56px',
                    height: '56px',
                    borderRadius: '16px',
                    background: 'linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%)',
                    color: '#1890ff',
                    fontSize: '24px',
                    marginBottom: '16px',
                    boxShadow: '0 8px 20px rgba(24, 144, 255, 0.15)'
                }}>
                    <KeyOutlined />
                </div>
                <Title level={3} style={{ margin: '0 0 6px 0', fontWeight: 700, color: '#0f172a', fontSize: '22px' }}>
                    Đổi Mật Khẩu
                </Title>
                <Text type="secondary" style={{ fontSize: '13.5px', color: '#64748b' }}>
                    Vui lòng thiết lập mật khẩu mạnh để bảo vệ tài khoản của bạn.
                </Text>
            </div>

            {/* FORM INPUTS */}
            <Form form={form} layout="vertical" onFinish={onFinish} requiredMark={false}>
                
                {/* 1. Mật khẩu cũ */}
                <Form.Item 
                    name="oldPassword" 
                    label={<span style={{ fontWeight: 600, color: '#334155', fontSize: '13px' }}>Mật khẩu hiện tại</span>} 
                    rules={[{ required: true, message: 'Nhập mật khẩu cũ!' }]}
                    style={{ marginBottom: 18 }}
                >
                    <Input.Password 
                        prefix={<LockOutlined style={{ color: '#94a3b8', marginRight: 4 }} />}
                        placeholder="Nhập mật khẩu cũ của bạn"
                        size="large"
                        style={{
                            borderRadius: '10px',
                            height: '46px',
                            backgroundColor: '#f8fafc',
                            border: '1px solid #e2e8f0',
                        }}
                    />
                </Form.Item>

                {/* 2. Mật khẩu mới */}
                <Form.Item 
                    name="newPassword" 
                    label={<span style={{ fontWeight: 600, color: '#334155', fontSize: '13px' }}>Mật khẩu mới</span>} 
                    rules={[{ required: true, message: 'Nhập mật khẩu mới!' }]}
                    style={{ marginBottom: 18 }}
                >
                    <Input.Password 
                        prefix={<KeyOutlined style={{ color: '#94a3b8', marginRight: 4 }} />}
                        placeholder="Thiết lập mật khẩu mới"
                        size="large"
                        style={{
                            borderRadius: '10px',
                            height: '46px',
                            backgroundColor: '#f8fafc',
                            border: '1px solid #e2e8f0',
                        }}
                    />
                </Form.Item>

                {/* 3. Xác nhận mật khẩu */}
                <Form.Item 
                    name="confirmPassword" 
                    label={<span style={{ fontWeight: 600, color: '#334155', fontSize: '13px' }}>Xác nhận mật khẩu mới</span>} 
                    dependencies={['newPassword']}
                    style={{ marginBottom: 30 }}
                    rules={[
                        { required: true, message: 'Nhập lại mật khẩu mới!' },
                        ({ getFieldValue }) => ({
                            validator(_, value) {
                                if (!value || getFieldValue('newPassword') === value) return Promise.resolve();
                                return Promise.reject(new Error('Mật khẩu không khớp!'));
                            },
                        }),
                    ]}
                >
                    <Input.Password 
                        prefix={<SafetyCertificateOutlined style={{ color: '#94a3b8', marginRight: 4 }} />}
                        placeholder="Nhập lại mật khẩu mới"
                        size="large"
                        style={{
                            borderRadius: '10px',
                            height: '46px',
                            backgroundColor: '#f8fafc',
                            border: '1px solid #e2e8f0',
                        }}
                    />
                </Form.Item>

                {/* KHỐI NÚT HÀNH ĐỘNG CUSTOM */}
                <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                    <Button 
                        size="large" 
                        onClick={handleCancel} 
                        style={{ 
                            flex: 1, 
                            borderRadius: '10px', 
                            height: '46px', 
                            fontWeight: 500, 
                            color: '#64748b',
                            borderColor: '#cbd5e1'
                        }}
                    >
                        Hủy bỏ
                    </Button>
                    <Button 
                        type="primary" 
                        htmlType="submit" 
                        size="large" 
                        loading={loading} 
                        style={{ 
                            flex: 1, 
                            borderRadius: '10px', 
                            height: '46px', 
                            fontWeight: 600, 
                            background: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)', 
                            border: 'none',
                            boxShadow: '0 4px 14px rgba(59, 130, 246, 0.35)'
                        }}
                    >
                        Cập nhật
                    </Button>
                </div>

            </Form>
        </Modal>
    );
};

export default ChangePasswordModal;