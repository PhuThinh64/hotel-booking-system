import React, { useEffect, useState, useCallback } from 'react';
import { 
    Table, Typography, Button, Modal, Form, Input, InputNumber, 
    message, Space, Popconfirm, Row, Col, Card, Statistic, 
    Segmented, Select, Tag, Flex, Tooltip
} from 'antd';
import { 
    PlusOutlined, CoffeeOutlined, DollarCircleOutlined, 
    SearchOutlined, EditOutlined, DeleteOutlined, UndoOutlined,
    ExperimentOutlined, WarningOutlined
} from '@ant-design/icons';
import * as ApiService from '../../api/ApiService';

const { Text, Title } = Typography;
const { Option } = Select;

const ServicePage = () => {
    
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const isAdmin = user.role === 'ROLE_ADMIN';

    
    const [data, setData] = useState([]);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [form] = Form.useForm();

    
    const [searchInputValue, setSearchInputValue] = useState("");
    const [searchText, setSearchText] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL"); 
    const [typeFilter, setTypeFilter] = useState("ALL");     
    const [currentPage, setCurrentPage] = useState(0);
    const pageSize = 5; 

    
    const [globalStats, setGlobalStats] = useState({ total: 0, inactive: 0, maxPrice: 0 });

    
    const loadGlobalStats = useCallback(async () => {
        try {
            const res = await ApiService.getExtraServices(0, 1000, "", null, null);
            if (res) {
                const result = res.content ? res : (res.data?.result || res);
                const list = result.content || [];
                
                const total = result.totalElements || list.length;
                const inactive = list.filter(item => item.active === false).length;
                
                
                const activeList = list.filter(item => item.active !== false);
                const maxPrice = activeList.length > 0 ? Math.max(...activeList.map(item => item.price || 0)) : 0;
                
                setGlobalStats({ total, inactive, maxPrice });
            }
        } catch (err) {
            console.error("Không thể tải số liệu thống kê tổng hợp:", err);
        }
    }, []);

    
    useEffect(() => {
        const handler = setTimeout(() => {
            setSearchText(searchInputValue);
            setCurrentPage(0); 
        }, 400);
        return () => clearTimeout(handler);
    }, [searchInputValue]);

    
    const loadData = useCallback(async (page = 0, name = searchText, status = statusFilter, type = typeFilter) => {
        setLoading(true);
        try {
            let activeParam = null;
            if (status === "ACTIVE") activeParam = true;
            if (status === "INACTIVE") activeParam = false;

            let typeParam = type === "ALL" ? null : type;

            const res = await ApiService.getExtraServices(page, pageSize, name, activeParam, typeParam);
            if (res) {
                const result = res.content ? res : (res.data?.result || res);
                setData(result.content || []);
                setTotal(result.totalElements || 0);
                setCurrentPage(page);
            }
        } catch (err) {
            console.error("Tải danh mục dịch vụ thất bại:", err);
            if (err.response?.status !== 401) {
                message.error("Không thể kết nối tới máy chủ để cập nhật danh sách dịch vụ");
            }
        } finally {
            setLoading(false);
        }
    }, [searchText, statusFilter, typeFilter]);

    
    useEffect(() => {
        loadData(currentPage);
    }, [loadData, currentPage]);

    useEffect(() => {
        loadGlobalStats();
    }, [loadGlobalStats, data]);

    const openModal = (record = null) => {
        if (record) {
            setEditingId(record.id);
            form.setFieldsValue(record);
        } else {
            setEditingId(null);
            form.resetFields();
        }
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setEditingId(null);
        form.resetFields();
    };

    const onFinish = async (values) => {
        try {
            if (editingId) {
                await ApiService.updateExtraService(editingId, { ...values, active: values.active ?? true });
                message.success('Cập nhật thông tin dịch vụ thành công!');
            } else {
                await ApiService.createExtraService(values);
                message.success('Thêm mới dịch vụ thành công!');
                setSearchInputValue("");
                setStatusFilter("ALL");
                setTypeFilter("ALL");
            }
            closeModal();
            loadData(0);
            loadGlobalStats();
        } catch (err) {
            const errorMsg = err.response?.data?.message || "Thao tác thất bại!";
            message.error(errorMsg);
        }
    };

    const confirmDelete = async (id) => {
        try {
            await ApiService.deleteExtraService(id);
            message.success('Đã tạm ẩn cung cấp dịch vụ thành công!');
            loadData(currentPage);
            loadGlobalStats();
        } catch (err) {
            message.error('Không thể thực hiện ẩn dịch vụ này!');
        }
    };

    
    const handleRestore = async (record) => {
        try {
            await ApiService.updateExtraService(record.id, { ...record, active: true });
            message.success(`Đã khôi phục thành công dịch vụ [${record.name}] về trạng thái hoạt động!`);
            loadData(currentPage);
            loadGlobalStats();
        } catch (err) {
            message.error('Không thể thực hiện khôi phục dịch vụ này!');
        }
    };

    const renderServiceTypeTag = (type) => {
        switch (type) {
            case 'REGULAR':
                return <Tag color="blue" icon={<CoffeeOutlined />} style={{ borderRadius: '6px', padding: '2px 8px' }}>Thông thường</Tag>;
            case 'ADDITIONAL':
                return <Tag color="purple" icon={<ExperimentOutlined />} style={{ borderRadius: '6px', padding: '2px 8px' }}>Dịch vụ bổ sung</Tag>;
            case 'DAMAGE_FINE':
                return <Tag color="volcano" icon={<WarningOutlined />} style={{ borderRadius: '6px', padding: '2px 8px' }}>Phí phạt đền bù</Tag>;
            default:
                return <Tag style={{ borderRadius: '6px' }}>{type}</Tag>;
        }
    };

    const columns = [
        { 
            title: 'ID', 
            dataIndex: 'id', 
            key: 'id', 
            width: 70,
            align: 'center',
            render: (id) => <span style={{ fontFamily: 'monospace', color: '#8c8c8c' }}>{id}</span>
        },
        { 
            title: 'Tên dịch vụ', 
            dataIndex: 'name', 
            key: 'name', 
            width: 220,
            render: (text, record) => (
                <Text strong style={{ color: record.active === false ? '#bfbfbf' : 'inherit' }}>
                    {text}
                </Text>
            ) 
        },
        { 
            title: 'Phân loại nhóm', 
            dataIndex: 'serviceType', 
            key: 'serviceType',
            width: 160,
            render: (type) => renderServiceTypeTag(type)
        },
        { 
            title: 'Mô tả chi tiết', 
            dataIndex: 'description', 
            key: 'description', 
            ellipsis: { tooltip: true },
            render: (text, record) => <span style={{ color: record.active === false ? '#d9d9d9' : '#595959' }}>{text || '---'}</span>
        },
        {
            title: 'Giá (VNĐ)',
            dataIndex: 'price',
            key: 'price',
            align: 'right',
            width: 150,
            render: (p, record) => (
                <span style={{ color: record.active === false ? '#bfbfbf' : '#fa8c16', fontWeight: 'bold', fontSize: '14px' }}>
                    {p?.toLocaleString()}₫
                </span>
            )
        },
        {
            title: 'Trạng thái',
            dataIndex: 'active',
            key: 'active',
            align: 'center',
            width: 140,
            render: (active) => (
                active !== false 
                    ? <Tag color="success" style={{ borderRadius: '4px' }}>Đang áp dụng</Tag>
                    : <Tag color="default" style={{ borderRadius: '4px', color: '#bfbfbf' }}>Ngừng hoạt động</Tag>
            )
        },
        
        ...(isAdmin ? [{
            title: 'Thao tác',
            key: 'action',
            width: 160, 
            align: 'center',
            render: (_, record) => (
                <Space size="small">
                    {record.active !== false ? (
                        <>
                            <Tooltip title="Chỉnh sửa cấu hình">
                                <Button 
                                    type="text" 
                                    size="small"
                                    icon={<EditOutlined style={{ color: '#1677ff' }} />} 
                                    onClick={() => openModal(record)}
                                    style={{ background: '#e6f7ff', borderRadius: '6px' }}
                                />
                            </Tooltip>
                            
                            <Popconfirm 
                                title="Ngưng cung cấp dịch vụ?" 
                                description="Hệ thống sẽ chuyển dịch vụ này sang trạng thái tạm ẩn."
                                onConfirm={() => confirmDelete(record.id)} 
                                okText="Xác nhận" 
                                cancelText="Hủy"
                                okButtonProps={{ danger: true }}
                            >
                                <Tooltip title="Tạm ẩn dịch vụ">
                                    <Button 
                                        type="text" 
                                        size="small"
                                        danger
                                        icon={<DeleteOutlined />} 
                                        style={{ background: '#fff1f0', borderRadius: '6px' }}
                                    />
                                </Tooltip>
                            </Popconfirm>
                        </>
                    ) : (
                        /* KHI DỊCH VỤ ĐÃ ẨN: Hiện cả Icon và chữ "Khôi phục" trực quan */
                        <Popconfirm 
                            title="Khôi phục hoạt động?" 
                            description={`Bạn muốn đưa dịch vụ [${record.name}] trở lại danh sách áp dụng chứ?`}
                            onConfirm={() => handleRestore(record)} 
                            okText="Khôi phục" 
                            cancelText="Hủy"
                            okButtonProps={{ type: 'primary' }}
                        >
                            <Button 
                                type="text" 
                                size="small"
                                icon={<UndoOutlined style={{ color: '#52c41a' }} />} 
                                style={{ 
                                    background: '#f6ffed', 
                                    borderRadius: '6px', 
                                    color: '#52c41a', 
                                    fontWeight: 500,
                                    padding: '2px 8px'
                                }}
                            >
                                Khôi phục
                            </Button>
                        </Popconfirm>
                    )}
                </Space>
            ),
        }] : [])
    ];

    return (
        <div className="custom-service-page" style={{ padding: '24px', backgroundColor: '#f8f9fa', minHeight: '100vh' }}>
            
            {/* 1. HEADER */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <div>
                    <Title level={3} style={{ margin: 0, fontWeight: 700, color: '#1f1f1f' }}>Thiết lập đơn giá dịch vụ</Title>
                    <Text type="secondary">Cập nhật trạng thái, cấu hình định mức giá áp dụng cho toàn hệ thống</Text>
                </div>
                {isAdmin && (
                    <Button
                        style={{ 
                            background: '#52c41a', 
                            borderColor: '#52c41a', 
                            height: 40, 
                            padding: '0 20px',
                            fontWeight: 600, 
                            borderRadius: 6,
                            boxShadow: '0 2px 4px rgba(82, 196, 26, 0.2)'
                        }}
                         type = "primary" size="large" icon={<PlusOutlined />} onClick={() => openModal()} >
                        Thêm dịch vụ mới
                    </Button>
                )}
            </div>

            {/* 2. 3 CARD THỐNG KÊ */}
            <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
                <Col xs={24} sm={8}>
                    <Card bordered={false} className="custom-stat-card">
                        <Statistic 
                            title={<span className="stat-card-title">Tổng số loại dịch vụ</span>} 
                            value={globalStats.total} 
                            prefix={<CoffeeOutlined style={{ color: '#1890ff', backgroundColor: '#e6f7ff', padding: '8px', borderRadius: '50%' }} />} 
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={8}>
                    <Card bordered={false} className="custom-stat-card">
                        <Statistic 
                            title={<span className="stat-card-title">Dịch vụ bị xóa/ẩn</span>} 
                            value={globalStats.inactive} 
                            valueStyle={{ color: '#ff4d4f' }} 
                            prefix={<WarningOutlined style={{ color: '#ff4d4f', backgroundColor: '#fff1f0', padding: '8px', borderRadius: '50%' }} />} 
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={8}>
                    <Card bordered={false} className="custom-stat-card">
                        <Statistic 
                            title={<span className="stat-card-title">Đơn giá dịch vụ cao nhất (còn hoạt động)</span>} 
                            value={globalStats.maxPrice} 
                            precision={0}
                            suffix="đ"
                            prefix={<DollarCircleOutlined style={{ color: '#52c41a', backgroundColor: '#f6ffed', padding: '8px', borderRadius: '50%' }} />} 
                            valueStyle={{ color: '#52c41a', fontWeight: 'bold' }}
                        />
                    </Card>
                </Col>
            </Row>

            {/* 3. THANH TÌM KIẾM & BỘ LỌC */}
            <Card bordered={false} className="table-wrapper-card" style={{ borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.02)' }}>
                <Flex justify="space-between" align="center" style={{ marginBottom: '20px' }} wrap="wrap" gap={16}>
                    <Input 
                        placeholder="Tìm nhanh theo tên dịch vụ cung cấp..." 
                        prefix={<SearchOutlined style={{ color: '#bfbfbf' }} />} 
                        value={searchInputValue}
                        onChange={(e) => setSearchInputValue(e.target.value)}
                        style={{ width: '340px', height: '40px', borderRadius: '8px' }}
                        allowClear
                    />
                    
                    <Flex gap={12} align="center">
                        <Select value={typeFilter} style={{ width: 180, height: '40px' }} onChange={setTypeFilter}>
                            <Option value="ALL">Tất cả phân loại</Option>
                            <Option value="REGULAR">Thông thường</Option>
                            <Option value="ADDITIONAL">Dịch vụ bổ sung</Option>
                            <Option value="DAMAGE_FINE">Phí phạt đền bù</Option>
                        </Select>

                        <Segmented 
                            size="large"
                            options={[
                                { label: 'Tất cả', value: 'ALL' },
                                { label: 'Đang áp dụng', value: 'ACTIVE' },
                                { label: 'Ngừng hoạt động', value: 'INACTIVE' }
                            ]}
                            value={statusFilter}
                            onChange={(value) => {
                                setStatusFilter(value);
                                setCurrentPage(0);
                            }}
                            style={{ padding: '4px', borderRadius: '8px', background: '#f0f2f5' }}
                        />
                    </Flex>
                </Flex>

                {/* BẢNG DANH SÁCH */}
                <Table 
                    columns={columns} 
                    dataSource={data} 
                    loading={loading}
                    rowKey="id"
                    pagination={{
                        current: currentPage + 1,
                        pageSize: pageSize,
                        total: total,
                        onChange: (page) => setCurrentPage(page - 1),
                        showSizeChanger: false,
                        position: ['bottomRight']
                    }}
                />
            </Card>

            {/* MODAL BIỂU MẪU */}
            <Modal
                title={<span style={{ fontSize: '18px', fontWeight: 700, color: '#1f1f1f' }}>{editingId ? "Cập nhật dữ liệu dịch vụ" : "Thiết lập cấu hình dịch vụ mới"}</span>}
                open={isModalOpen}
                onCancel={closeModal}
                onOk={() => form.submit()}
                destroyOnClose
                okText="Lưu thông tin"
                cancelText="Hủy bỏ"
                width={600}
            >
                <Form form={form} layout="vertical" onFinish={onFinish} style={{ marginTop: '20px' }}>
                    <Row gutter={16}>
                        <Col span={14}>
                            <Form.Item name="name" label="Tên dịch vụ cung cấp" rules={[{ required: true, message: 'Tên danh mục không được bỏ trống!' }]}>
                                <Input placeholder="Ví dụ: Giặt sấy quần áo, Ăn sáng buffet..." style={{ height: '40px' }} />
                            </Form.Item>
                        </Col>
                        <Col span={10}>
                            <Form.Item name="serviceType" label="Phân loại nhóm nghiệp vụ" initialValue="REGULAR">
                                <Select placeholder="Chọn nhóm" style={{ height: '40px' }}>
                                    <Option value="REGULAR">Dịch vụ thông thường</Option>
                                    <Option value="ADDITIONAL">Dịch vụ bổ sung</Option>
                                    <Option value="DAMAGE_FINE">Phí phạt đền bù hư tổn</Option>
                                </Select>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Row gutter={16}>
                        <Col span={14}>
                            <Form.Item name="price" label="Đơn giá áp dụng (VNĐ)" rules={[{ required: true, message: 'Đơn giá bắt buộc nhập giá trị hợp lệ!' }]}>
                                <InputNumber
                                    style={{ width: '100%', height: '40px', display: 'flex', alignItems: 'center' }}
                                    min={0}
                                    placeholder="Nhập giá tiền..."
                                    formatter={v => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                                    parser={v => v.replace(/\$\s?|(,*)/g, '')}
                                />
                            </Form.Item>
                        </Col>
                        <Col span={10}>
                            <Form.Item name="active" label="Trạng thái hoạt động" initialValue={true}>
                                <Select style={{ height: '40px' }}>
                                    <Option value={true}>Đang áp dụng</Option>
                                    <Option value={false}>Tạm ẩn / Ngừng dùng</Option>
                                </Select>
                            </Form.Item>
                        </Col>
                    </Row>

                    <Form.Item name="description" label="Ghi chú chi tiết / Quy định dịch vụ">
                        <Input.TextArea rows={4} placeholder="Nhập thêm điều kiện hoặc nội dung áp dụng cho lễ tân tiện quản lý..." style={{ borderRadius: '8px' }} />
                    </Form.Item>
                </Form>
            </Modal>

            {/* Custom CSS Style */}
            <style>{`
                .custom-stat-card {
                    border-radius: 12px !important;
                    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03) !important;
                    transition: transform 0.3s ease, box-shadow 0.3s ease;
                }
                .custom-stat-card:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.06) !important;
                }
                .stat-card-title {
                    color: #8c8c8c;
                    font-size: 14px;
                    font-weight: 500;
                }
                .custom-service-page .ant-table-thead > tr > th {
                    background: #f5f5f5 !important;
                    font-weight: 600 !important;
                    color: #262626 !important;
                }
                .ant-input, .ant-select-selector, .ant-input-number, .ant-btn {
                    border-radius: 8px !important;
                }
                .ant-segmented-item-selected {
                    font-weight: 600 !important;
                    color: #1677ff !important;
                }
            `}</style>
        </div>
    );
};

export default ServicePage;