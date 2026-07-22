import React, { useEffect, useState, useCallback } from 'react';
import { 
    Table, Button, Space, Input, Tag, Card, Modal, Form, 
    Select, message, Row, Col, Statistic, Popconfirm, DatePicker, Avatar, Typography, Flex, Segmented, Skeleton
} from 'antd';
import { 
    PlusOutlined, UserOutlined, PhoneOutlined, IdcardOutlined, 
    SearchOutlined, MailOutlined, EnvironmentOutlined, GlobalOutlined,
    EditOutlined, DeleteOutlined, TeamOutlined, UserAddOutlined, UndoOutlined,
    UnorderedListOutlined
} from '@ant-design/icons';
import * as ApiService from '../../api/ApiService';
import dayjs from 'dayjs';

const { Text, Title } = Typography;
const { Option } = Select;


const getFirstLetterOfName = (fullName) => {
    if (!fullName) return "C";
    const words = fullName.trim().split(/\s+/);
    const lastWord = words[words.length - 1];
    return lastWord ? lastWord.charAt(0).toUpperCase() : "C";
};


const getAvatarColor = (name) => {
    const colors = ['#1677ff', '#52c41a', '#fadb14', '#13c2c2', '#722ed1', '#eb2f96', '#fa8c16'];
    if (!name) return colors[0];
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash % colors.length);
    return colors[index];
};

const CustomerPage = () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const isAdmin = user.role === 'ROLE_ADMIN';

    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [statsLoading, setStatsLoading] = useState(false);
    
    const [statusLoadingMap, setStatusLoadingMap] = useState({}); 
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [form] = Form.useForm();
    
    const [keyword, setKeyword] = useState('');
    const [activeTab, setActiveTab] = useState('ACTIVE'); 
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

    
    const [newCustomersCount, setNewCustomersCount] = useState(0);
    const [activeCount, setActiveCount] = useState(0);
    const [deletedCount, setDeletedCount] = useState(0);

    
    const fetchCustomers = useCallback(async (page = 0, size = 10, searchKey = keyword, tab = activeTab) => {
        setLoading(true);
        try {
            
            const targetActiveStatus = tab === 'ALL' ? null : tab === 'ACTIVE';
            const data = await ApiService.getCustomers(page, size, searchKey, targetActiveStatus);
            
            if (data && data.content) {
                setCustomers(data.content);
                setPagination(prev => ({
                    ...prev,
                    total: data.totalElements,
                    current: data.number + 1
                }));

                
                if (tab === 'ACTIVE') {
                    setActiveCount(data.totalElements || 0);
                } else if (tab === 'DELETED') {
                    setDeletedCount(data.totalElements || 0);
                }
            }
        } catch (error) {
            console.error("Lỗi lấy danh sách khách hàng:", error);
        } finally {
            setLoading(false);
        }
    }, [keyword, activeTab]);

    
    const fetchStats = useCallback(async () => {
        setStatsLoading(true);
        try {
            const startOfMonth = dayjs().startOf('month').toISOString();
            const endOfMonth = dayjs().endOf('month').toISOString();
            
            
            const [countNew, resActive, resDeleted] = await Promise.all([
                ApiService.getNewCustomersCount(startOfMonth, endOfMonth),
                ApiService.getCustomers(0, 1, "", true),
                ApiService.getCustomers(0, 1, "", false)
            ]);

            setNewCustomersCount(countNew || 0);
            setActiveCount(resActive?.totalElements || 0);
            setDeletedCount(resDeleted?.totalElements || 0);
        } catch (error) {
            console.error("Lỗi thống kê:", error);
        } finally {
            setStatsLoading(false);
        }
    }, []);

    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            fetchCustomers(0, pagination.pageSize, keyword, activeTab);
        }, 200);
        return () => clearTimeout(delayDebounceFn);
    }, [keyword, activeTab, pagination.pageSize]);

    
    useEffect(() => {
        fetchStats();
    }, [fetchStats]);

    const handleSearchChange = (e) => {
        const value = e.target.value;
        setKeyword(value);
        setPagination(prev => ({ ...prev, current: 1 }));
        fetchCustomers(0, pagination.pageSize, value, activeTab);
    };

    const handleDeleteCustomer = async (id, currentName) => {
        setStatusLoadingMap(prev => ({ ...prev, [id]: true }));
        try {
            
            const targetCust = customers.find(c => c.id === id);
            const isCurrentlyActive = targetCust ? targetCust.active : true;

            await ApiService.deleteCustomer(id);

            if (isCurrentlyActive) {
                message.success(`Đã khóa khách hàng [${currentName}]!`);
            } else {
                message.success(`Đã khôi phục khách hàng [${currentName}] thành công!`);
            }

            fetchCustomers(pagination.current - 1, pagination.pageSize);
            fetchStats();
        } catch (error) {
            console.error("Thao tác thất bại:", error);
        } finally {
            setStatusLoadingMap(prev => ({ ...prev, [id]: false }));
        }
    };

    const handleSave = async () => {
        try {
            const values = await form.validateFields();
            if (values.birthday) {
                values.birthday = values.birthday.format('YYYY-MM-DD');
            }
            
            setLoading(true);
            if (editingId) {
                await ApiService.updateCustomer(editingId, values);
                message.success('Cập nhật thông tin khách hàng thành công!');
            } else {
                await ApiService.createCustomer(values);
                message.success('Thêm mới khách hàng thành công!');
            }
            
            setIsModalOpen(false);
            form.resetFields();
            setEditingId(null);
            fetchCustomers(pagination.current - 1, pagination.pageSize);
            fetchStats();
        } catch (error) {
            console.error("Lỗi lưu dữ liệu:", error);
        } finally {
            setLoading(false);
        }
    };

    const openEditModal = (record) => {
        setEditingId(record.id);
        form.setFieldsValue({
            ...record,
            birthday: record.birthday ? dayjs(record.birthday) : null
        });
        setIsModalOpen(true);
    };

    const openCreateModal = () => {
        setEditingId(null);
        form.resetFields();
        setIsModalOpen(true);
    };

    const columns = [
        {
            title: 'Khách hàng',
            key: 'customerInfo',
            width: '24%',
            render: (_, record) => (
                <Flex gap="middle" align="center">
                    <Avatar 
                        size={40} 
                        style={{ 
                            backgroundColor: getAvatarColor(record.fullName), 
                            color: '#ffffff',
                            fontWeight: 600,
                            fontSize: '16px',
                            opacity: record.active ? 1 : 0.6 
                        }}
                    >
                        {getFirstLetterOfName(record.fullName)}
                    </Avatar>
                    <div>
                        <Text strong style={{ fontSize: '14px', display: 'block' }} delete={!record.active} type={record.active ? undefined : 'secondary'}>
                            {record.fullName}
                        </Text>
                        <Flex gap={4} wrap="wrap" style={{ marginTop: 4 }}>
                            <Tag color={record.gender === 'MALE' ? 'blue' : record.gender === 'FEMALE' ? 'pink' : 'default'} style={{ borderRadius: 4, margin: 0 }}>
                                {record.gender === 'MALE' ? 'Nam' : record.gender === 'FEMALE' ? 'Nữ' : 'Khác'}
                            </Tag>
                            {/* Hiển thị thêm Tag trạng thái hoạt động trực quan khi đứng ở tab "Tất cả" */}
                            {activeTab === 'ALL' && (
                                <Tag color={record.active ? 'success' : 'error'} style={{ borderRadius: 4, margin: 0 }}>
                                    {record.active ? 'Hoạt động' : 'Đã xóa'}
                                </Tag>
                            )}
                        </Flex>
                    </div>
                </Flex>
            )
        },
        {
            title: 'Liên hệ',
            key: 'contact',
            width: '22%',
            render: (_, record) => (
                <Space direction="vertical" size={2}>
                    <Text type="secondary" style={{ fontSize: '13px' }}><PhoneOutlined /> {record.phoneNumber}</Text>
                    {record.email && <Text type="secondary" style={{ fontSize: '13px' }}><MailOutlined /> {record.email}</Text>}
                </Space>
            )
        },
        {
            title: 'Số định danh (CCCD)',
            key: 'identity',
            width: '16%',
            render: (_, record) => (
                <Text style={{ fontSize: '13px' }}>
                    <IdcardOutlined /> {record.identityCard || <Text type="span" style={{ color: '#bfbfbf', italic: true }}>Chưa cập nhật</Text>}
                </Text>
            )
        },
        {
            title: 'Quốc tịch',
            dataIndex: 'nationality',
            key: 'nationality',
            width: '12%',
            render: (text) => text?.trim() ? (
                <Tag icon={<GlobalOutlined />} color="cyan">{text}</Tag>
            ) : (
                <Text type="secondary" italic style={{ color: '#bfbfbf' }}>Chưa cập nhật</Text>
            )
        },
        {
            title: 'Địa chỉ thường trú',
            dataIndex: 'address',
            key: 'address',
            width: '16%',
            render: (text) => text?.trim() ? (
                <Text style={{ fontSize: '13px' }} ellipsis={{ tooltip: text }}><EnvironmentOutlined /> {text}</Text>
            ) : (
                <Text type="secondary" italic style={{ color: '#bfbfbf' }}>Chưa cập nhật</Text>
            )
        },
        {
            title: 'Thao tác',
            key: 'actions',
            width: '12%',
            align: 'center',
            render: (_, record) => (
                <Space size="small">
                    {/* Chỉ cho phép sửa đổi thông tin khi tài khoản đang ở trạng thái hoạt động */}
                    {record.active && (
                        <Button 
                            type="text" 
                            icon={<EditOutlined style={{ color: '#1890ff' }} />} 
                            onClick={() => openEditModal(record)}
                        />
                    )}

                    {record.active ? (
                        <Popconfirm
                            title="Xóa khách hàng"
                            description={`Bạn có chắc muốn đưa khách hàng [${record.fullName}] vào thùng rác không?`}
                            onConfirm={() => handleDeleteCustomer(record.id, record.fullName)}
                            okText="Xóa"
                            cancelText="Hủy"
                            okButtonProps={{ danger: true }}
                        >
                            <Button 
                                type="text" 
                                danger
                                icon={<DeleteOutlined />} 
                                loading={statusLoadingMap[record.id]}
                            />
                        </Popconfirm>
                    ) : (
                        <Popconfirm
                            title="Xác nhận khôi phục?"
                            description="Khách hàng này sẽ quay lại danh sách hoạt động chính."
                            onConfirm={() => handleDeleteCustomer(record.id, record.fullName)}
                            okText="Đồng ý"
                            cancelText="Hủy"
                        >
                            <Button 
                                type="primary"
                                ghost
                                icon={<UndoOutlined />}
                                style={{ borderRadius: '6px', fontSize: '12px', height: '30px', fontWeight: 500 }}
                                loading={statusLoadingMap[record.id]}
                            >
                                Khôi phục
                            </Button>
                        </Popconfirm>
                    )}
                </Space>
            )
        }
    ];

    return (
        <div className="custom-customer-page" style={{ padding: '24px', background: '#f5f7fa', minHeight: '100vh' }}>
            
            {/* 🌟 ĐÃ THÊM: Khu vực Tiêu đề trang và Nút thêm mới đẩy lên đầu trang độc lập */}
            <Flex justify="space-between" align="center" style={{ marginBottom: '24px' }} wrap="wrap" gap="middle">
                <div>
                    <Title level={3} style={{ margin: 0, fontWeight: 700, color: '#1f1f1f' }}>
                        Quản lý khách hàng
                    </Title>
                    <Text type="secondary" style={{ fontSize: '14px' }}>
                        Hệ thống hồ sơ thông tin khách lưu trú, quản lý quốc tịch và thiết lập trạng thái tài khoản.
                    </Text>
                </div>
                
                {/* Nút hành động chính góc trên bên phải (Ẩn khi chọn tab Ngừng hoạt động) */}
                {activeTab !== 'DELETED' && (
                    <Button 
                        type="primary" 
                        icon={<PlusOutlined />} 
                        onClick={openCreateModal} 
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
                        Thêm khách hàng
                    </Button>
                )}
            </Flex>

            {/* Dàn hàng ngang 3 Thẻ Card Thống kê */}
            <Row gutter={24} style={{ marginBottom: '24px' }}>
                <Col span={8}>
                    <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.02)' }}>
                        {statsLoading ? <Skeleton active paragraph={{ rows: 1 }} /> : (
                            <Statistic 
                                title={<Text type="secondary" style={{ fontSize: 14 }}>Khách hàng hoạt động</Text>}
                                value={activeCount} 
                                valueStyle={{ color: '#1677ff', fontWeight: 700, fontSize: 26 }}
                                prefix={<TeamOutlined />}
                                suffix={<Text style={{ fontSize: 13, color: '#aaa', fontWeight: 'normal' }}> người</Text>}
                            />
                        )}
                    </Card>
                </Col>
                <Col span={8}>
                    <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.02)' }}>
                        {statsLoading ? <Skeleton active paragraph={{ rows: 1 }} /> : (
                            <Statistic 
                                title={<Text type="secondary" style={{ fontSize: 14 }}>Khách hàng mới tháng này</Text>}
                                value={newCustomersCount} 
                                valueStyle={{ color: '#52c41a', fontWeight: 700, fontSize: 26 }}
                                prefix={<UserAddOutlined />}
                                suffix={<Text style={{ fontSize: 13, color: '#aaa', fontWeight: 'normal' }}> người</Text>}
                            />
                        )}
                    </Card>
                </Col>
                <Col span={8}>
                    <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.02)' }}>
                        {statsLoading ? <Skeleton active paragraph={{ rows: 1 }} /> : (
                            <Statistic 
                                title={<Text type="secondary" style={{ fontSize: 14 }}>Ngừng hoạt động</Text>}
                                value={deletedCount} 
                                valueStyle={{ color: '#ff4d4f', fontWeight: 700, fontSize: 26 }}
                                prefix={<DeleteOutlined />}
                                suffix={<Text style={{ fontSize: 13, color: '#aaa', fontWeight: 'normal' }}> tài khoản</Text>}
                            />
                        )}
                    </Card>
                </Col>
            </Row>

            {/* Bảng quản lý chính */}
            <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.04)' }}>
                <Flex justify="space-between" align="center" style={{ marginBottom: 20 }} wrap="wrap" gap="middle">
                    <Segmented
                        size="large"
                        options={[
                            { label: 'Tất cả', value: 'ALL', icon: <UnorderedListOutlined /> },
                            { label: 'Đang hoạt động', value: 'ACTIVE', icon: <TeamOutlined /> },
                            { label: 'Ngừng hoạt động', value: 'DELETED', icon: <DeleteOutlined /> }
                        ]}
                        value={activeTab}
                        onChange={(value) => {
                            setActiveTab(value);
                            setPagination(prev => ({ ...prev, current: 1 }));
                        }}
                        style={{ background: '#e4e9f0', padding: '3px', borderRadius: '8px' }}
                    />

                    {/* 🌟 ĐÃ SỬA: Thanh tìm kiếm chiếm trọn góc phải vì nút thêm đã được chuyển đi */}
                    <Input
                        placeholder="Tìm tên, số điện thoại..."
                        prefix={<SearchOutlined style={{ color: '#bfbfbf' }} />}
                        value={keyword}
                        onChange={handleSearchChange}
                        style={{ width: 280, height: 38, borderRadius: 6 }}
                        allowClear
                    />
                </Flex>

                <Table 
                    columns={columns} 
                    dataSource={customers} 
                    rowKey="id"
                    loading={loading}
                    rowClassName={(record) => !record.active ? 'row-deactivated' : ''}
                    pagination={{
                        current: pagination.current,
                        pageSize: pagination.pageSize,
                        total: pagination.total,
                        showTotal: (total) => `Tổng số ${total} khách hàng`,
                        showSizeChanger: true
                    }}
                    onChange={(pag) => {
                        setPagination(pag);
                        fetchCustomers(pag.current - 1, pag.pageSize, keyword, activeTab);
                    }}
                    style={{ background: '#fff' }}
                />
            </Card>

            {/* Modal Form Thêm Mới & Chỉnh Sửa */}
            <Modal
                title={
                    <Title level={4} style={{ margin: 0, display: 'flex', alignItems: 'center', gap: 8 }}>
                        {editingId ? <EditOutlined style={{ color: '#1890ff' }} /> : <PlusOutlined style={{ color: '#52c41a' }} />}
                        {editingId ? 'Cập nhật thông tin khách hàng' : 'Thêm mới khách hàng'}
                    </Title>
                }
                open={isModalOpen}
                onOk={handleSave}
                onCancel={() => setIsModalOpen(false)}
                okText="Xác nhận lưu"
                cancelText="Hủy bỏ"
                width={720}
                confirmLoading={loading}
                destroyOnClose
                modalRender={(modalTxt) => <div style={{ borderRadius: 12, overflow: 'hidden' }}>{modalTxt}</div>}
            >
                <Form form={form} layout="vertical" style={{ marginTop: 20 }}>
                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="fullName" label="Họ và tên" rules={[{ required: true, message: 'Họ tên không được để trống' }]}>
                                <Input prefix={<UserOutlined style={{ color: '#bfbfbf' }} />} placeholder="Nguyễn Văn A" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="phoneNumber" label="Số điện thoại" rules={[{ required: true, message: 'Số điện thoại không được để trống' }]}>
                                <Input prefix={<PhoneOutlined style={{ color: '#bfbfbf' }} />} placeholder="090XXXXXXXX" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={12}>
                            <Form.Item name="identityCard" label="Số CCCD / Hộ chiếu">
                                <Input prefix={<IdcardOutlined style={{ color: '#bfbfbf' }} />} placeholder="Nhập số định danh" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item name="email" label="Địa chỉ Email">
                                <Input prefix={<MailOutlined style={{ color: '#bfbfbf' }} />} placeholder="example@gmail.com" />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={8}>
                            <Form.Item name="gender" label="Giới tính" initialValue="MALE">
                                <Select>
                                    <Option value="MALE">Nam</Option>
                                    <Option value="FEMALE">Nữ</Option>
                                    <Option value="OTHER">Khác</Option>
                                </Select>
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="birthday" label="Ngày sinh">
                                <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" placeholder="Chọn ngày" />
                            </Form.Item>
                        </Col>
                        <Col span={8}>
                            <Form.Item name="nationality" label="Quốc tịch">
                                <Input prefix={<GlobalOutlined style={{ color: '#bfbfbf' }} />} placeholder="Ví dụ: Việt Nam, Hàn Quốc..." />
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item name="address" label="Địa chỉ thường trú">
                        <Input.TextArea rows={2} placeholder="Số nhà, ngõ ngách, tên đường, tỉnh thành..." />
                    </Form.Item>
                </Form>
            </Modal>

            <style>{`
                .custom-customer-page .ant-table-thead > tr > th {
                    background: #f8f9fa !important;
                    font-weight: 600 !important;
                }
                .ant-select-selector, .ant-input-affine-wrapper, .ant-picker, .ant-btn {
                    border-radius: 6px !important;
                }
                .ant-segmented {
                    border-radius: 8px !important;
                }
                /* CSS tạo hiệu ứng mờ nhẹ và nền xám cho các khách hàng đã xoá khi xem ở tab Tất cả */
                .row-deactivated {
                    background-color: #fbfbfc;
                    color: #8c8c8c;
                }
                .row-deactivated td {
                    color: #8c8c8c !important;
                }
            `}</style>
        </div>
    );
};

export default CustomerPage;