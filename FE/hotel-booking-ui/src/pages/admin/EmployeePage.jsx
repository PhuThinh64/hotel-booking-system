import React, { useState, useEffect, useCallback } from 'react';
import {
    Table, Button, Modal, Form, Input, message, Popconfirm, 
    Space, Typography, Card, Tag, Row, Col, Statistic, Flex, Segmented
} from 'antd';
import {
    PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, 
    KeyOutlined, TeamOutlined, UndoOutlined, UnorderedListOutlined,
    PhoneOutlined, MailOutlined 
} from '@ant-design/icons';
import * as ApiService from '../../api/ApiService';

const { Title,Text } = Typography;


const getFirstLetterOfName = (fullName) => {
    if (!fullName) return "E";
    const words = fullName.trim().split(/\s+/);
    const lastWord = words[words.length - 1];
    return lastWord ? lastWord.charAt(0).toUpperCase() : "E";
};


const getAvatarColor = (name) => {
    const colors = ['#1677ff', '#722ed1', '#13c2c2', '#52c41a', '#fa8c16', '#eb2f96'];
    if (!name) return colors[0];
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    return colors[Math.abs(hash % colors.length)];
};

const EmployeePage = () => {
    const [form] = Form.useForm();

    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(false);
    const [statusLoadingMap, setStatusLoadingMap] = useState({});

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingEmployee, setEditingEmployee] = useState(null);

    
    const [searchInputValue, setSearchInputValue] = useState('');
    const [keyword, setKeyword] = useState('');
    
    
    const [activeTab, setActiveTab] = useState(null); 
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

    
    const [activeCount, setActiveCount] = useState(0);
    const [deletedCount, setDeletedCount] = useState(0);

    
    useEffect(() => {
        const handler = setTimeout(() => {
            setKeyword(searchInputValue);
            setPagination(prev => ({ ...prev, current: 1 }));
        }, 200);
        return () => clearTimeout(handler);
    }, [searchInputValue]);

    const fetchStatistics = useCallback(async () => {
        try {
            const [activeRes, deletedRes] = await Promise.all([
                ApiService.getAllEmployees({
                    page: 0,
                    size: 1,
                    active: true
                }),
                ApiService.getAllEmployees({
                    page: 0,
                    size: 1,
                    active: false
                })
            ]);

            setActiveCount(activeRes.result?.totalElements || activeRes.totalElements || 0);
            setDeletedCount(deletedRes.result?.totalElements || deletedRes.totalElements || 0);
        } catch (e) {
            console.error("Load statistics failed", e);
        }
    }, []);    

    
    const fetchEmployees = useCallback(async (page = 1, pageSize = 10, searchKey = keyword, isActive = activeTab) => {
        setLoading(true);
        try {
            const res = await ApiService.getAllEmployees({
                page: page - 1,
                size: pageSize,
                keyword: searchKey,
                active: isActive 
            });

            setEmployees(res.result?.content || res.content || []);
            const total = res.result?.totalElements || res.totalElements || 0;
            
            setPagination(prev => ({
                ...prev,
                current: page,
                total: total
            }));
        } catch (e) {
            message.error('Lỗi tải danh sách nhân viên từ hệ thống');
        } finally {
            setLoading(false);
        }
    }, [keyword, activeTab]);

    
    useEffect(() => {
        fetchEmployees(pagination.current, pagination.pageSize, keyword, activeTab);
        fetchStatistics();
    }, [fetchEmployees, fetchStatistics, pagination.current, pagination.pageSize, keyword, activeTab]);

    
    useEffect(() => {
        if (!isModalOpen) return;
        if (editingEmployee) {
            form.setFieldsValue({
                fullName: editingEmployee.fullName,
                phoneNumber: editingEmployee.phoneNumber,
                email: editingEmployee.email,
                username: editingEmployee.username,
            });
        } else {
            form.resetFields();
        }
    }, [isModalOpen, editingEmployee, form]);

    const handleAdd = () => {
        setEditingEmployee(null);
        setIsModalOpen(true);
    };

    const handleEdit = (record) => {
        setEditingEmployee(record);
        setIsModalOpen(true);
    };

    const handleSave = async (values) => {
        setLoading(true);
        try {
            if (editingEmployee) {
                await ApiService.updateEmployee(editingEmployee.id, values);
                message.success('Cập nhật thông tin nhân viên thành công');
            } else {
                await ApiService.createEmployee(values);
                message.success('Thêm mới nhân viên thành công');
            }
            setIsModalOpen(false);
            setEditingEmployee(null);
            fetchEmployees(pagination.current, pagination.pageSize, keyword, activeTab);
        } catch (e) {
            const errorMsg = e.response?.data?.message || "Thao tác thất bại.";
            message.error(errorMsg);
        } finally {
            setLoading(false);
        }
    };

    
    const handleDeleteEmployee = async (id, currentName, isRecordActive) => {
        setStatusLoadingMap(prev => ({ ...prev, [id]: true }));
        try {
            await ApiService.deleteEmployee(id);
            
            if (isRecordActive) {
                message.success(`Đã tạm khóa và chuyển nhân viên [${currentName}] vào Thùng rác`);
                setActiveCount(prev => Math.max(0, prev - 1));
                setDeletedCount(prev => prev + 1);
            } else {
                message.success(`Đã khôi phục trạng thái hoạt động cho nhân viên [${currentName}]`);
                setActiveCount(prev => prev + 1);
                setDeletedCount(prev => Math.max(0, prev - 1));
            }
            fetchEmployees(pagination.current, pagination.pageSize, keyword, activeTab);
        } catch (e) {
            message.error('Cập nhật trạng thái thất bại');
        } finally {
            setStatusLoadingMap(prev => ({ ...prev, [id]: false }));
        }
    };

    const handleResetPassword = async (userId) => {
        try {
            await ApiService.resetEmployeePassword(userId);
            message.success('Mật khẩu nhân viên đã được đặt lại về "123456" mặc định!');
        } catch (e) {
            message.error('Không thể đặt lại mật khẩu');
        }
    };

    const columns = [
        {
            title: 'Nhân viên',
            key: 'employeeInfo',
            width: '25%',
            render: (_, record) => (
                <Flex gap="middle" align="center" style={{ opacity: record.active ? 1 : 0.6 }}>
                    <div style={{
                        width: 40, height: 40, borderRadius: '50%',
                        backgroundColor: getAvatarColor(record.fullName),
                        color: '#fff', fontWeight: 600, fontSize: '15px',
                        display: 'flex', alignItems: 'center', justifyContent: 'center'
                    }}>
                        {getFirstLetterOfName(record.fullName)}
                    </div>
                    <div>
                        <Text strong style={{ fontSize: '14px', display: 'block', textDecoration: record.active ? 'none' : 'line-through' }}>
                            {record.fullName}
                        </Text>
                        <Tag color="purple" style={{ marginTop: 2, borderRadius: 4 }}>
                            {record.roleName || 'Lễ tân'}
                        </Tag>
                    </div>
                </Flex>
            )
        },
        {
            title: 'Tài khoản hệ thống',
            dataIndex: 'username',
            key: 'username',
            width: '18%',
            render: (text, record) => (
                <Text style={{ 
                    fontFamily: 'monospace', fontWeight: 600, 
                    color: record.active ? '#434343' : '#bfbfbf',
                    textDecoration: record.active ? 'none' : 'line-through'
                }}>
                    {text || 'N/A'}
                </Text>
            )
        },
        {
            title: 'Thông tin liên hệ',
            key: 'contact',
            width: '25%',
            render: (_, record) => (
                <Space direction="vertical" size={2} style={{ opacity: record.active ? 1 : 0.6 }}>
                    <Text type="secondary" style={{ fontSize: '13px' }}><PhoneOutlined /> {record.phoneNumber}</Text>
                    <Text type="secondary" style={{ fontSize: '13px' }}><MailOutlined /> {record.email}</Text>
                </Space>
            )
        },
        {
            title: 'Trạng thái',
            dataIndex: 'active',
            key: 'active',
            align: 'center',
            width: '12%',
            render: (active) => (
                <Tag color={active ? 'success' : 'error'} style={{ borderRadius: 4, padding: '2px 8px' }}>
                    {active ? 'Đang làm việc' : 'Đã tạm khóa'}
                </Tag>
            )
        },
        {
            title: 'Thao tác',
            key: 'actions',
            align: 'center',
            width: '22%', 
            render: (_, record) => (
                <Space size="middle">
                    {/* Nút sửa chỉ hiện khi bản ghi này đang hoạt động (active = 1) */}
                    {record.active && (
                        <Button
                            type="text"
                            icon={<EditOutlined style={{ color: '#1677ff' }} />}
                            onClick={() => handleEdit(record)}
                            style={{ background: '#e6f7ff', borderRadius: 6 }}
                        />
                    )}

                    {/* Tiêu đề Popconfirm và kiểu nút Xóa/Khôi phục chạy động theo record.active */}
                    <Popconfirm
                        title={record.active ? "Xác nhận khóa tài khoản nhân viên?" : "Xác nhận khôi phục tài khoản?"}
                        description={record.active ? "Tài khoản nhân viên này sẽ không thể đăng nhập." : "Nhân viên sẽ được mở quyền làm việc trở lại."}
                        onConfirm={() => handleDeleteEmployee(record.id, record.fullName, record.active)}
                        okText="Đồng ý"
                        cancelText="Hủy"
                        okButtonProps={{ danger: record.active }}
                    >
                        <Button
                            type="text"
                            danger={record.active}
                            style={{ 
                                background: record.active ? '#fff1f0' : '#f6ffed', 
                                color: record.active ? '#ff4d4f' : '#52c41a',
                                borderRadius: 6,
                                fontWeight: record.active ? 400 : 500,
                                padding: record.active ? '4px 8px' : '4px 12px' 
                            }}
                            icon={record.active ? <DeleteOutlined /> : <UndoOutlined />}
                            loading={statusLoadingMap[record.id]}
                        >
                            {/* 🌟 ĐÃ THÊM: Hiện chữ "Khôi phục" trực quan bên cạnh icon khi tài khoản bị khóa */}
                            {!record.active && "Khôi phục"}
                        </Button>
                    </Popconfirm>

                    {/* Reset mật khẩu chỉ dùng được với nhân viên đang hoạt động */}
                    {record.active && (
                        <Popconfirm
                            title="Xác nhận reset mật khẩu về '123456'?"
                            onConfirm={() => handleResetPassword(record.userId)}
                            okText="Đồng ý"
                            cancelText="Hủy"
                        >
                            <Button 
                                type="primary"
                                ghost
                                danger 
                                icon={<KeyOutlined />} 
                                size="small"
                                style={{ borderRadius: 6, fontSize: '12px' }}
                            >
                                Reset
                            </Button>
                        </Popconfirm>
                    )}
                </Space>
            ),
        }
    ];

    return (
        <div style={{ padding: '24px', background: '#f5f7fa', minHeight: '100vh' }}>
            
            {/* 🌟 ĐÃ THÊM: Khu vực Tiêu đề trang và Nút thêm mới đẩy lên đầu trang */}
            <Flex justify="space-between" align="center" style={{ marginBottom: '24px' }} wrap="wrap" gap="middle">
                <div>
                    <Title level={3} style={{ margin: 0, fontWeight: 700, color: '#1f1f1f' }}>
                        Quản lý nhân viên
                    </Title>
                    <Text type="secondary" style={{ fontSize: '14px' }}>
                        Hệ thống cấp tài khoản, phân quyền quản trị và điều chỉnh trạng thái làm việc của nhân sự.
                    </Text>
                </div>
                
                {/* Nút Thêm nhân viên đứng độc lập góc trên bên phải (Ẩn khi chọn tab Đã khóa) */}
                {activeTab !== false && (
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={handleAdd}
                        style={{ 
                            background: '#52c41a', 
                            borderColor: '#52c41a', 
                            height: 40, 
                            padding: '0 20px',
                            fontWeight: 600, 
                            borderRadius: 6,
                            boxShadow: '0 2px 4px rgba(82, 196, 26, 0.2)'
                        }}
                    >
                        Thêm nhân viên
                    </Button>
                )}
            </Flex>

            {/* Hàng Thống kê KPI gọn gàng, đồng bộ */}
            <Row gutter={24} style={{ marginBottom: '24px' }}>
                <Col span={12}>
                    <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.02)' }}>
                        <Statistic 
                            title={<Text type="secondary" style={{ fontSize: 14 }}>Nhân sự đang hoạt động</Text>}
                            value={activeCount} 
                            valueStyle={{ color: '#1677ff', fontWeight: 700 }}
                            prefix={<TeamOutlined />}
                            suffix={<span style={{ fontSize: 13, color: '#aaa' }}> thành viên</span>}
                        />
                    </Card>
                </Col>
                <Col span={12}>
                    <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.02)' }}>
                        <Statistic 
                            title={<Text type="secondary" style={{ fontSize: 14 }}>Tài khoản nhân viên bị khóa</Text>}
                            value={deletedCount}
                            valueStyle={{ color: '#ff4d4f', fontWeight: 700 }}
                            prefix={<DeleteOutlined />}
                            suffix={<span style={{ fontSize: 13, color: '#aaa' }}> tài khoản</span>}
                        />
                    </Card>
                </Col>
            </Row>

            {/* Khối quản trị trung tâm chứa Bộ lọc & Bảng dữ liệu */}
            <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                <Flex justify="space-between" align="center" style={{ marginBottom: 20 }} wrap="wrap" gap="middle">
                    
                    {/* Bộ lọc đa trạng thái */}
                    <Segmented
                        size="large"
                        options={[
                            { label: 'Tất cả', value: null, icon: <UnorderedListOutlined /> },
                            { label: 'Đang hoạt động', value: true, icon: <TeamOutlined /> },
                            { label: 'Đã khóa', value: false, icon: <DeleteOutlined /> }
                        ]}
                        value={activeTab}
                        onChange={(value) => {
                            setActiveTab(value);
                            setPagination(prev => ({ ...prev, current: 1 }));
                        }}
                        style={{ background: '#e4e9f0', padding: '3px', borderRadius: '8px' }}
                    />

                    {/* Ô tìm kiếm Real-time (Đã lược bỏ nút Thêm trùng lặp ở đây) */}
                    <Input
                        placeholder="Tìm theo tên, sđt, email..."
                        prefix={<SearchOutlined style={{ color: '#bfbfbf' }} />}
                        value={searchInputValue}
                        onChange={(e) => setSearchInputValue(e.target.value)}
                        style={{ width: 280, height: 38, borderRadius: 6 }}
                        allowClear
                    />
                </Flex>

                <Table
                    rowKey="id"
                    loading={loading}
                    columns={columns}
                    dataSource={employees}
                    pagination={{
                        current: pagination.current,
                        pageSize: pagination.pageSize,
                        total: pagination.total,
                        showTotal: (total) => `Tổng cộng ${total} nhân viên`,
                        onChange: (page, pageSize) => setPagination(prev => ({ ...prev, current: page, pageSize })),
                    }}
                    style={{ background: '#fff' }}
                />
            </Card>

            {/* Modal Form Thêm / Sửa nhân viên giữ nguyên */}
            <Modal
                destroyOnClose={true}
                open={isModalOpen}
                confirmLoading={loading}
                title={
                    <span style={{ fontSize: '16px', fontWeight: 700 }}>
                        {editingEmployee ? 'Cập nhật thông tin nhân viên' : 'Thêm mới nhân viên hệ thống'}
                    </span>
                }
                onCancel={() => {
                    setIsModalOpen(false);
                    setEditingEmployee(null);
                    form.resetFields();
                }}
                onOk={() => form.submit()}
                okText="Xác nhận lưu"
                cancelText="Hủy bỏ"
                width={600}
                modalRender={(modalTxt) => <div style={{ borderRadius: 12, overflow: 'hidden' }}>{modalTxt}</div>}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSave}
                    style={{ marginTop: 16 }}
                >
                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item
                                label="Họ và tên"
                                name="fullName"
                                rules={[{ required: true, message: 'Vui lòng nhập họ và tên' }]}
                            >
                                <Input placeholder="Nguyễn Văn B" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item
                                label="Số điện thoại"
                                name="phoneNumber"
                                rules={[{ required: true, pattern: /^[0-9]{10,11}$/, message: 'Số điện thoại gồm 10-11 chữ số hợp lệ' }]}
                            >
                                <Input placeholder="090xxxxxxxx" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item
                        label="Địa chỉ Email"
                        name="email"
                        rules={[{ required: true, message: 'Vui lòng nhập email' }, { type: 'email', message: 'Email chưa đúng định dạng' }]}
                    >
                        <Input placeholder="nhanvien@hotel.com" />
                    </Form.Item>

                    {!editingEmployee && (
                        <Row gutter={16} style={{ background: '#f9f9f9', padding: '16px 8px 0 8px', borderRadius: 8, marginBottom: 16 }}>
                            <Col span={12}>
                                <Form.Item
                                    label="Tài khoản đăng nhập"
                                    name="username"
                                    rules={[{ required: true, message: 'Nhập tên tài khoản' }]}
                                >
                                    <Input placeholder="username123" />
                                </Form.Item>
                            </Col>

                            <Col span={12}>
                                <Form.Item
                                    label="Mật khẩu khởi tạo"
                                    name="password"
                                    rules={[{ required: true, message: 'Nhập mật khẩu khởi tạo' }]}
                                >
                                    <Input.Password placeholder="••••••••" />
                                </Form.Item>
                            </Col>
                        </Row>
                    )}
                </Form>
            </Modal>

            <style>{`
                .ant-table-thead > tr > th {
                    background: #f8f9fa !important;
                    font-weight: 600 !important;
                }
                .ant-select-selector, .ant-input-affine-wrapper, .ant-btn {
                    border-radius: 6px !important;
                }
            `}</style>
        </div>
    );
};

export default EmployeePage;