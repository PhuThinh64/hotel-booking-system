import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Button, message } from 'antd';
import * as ApiService from '../../api/ApiService';

const AdminProfile = () => {
    const [loading, setLoading] = useState(false);
    const [form] = Form.useForm();
    
    
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    
    
    useEffect(() => {
        console.log("User object từ localStorage:", user); 
        if (user) {
            form.setFieldsValue({
                username: user.username,
                fullName: user.fullName,
                email: user.email,
                phoneNumber: user.phoneNumber, 
            });
        }
    }, [form, user]);

    const onFinish = async (values) => {
        setLoading(true);
        try {
            const updatedUser = await ApiService.updateProfile(values);
            localStorage.setItem('user', JSON.stringify(updatedUser));
            window.dispatchEvent(new Event('user-updated'));
            message.success("Cập nhật thông tin thành công!");
        } catch (error) {
            message.error(error.response?.data?.message || "Có lỗi xảy ra");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card title="Thông tin cá nhân" style={{ maxWidth: 600, margin: '0 auto' }}>
            <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
            >
                <Form.Item name="username" label="Tên đăng nhập (Username)">
                    <Input disabled />
                </Form.Item>
                
                <Form.Item name="fullName" label="Họ và tên" rules={[{ required: true }]}>
                    <Input />
                </Form.Item>
                
                <Form.Item name="phoneNumber" label="Số điện thoại" rules={[{ pattern: /^[0-9]{10,11}$/, message: "Số điện thoại không hợp lệ!" }]}>
                    <Input placeholder="Nhập số điện thoại" />
                </Form.Item>

                <Form.Item name="email" label="Email" rules={[{ type: 'email' }]}>
                    <Input />
                </Form.Item>

               

                <Form.Item>
                    <Button type="primary" htmlType="submit" loading={loading}>
                        Lưu thay đổi
                    </Button>
                </Form.Item>
            </Form>
        </Card>
    );
};

export default AdminProfile;