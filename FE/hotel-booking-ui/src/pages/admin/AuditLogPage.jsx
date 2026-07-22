import React, { useState, useEffect } from 'react';
import { 
    Table, Input, Card, Tag, Space, Typography, Select, Flex, Avatar, 
    DatePicker, Tooltip, Button, message, Row, Col
} from 'antd';
import { 
    UserOutlined, SearchOutlined, AuditOutlined, ReloadOutlined,
    FileTextOutlined, HomeOutlined, CreditCardOutlined, AppstoreOutlined, 
    TeamOutlined, SettingOutlined, PlusCircleOutlined, CheckCircleOutlined, 
    CloseCircleOutlined, SwapOutlined, LoginOutlined, LogoutOutlined, 
    ArrowRightOutlined, DollarOutlined, RollbackOutlined, EditOutlined
} from '@ant-design/icons';
import { getAuditLogs, getAllEmployees } from '../../api/ApiService';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;
const { RangePicker } = DatePicker;


const MODULE_CONFIG = {
    BOOKING:  { label: 'Đơn đặt phòng', icon: <FileTextOutlined />,  style: { color: '#108ee9', background: '#e6f7ff', borderColor: '#91d5ff' } },
    ROOM:     { label: 'Phòng',         icon: <HomeOutlined />,      style: { color: '#13c2c2', background: '#e6fffb', borderColor: '#87e8de' } },
    PAYMENT:  { label: 'Thanh toán',    icon: <CreditCardOutlined />, style: { color: '#fa8c16', background: '#fff7e6', borderColor: '#ffd591' } },
    SERVICE:  { label: 'Dịch vụ',       icon: <AppstoreOutlined />,  style: { color: '#722ed1', background: '#f9f0ff', borderColor: '#d3adf7' } },
    CUSTOMER: { label: 'Khách hàng',    icon: <TeamOutlined />,      style: { color: '#52c41a', background: '#f6ffed', borderColor: '#b7eb8f' } },
    USER:     { label: 'Người dùng',    icon: <SettingOutlined />,   style: { color: '#666666', background: '#f5f5f5', borderColor: '#d9d9d9' } }
};


const ACTION_CONFIG = {
    UPDATE:             { color: 'default',  icon: <EditOutlined />,          label: 'Cập nhật thông tin' },
    CHECK_IN:           { color: 'blue',     icon: <LoginOutlined />,         label: 'Check-in' },
    CHECK_OUT:          { color: 'purple',   icon: <LogoutOutlined />,        label: 'Check-out' },
    CLEANING_COMPLETED: { color: 'success',  icon: <CheckCircleOutlined />,   label: 'Dọn phòng xong' },
    ASSIGN_ROOM:        { color: 'cyan',     icon: <PlusCircleOutlined />,    label: 'Xếp số phòng' },
    CHANGE_ROOM_NUMBER: { color: 'orange',   icon: <SwapOutlined />,          label: 'Đổi số phòng' },
    CHANGE_ROOM_TYPE:   { color: 'warning',  icon: <ArrowRightOutlined />,    label: 'Đổi loại phòng' },
    ADD_ROOM_TYPE:      { color: 'geekblue', icon: <PlusCircleOutlined />,    label: 'Thêm loại phòng' },
    CANCEL_ROOM_ITEM:   { color: 'volcano',  icon: <CloseCircleOutlined />,   label: 'Hủy phòng lẻ' },
    CREATE_BOOKING_CASH:{ color: '#52c41a',  icon: <DollarOutlined />,        label: 'Tạo đơn (Tiền mặt)' }, 
    CANCEL_BOOKING:     { color: 'error',    icon: <CloseCircleOutlined />,   label: 'Hủy đơn' },
    ADD_SERVICE:        { color: 'geekblue', icon: <PlusCircleOutlined />,    label: 'Thêm dịch vụ' },
    UPDATE_SERVICE:     { color: 'gold',     icon: <EditOutlined />,          label: 'Cập nhật dịch vụ' },
    CANCEL_SERVICE:     { color: 'red',      icon: <CloseCircleOutlined />,   label: 'Hủy dịch vụ' },
    REFUND:             { color: 'magenta',  icon: <RollbackOutlined />,      label: 'Hoàn tiền' },
    SUCCESS:            { color: 'green',    icon: <CheckCircleOutlined />,   label: 'Thanh toán thành công' } 
};

const AuditLogPage = () => {
    const [logs, setLogs] = useState([]);
    const [employees, setEmployees] = useState([]); 
    const [loading, setLoading] = useState(false);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
    
    const initialFilters = { 
        module: null, action: null, targetId: null, employeeId: null, fromDate: null, toDate: null 
    };
    const [filters, setFilters] = useState(initialFilters);

    useEffect(() => {
        const fetchEmployees = async () => {
            try {
                const data = await getAllEmployees({ page: 0, size: 100 }); 
                if (data && data.content) {
                    setEmployees(data.content);
                } else if (Array.isArray(data)) {
                    setEmployees(data);
                } else {
                    setEmployees([]);
                }
            } catch (err) {
                console.error("Không tải được danh sách nhân viên", err);
                message.error("Lỗi khi tải danh sách nhân viên");
            }
        };
        fetchEmployees();
    }, []);

    const fetchLogs = async (page = 1, pageSize = 10) => {
        setLoading(true);
        try {
            const params = { 
                ...filters, 
                page: page - 1, 
                size: pageSize, 
                sort: 'createdAt,desc' 
            };
            const data = await getAuditLogs(params);
            setLogs(data?.content || []);
            setPagination({ current: page, pageSize: pageSize, total: data?.totalElements || 0 });
        } catch (error) {
            message.error("Lỗi khi tải nhật ký hệ thống");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchLogs(1, pagination.pageSize);
    }, [filters]);

    const handleFilterChange = (key, value) => {
        setFilters(prev => ({ ...prev, [key]: value ?? null }));
    };

    const handleReset = () => {
        setFilters(initialFilters);
        message.success("Đã làm mới tất cả bộ lọc");
    };

    const columns = [
        { 
            title: 'ID', 
            dataIndex: 'id', 
            width: 80, 
            align: 'center',
            render: (id) => <span style={{ color: '#8c8c8c', fontFamily: 'monospace', fontWeight: 600 }}>#{id}</span>
        },
        { 
            title: 'Phân hệ (Module)', 
            dataIndex: 'module', 
            width: 170, 
            render: (text) => {
                const config = MODULE_CONFIG[text];
                if (!config) return <Tag color="default">{text}</Tag>;
                return (
                    <Tag 
                        icon={config.icon} 
                        style={{ 
                            ...config.style,
                            borderRadius: '6px', 
                            padding: '5px 10px', 
                            fontWeight: 600,
                            borderWidth: '1px',
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '4px'
                        }}
                    >
                        {config.label}
                    </Tag>
                );
            }
        },
        { 
            title: 'Hành động', 
            dataIndex: 'action', 
            width: 210, 
            align: 'left', 
            render: (act) => {
                const config = ACTION_CONFIG[act] || { color: 'default', icon: <SettingOutlined />, label: act };
                return (
                    <Tag 
                        color={config.color} 
                        icon={config.icon}
                        style={{ 
                            borderRadius: '20px', 
                            padding: '4px 12px',
                            fontWeight: 500,
                            boxShadow: '0 2px 4px rgba(0,0,0,0.02)',
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '4px'
                        }}
                    >
                        {config.label}
                    </Tag>
                );
            }
        },
        { 
            title: 'Target ID', 
            dataIndex: 'targetId', 
            width: 100, 
            align: 'center',
            render: (id) => id ? <Tag color="blue" style={{ fontWeight: 'bold' }}>{id}</Tag> : <Text type="secondary">-</Text>
        },
        { 
            title: 'Người thực hiện', 
            dataIndex: 'performedBy', 
            width: 220,
            render: (text, record) => {
                const isSystem = text === 'Hệ thống' || !text;
                return (
                    <Flex align="center" gap="middle">
                        <Avatar 
                            size={32} 
                            icon={isSystem ? <SettingOutlined /> : <UserOutlined />} 
                            style={{ 
                                backgroundColor: isSystem ? '#f5f5f5' : '#e6f7ff',
                                color: isSystem ? '#bfbfbf' : '#1890ff',
                                border: `1px solid ${isSystem ? '#d9d9d9' : '#91d5ff'}`
                            }}
                        />
                        <Flex vertical justify="center">
                            <Text style={{ fontWeight: 600, color: '#262626', lineHeight: '1.4' }}>
                                {text || 'Hệ thống'}
                            </Text>
                            <Text style={{ fontSize: '11px', color: '#8c8c8c' }}>
                                {isSystem ? 'Tác vụ tự động' : `ID NV: ${record.performedById || 'N/A'}`}
                            </Text>
                        </Flex>
                    </Flex>
                );
            }
        },
        {
            title: 'Mô tả chi tiết', 
            dataIndex: 'description', 
            ellipsis: true,
            render: (text) => (
                <Tooltip title={text} placement="topLeft" mouseEnterDelay={0.3}>
                    <span style={{ color: '#434343', fontWeight: 400 }}>{text || '-'}</span>
                </Tooltip>
            )
        },
        { 
            title: 'Thời gian', 
            dataIndex: 'createdAt', 
            width: 180, 
            align: 'right',
            render: (text) => (
                <Text style={{ color: '#595959', fontFamily: 'monospace' }}>
                    {text ? dayjs(text).format('DD/MM/YYYY HH:mm:ss') : 'N/A'}
                </Text>
            )
        },
    ];

    return (
        <div style={{ padding: '4px', background: '#f8f9fa', borderRadius: '16px' }}>
            <Card 
                bordered={false} 
                style={{ 
                    borderRadius: '16px', 
                    boxShadow: '0 4px 20px rgba(0,0,0,0.03)' 
                }}
            >
                <Flex vertical gap="large">
                    {/* Header */}
                    <Flex justify="space-between" align="center" style={{ borderBottom: '1px solid #f0f0f0', paddingBottom: '16px' }}>
                        <Space size="middle">
                            <div style={{ padding: '8px', background: '#e6f7ff', borderRadius: '10px', display: 'flex', color: '#1890ff' }}>
                                <AuditOutlined style={{ fontSize: '22px' }} />
                            </div>
                            <div>
                                <Title level={4} style={{ margin: 0, fontWeight: 700, color: '#141414' }}>Nhật ký hệ thống</Title>
                                <Text type="secondary" style={{ fontSize: '13px' }}>Giám sát hành động và lịch sử thay đổi của dữ liệu</Text>
                            </div>
                        </Space>
                        <Button 
                            type="text" 
                            icon={<ReloadOutlined />} 
                            onClick={handleReset}
                            style={{ borderRadius: '8px', color: '#595959' }}
                        >
                            Làm mới bộ lọc
                        </Button>
                    </Flex>

                    {/* Filter Section (Responsive Grid) */}
                    <div style={{ background: '#fafafa', padding: '18px', borderRadius: '12px', border: '1px solid #f0f0f0' }}>
                        <Row gitter={[16, 16]} gutter={[12, 12]}>
                            <Col xs={24} sm={12} md={4}>
                                <Input 
                                    placeholder="Tìm Target ID..." 
                                    allowClear 
                                    prefix={<SearchOutlined style={{ color: '#bfbfbf' }} />}
                                    style={{ width: '100%', borderRadius: '8px' }}
                                    value={filters.targetId || ''}
                                    onChange={(e) => handleFilterChange('targetId', e.target.value)}
                                />
                            </Col>
                            
                            <Col xs={24} sm={12} md={5}>
                                <Select 
                                    placeholder="Chọn Phân hệ (Module)" 
                                    allowClear 
                                    style={{ width: '100%' }}
                                    dropdownStyle={{ borderRadius: '8px' }}
                                    value={filters.module} 
                                    onChange={val => handleFilterChange('module', val)}
                                >
                                    {Object.entries(MODULE_CONFIG).map(([key, config]) => (
                                        <Option key={key} value={key}>
                                            <Space>{config.icon} {config.label}</Space>
                                        </Option>
                                    ))}
                                </Select>
                            </Col>

                            <Col xs={24} sm={12} md={5}>
                                <Select 
                                    placeholder="Chọn Hành động" 
                                    allowClear 
                                    style={{ width: '100%' }}
                                    dropdownStyle={{ borderRadius: '8px' }}
                                    value={filters.action} 
                                    onChange={val => handleFilterChange('action', val)}
                                >
                                    {Object.entries(ACTION_CONFIG).map(([key, config]) => (
                                        <Option key={key} value={key}>
                                            <Tag color={config.color} icon={config.icon} style={{ borderRadius: '10px', margin: 0 }}>
                                                {config.label}
                                            </Tag>
                                        </Option>
                                    ))}
                                </Select>
                            </Col>

                            <Col xs={24} sm={12} md={5}>
                                <Select 
                                    placeholder="Lọc theo Nhân viên" 
                                    allowClear 
                                    style={{ width: '100%' }}
                                    dropdownStyle={{ borderRadius: '8px' }}
                                    value={filters.employeeId} 
                                    onChange={val => handleFilterChange('employeeId', val)}
                                >
                                    {employees.map(emp => (
                                        <Option key={emp.id} value={emp.id}>
                                            <Space>
                                                <Avatar size="small" icon={<UserOutlined />} src={emp.avatarUrl} />
                                                {emp.fullName}
                                            </Space>
                                        </Option>
                                    ))}
                                </Select>
                            </Col>

                            <Col xs={24} md={5}>
                                <RangePicker 
                                    showTime 
                                    format="DD/MM/YYYY HH:mm"
                                    style={{ width: '100%', borderRadius: '8px' }}
                                    value={filters.fromDate ? [dayjs(filters.fromDate), dayjs(filters.toDate)] : null}
                                    onChange={(dates) => {
                                        setFilters(prev => ({
                                            ...prev,
                                            fromDate: dates ? dates[0].toISOString() : null,
                                            toDate: dates ? dates[1].toISOString() : null
                                        }));
                                    }}
                                />
                            </Col>
                        </Row>
                    </div>

                    {/* Styled Data Table */}
                    <div className="custom-audit-table">
                        <Table 
                            columns={columns} 
                            dataSource={logs} 
                            rowKey="id" 
                            loading={loading}
                            pagination={{ 
                                ...pagination, 
                                showSizeChanger: true,
                                pageSizeOptions: ['10', '20', '50'],
                                showTotal: (total, range) => `Hiển thị từ ${range[0]} đến ${range[1]} trong tổng số ${total} bản ghi`
                            }}
                            onChange={(pag) => fetchLogs(pag.current, pag.pageSize)}
                            style={{ borderRadius: '8px' }}
                        />
                    </div>
                </Flex>
            </Card>

            {/* Custom CSS Style injecting for dynamic hover smoothly */}
            <style>{`
                .custom-audit-table .ant-table-thead > tr > th {
                    background: #fafafa !important;
                    font-weight: 600 !important;
                    color: #434343 !important;
                    border-bottom: 2px solid #f0f0f0 !important;
                }
                .custom-audit-table .ant-table-tbody > tr {
                    transition: all 0.2s ease !important;
                }
                .custom-audit-table .ant-table-tbody > tr:hover > td {
                    background: #f9fbfd !important;
                    cursor: pointer;
                }
                .ant-select-selector, .ant-picker {
                    border-radius: 8px !important;
                }
            `}</style>
        </div>
    );
};

export default AuditLogPage;