import React, { useEffect, useState } from 'react';
import { Card, Form, Input, Button, Typography, Row, Col, Space, message, Spin, Flex, Radio, DatePicker } from 'antd';
import { UserOutlined, PhoneOutlined, MailOutlined, HomeOutlined, SaveOutlined, ArrowLeftOutlined, IdcardOutlined, GlobalOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import dayjs from 'dayjs';

const { Title, Text } = Typography;

const UserProfile = () => {
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        fetchUserProfile();
    }, []);

    const fetchUserProfile = async () => {
        const token = localStorage.getItem("token");
        if (!token) {
            message.error("Vui lòng đăng nhập trước!");
            navigate("/login");
            return;
        }

        setLoading(true);
        try {
            const res = await axios.get("http://localhost:8080/api/v1/users/profile", {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            if (res.data.result) {
                const data = res.data.result;
                
                
                if (data.birthday) {
                    data.birthday = dayjs(data.birthday);
                }
                
                form.setFieldsValue(data);
            }
        } catch (err) {
            console.error(err);
            message.error("Không thể tải thông tin cá nhân");
        } finally {
            setLoading(false);
        }
    };

    const onFinish = async (values) => {
        const token = localStorage.getItem("token");
        setSubmitting(true);
        
        
        
        const submitData = {
            ...values,
            birthday: values.birthday ? values.birthday.format('YYYY-MM-DD') : null
        };

        try {
            await axios.put("http://localhost:8080/api/v1/users/profile/customer", submitData, {
                headers: { Authorization: `Bearer ${token}` }
            });
            message.success("Cập nhật thông tin cá nhân thành công!");
            
            const storedUser = JSON.parse(localStorage.getItem('user'));
            
            const newUser = { ...storedUser, fullName: values.fullName };
            
            localStorage.setItem('user', JSON.stringify(newUser));
            
            window.dispatchEvent(new Event('user-updated'));
            fetchUserProfile(); 
        } catch (err) {
            console.error(err);
            const errorMsg = err.response?.data?.message || "Có lỗi xảy ra khi cập nhật";
            message.error(errorMsg);
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <Flex justify="center" align="center" style={{ height: '80vh' }}>
                <Spin size="large" tip="Đang tải dữ liệu hồ sơ..." />
            </Flex>
        );
    }

    return (
        <div style={{ maxWidth: '900px', margin: '40px auto', padding: '0 20px' }}>
            {/* NÚT BACK CÁ ĐIỆU */}
            <div style={{
                display: 'flex', alignItems: 'center', marginBottom: 25, padding: '12px 16px',
                background: 'linear-gradient(135deg, #f0f5ff 0%, #ffffff 100%)',
                borderRadius: 14, border: '1px solid #e6f4ff'
            }}>
                <div onClick={() => navigate('/')} style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
                    <ArrowLeftOutlined style={{ color: '#1677ff' }} />
                    <Text style={{ color: '#1677ff', fontWeight: 600 }}>Quay lại</Text>
                </div>
            </div>

            {/* MAIN CARD */}
            <Card
                style={{ borderRadius: 20, boxShadow: '0 8px 24px rgba(0,0,0,0.05)', overflow: 'hidden' }}
                styles={{ body: { padding: 0 } }}
            >
                <div style={{
                    background: 'linear-gradient(135deg, #1677ff 0%, #69b1ff 100%)',
                    padding: '30px 40px', color: '#fff'
                }}>
                    <Title level={3} style={{ color: '#fff', margin: 0 }}>Hồ sơ cá nhân</Title>
                    <Text style={{ color: 'rgba(255,255,255,0.85)' }}>Cập nhật thông tin chính xác để thuận tiện cho việc nhận phòng</Text>
                </div>

                <Form form={form} layout="vertical" onFinish={onFinish} style={{ padding: '40px' }}>
                    <Row gutter={[24, 16]}>
                        
                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Tên đăng nhập</Text>} name="username">
                                <Input prefix={<UserOutlined />} disabled style={{ borderRadius: 10, height: 40, backgroundColor: '#f5f5f5' }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Họ và tên</Text>} name="fullName" rules={[{ required: true, message: 'Vui lòng nhập họ và tên!' }]}>
                                <Input prefix={<UserOutlined />} placeholder="Nhập họ tên" style={{ borderRadius: 10, height: 40 }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Số CMND / CCCD</Text>} name="identityCard" rules={[{ required: true, message: 'Vui lòng nhập CMND/CCCD!' }]}>
                                <Input prefix={<IdcardOutlined />} placeholder="Nhập số CCCD" style={{ borderRadius: 10, height: 40 }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Số điện thoại</Text>} name="phone" rules={[
                                { required: true, message: 'Vui lòng nhập số điện thoại!' },
                                { pattern: /^[0-9]{10,11}$/, message: 'SĐT không hợp lệ!' }
                            ]}>
                                <Input prefix={<PhoneOutlined />} placeholder="Nhập số điện thoại" style={{ borderRadius: 10, height: 40 }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Email</Text>} name="email" rules={[{ type: 'email', message: 'Email không hợp lệ!' }]}>
                                <Input prefix={<MailOutlined />} placeholder="Nhập địa chỉ Email" style={{ borderRadius: 10, height: 40 }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Quốc tịch</Text>} name="nationality">
                                <Input prefix={<GlobalOutlined />} placeholder="Ví dụ: Việt Nam" style={{ borderRadius: 10, height: 40 }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Ngày sinh</Text>} name="birthday">
                                <DatePicker format="DD/MM/YYYY" style={{ borderRadius: 10, height: 40, width: '100%' }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} md={12}>
                            <Form.Item label={<Text strong>Giới tính</Text>} name="gender">
                                <Radio.Group style={{ height: 40, display: 'flex', alignItems: 'center' }}>
                                    <Radio value="MALE">Nam</Radio>
                                    <Radio value="FEMALE">Nữ</Radio>
                                    <Radio value="OTHER">Khác</Radio>
                                </Radio.Group>
                            </Form.Item>
                        </Col>

                        <Col xs={24}>
                            <Form.Item label={<Text strong>Địa chỉ thường trú</Text>} name="address">
                                <Input.TextArea rows={2} placeholder="Nhập địa chỉ chi tiết" style={{ borderRadius: 10 }} />
                            </Form.Item>
                        </Col>

                        <Col xs={24} style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 10 }}>
                            <Space>
                                <Button type="default" onClick={() => navigate('/')} style={{ borderRadius: 10, height: 42, paddingInline: 24 }}>Hủy bỏ</Button>
                                <Button type="primary" htmlType="submit" loading={submitting} icon={<SaveOutlined />} style={{ borderRadius: 10, height: 42, paddingInline: 24 }}>Lưu thay đổi</Button>
                            </Space>
                        </Col>
                    </Row>
                </Form>
            </Card>
        </div>
    );
};

export default UserProfile;