import React, { useEffect, useState, useCallback } from 'react';
import { 
  Table, Button, Space, Input, Tag, Card, Modal, Form, 
  Select, message, Row, Col, Statistic, Popconfirm, InputNumber, Flex, Segmented, Skeleton, Typography
} from 'antd';
import { 
  PlusOutlined, SearchOutlined, HomeOutlined, DeleteOutlined, 
  EditOutlined, UndoOutlined, CheckCircleOutlined, SyncOutlined
} from '@ant-design/icons';
import * as ApiService from '../../api/ApiService';

const { Title, Text } = Typography;

const RoomPage = () => {
  const [form] = Form.useForm();
  
  
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]); 
  const [loading, setLoading] = useState(false);
  const [statusLoadingMap, setStatusLoadingMap] = useState({});
  
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);

  
  const [searchInputValue, setSearchInputValue] = useState('');
  const [roomNumber, setRoomNumber] = useState('');
  const [statusFilter, setStatusFilter] = useState(undefined);
  const [floorFilter, setFloorFilter] = useState(undefined);
  const [roomTypeIdFilter, setRoomTypeIdFilter] = useState(undefined);
  const [activeTab, setActiveTab] = useState(true); 
  
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  
  const [activeCount, setActiveCount] = useState(0);
  const [deletedCount, setDeletedCount] = useState(0);
  const [availableCount, setAvailableCount] = useState(0); 

  
  const user = JSON.parse(localStorage.getItem("user") || '{}'); 
  const isAdmin = user?.role === "ROLE_ADMIN";

  const [availableFloors, setAvailableFloors] = useState([1, 2, 3, 4, 5]);

  
  useEffect(() => {
    const handler = setTimeout(() => {
      setRoomNumber(searchInputValue);
      setPagination(prev => ({ ...prev, current: 1 }));
    }, 300);
    return () => clearTimeout(handler);
  }, [searchInputValue]);

  
  const refreshStats = useCallback(async () => {
    try {
      const [resActive, resDeleted, resAvailable, floors] = await Promise.all([
        ApiService.getRooms({ active: true, page: 0, size: 1 }).catch(() => ({ totalElements: 0 })),
        ApiService.getRooms({ active: false, page: 0, size: 1 }).catch(() => ({ totalElements: 0 })),
        ApiService.getRooms({ active: true, status: 'AVAILABLE', page: 0, size: 1 }).catch(() => ({ totalElements: 0 })),
        ApiService.getDistinctFloors().catch(() => [1, 2, 3, 4, 5])
      ]);
      
      const totalActive = resActive?.totalElements ?? resActive?.data?.totalElements ?? resActive?.data?.result?.totalElements ?? 0;
      const totalDeleted = resDeleted?.totalElements ?? resDeleted?.data?.totalElements ?? resDeleted?.data?.result?.totalElements ?? 0;
      const totalAvailable = resAvailable?.totalElements ?? resAvailable?.data?.totalElements ?? resAvailable?.data?.result?.totalElements ?? 0;

      setActiveCount(totalActive);
      setDeletedCount(totalDeleted);
      setAvailableCount(totalAvailable); 
      setAvailableFloors(floors || [1, 2, 3, 4, 5]);
      
      if (typeof ApiService.getRoomTypes === 'function') {
        const resTypes = await ApiService.getRoomTypes({ active: true, page: 0, size: 50 });
        setRoomTypes(resTypes?.content || resTypes?.data?.content || []);
      }
    } catch (e) {
      console.error("Lỗi cập nhật số liệu thống kê tổng hợp phòng:", e);
    }
  }, []);

  
  const fetchRooms = useCallback(async (
    page = 1, 
    pageSize = 10, 
    searchNum = roomNumber, 
    status = statusFilter, 
    active = activeTab, 
    floor = floorFilter, 
    typeId = roomTypeIdFilter
  ) => {
    setLoading(true);
    try {
      const res = await ApiService.getRooms({
        page: page - 1,
        size: pageSize,
        roomNumber: searchNum || undefined,
        status: status || undefined,
        active: active,
        floor: floor || undefined,
        roomTypeId: typeId || undefined
      });
      
      const content = res?.content || res?.data?.content || [];
      const total = res?.totalElements || res?.data?.totalElements || 0;

      setRooms(content);
      setPagination(prev => ({ ...prev, current: page, total: total }));
    } catch (e) {
      message.error("Hệ thống gặp sự cố khi tải danh sách phòng!");
    } finally {
      setLoading(false);
    }
  }, [roomNumber, statusFilter, activeTab, floorFilter, roomTypeIdFilter]);

  
  const handleResetFilters = useCallback(() => {
    setSearchInputValue('');
    setRoomNumber('');
    setStatusFilter(undefined);
    setFloorFilter(undefined);
    setRoomTypeIdFilter(undefined);
    setPagination(p => ({ ...p, current: 1 }));
    message.success("Đã làm mới thanh công cụ tìm kiếm thành công!");
  }, []);

  useEffect(() => {
    fetchRooms(pagination.current, pagination.pageSize, roomNumber, statusFilter, activeTab, floorFilter, roomTypeIdFilter);
  }, [fetchRooms, pagination.current, pagination.pageSize, roomNumber, statusFilter, activeTab, floorFilter, roomTypeIdFilter]);

  useEffect(() => {
    refreshStats();
  }, [refreshStats]);

  const handleOpenModal = (record = null) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue({
        roomNumber: record.roomNumber,
        roomTypeId: record.roomTypeId || (roomTypes.find(t => t.name === record.roomType)?.id),
        floor: record.floor,
        status: record.status
      });
    } else {
      setEditingId(null);
      form.resetFields();
    }
    setIsModalOpen(true);
  };

  const handleSave = async (values) => {
    setLoading(true);
    try {
      if (editingId) {
        
        if (isAdmin) {
          
          await ApiService.updateRoom(editingId, values);
          message.success("Cập nhật thông tin cấu hình phòng thành công!");
        } else {
          
          await ApiService.updateRoomStatus(editingId, values.status);
          message.success("Lễ tân cập nhật trạng thái phòng thành công!");
        }
      } else {
        
        await ApiService.createRoom(values);
        message.success("Thêm phòng mới vào sơ đồ thành công!");
      }
      
      setIsModalOpen(false);
      
      fetchRooms(pagination.current, pagination.pageSize, roomNumber, statusFilter, activeTab, floorFilter, roomTypeIdFilter);
      refreshStats();
    } catch (e) {
      
      
      console.error("Lỗi khi thực thi lưu phòng:", e);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleActive = async (id, currentNum) => {
    setStatusLoadingMap(prev => ({ ...prev, [id]: true }));
    try {
      await ApiService.deleteRoom(id);
      if (activeTab) {
        message.success(`Đã tạm ẩn phòng [${currentNum}] vào kho lưu trữ (thùng rác)`);
      } else {
        message.success(`Đã khôi phục thành công phòng [${currentNum}] về trạng thái hoạt động`);
      }
      await fetchRooms(pagination.current, pagination.pageSize, roomNumber, statusFilter, activeTab, floorFilter, roomTypeIdFilter);
      await refreshStats();
    } catch (e) {
      message.error("Đổi trạng thái hoạt động phòng thất bại!");
    } finally {
      setStatusLoadingMap(prev => ({ ...prev, [id]: false }));
    }
  };

  const handleConfirmCleaned = async (id, currentNum) => {
    try {
      await ApiService.confirmCleaned(id);
      message.success(`Xác nhận phòng [${currentNum}] đã dọn xong, chuyển sang Sẵn sàng đón khách!`);
      fetchRooms(pagination.current, pagination.pageSize, roomNumber, statusFilter, activeTab, floorFilter, roomTypeIdFilter);
      refreshStats();
    } catch (e) {
      message.error("Cập nhật trạng thái dọn dẹp phòng thất bại!");
    }
  };

  const getStatusTag = (status, record) => {
    switch (status) {
      case 'AVAILABLE': return <Tag color="success" style={{fontWeight: 500}}>Trống</Tag>;
      case 'OCCUPIED': return <Tag color="error" style={{fontWeight: 500}}>Đang ở</Tag>;
      case 'CLEANING': 
        return (
          <Space size={4}>
            <Tag color="processing" icon={<SyncOutlined spin />} style={{fontWeight: 500}}>Đang dọn dẹp</Tag>
            <Button size="small" type="dashed" onClick={() => handleConfirmCleaned(record.id, record.roomNumber)}>Xong</Button>
          </Space>
        );
      case 'MAINTENANCE': return <Tag color="default" style={{fontWeight: 500}}>Bảo trì</Tag>;
      default: return <Tag color="default">{status}</Tag>;
    }
  };

  const columns = [
    { title: 'Số phòng', dataIndex: 'roomNumber', key: 'roomNumber', render: (text) => <strong style={{color: '#1677ff'}}>{text}</strong> },
    { title: 'Vị trí tầng', dataIndex: 'floor', key: 'floor', render: (text) => `Tầng ${text}` },
    { title: 'Loại phòng', dataIndex: 'roomType', key: 'roomType' },
    { 
      title: 'Đơn giá niêm yết', 
      dataIndex: 'price', 
      key: 'price', 
      render: (price) => <span style={{color: '#ff4d4f', fontWeight: 600}}>{price?.toLocaleString('vi-VN')} đ/đêm</span> 
    },
    { title: 'Trạng thái phòng', dataIndex: 'status', key: 'status', render: (status, record) => getStatusTag(status, record) },
    {
      title: 'Thao tác',
      key: 'action',
      align: 'center',
      render: (_, record) => (
        <Space size="middle">
          {activeTab ? (
            <>
              {/* Cả Admin và Nhân viên thường đều có quyền Sửa trạng thái thủ công */}
              <Button type="text" icon={<EditOutlined style={{ color: '#1677ff' }} />} onClick={() => handleOpenModal(record)}>Sửa</Button>
              
              {/* Chỉ có Admin mới được quyền Tạm ẩn phòng */}
              {isAdmin && (
                <Popconfirm
                  title={`Bạn muốn tạm ẩn cấu hình hoạt động của phòng ${record.roomNumber} này chứ?`}
                  onConfirm={() => handleToggleActive(record.id, record.roomNumber)}
                  okText="Ẩn đi" cancelText="Hủy" okButtonProps={{ danger: true }}
                >
                  <Button type="text" danger icon={<DeleteOutlined />} loading={statusLoadingMap[record.id]}>Tạm ẩn</Button>
                </Popconfirm>
              )}
            </>
          ) : (
            /* KHI Ở TAB PHÒNG TẠM ẨN: Kiểm tra quyền phân cấp tài khoản */
            isAdmin ? (
              <Popconfirm
                title={`Khôi phục hoạt động cho phòng ${record.roomNumber}?`}
                onConfirm={() => handleToggleActive(record.id, record.roomNumber)}
                okText="Khôi phục" cancelText="Hủy"
              >
                <Button type="text" style={{ color: '#52c41a' }} icon={<UndoOutlined />} loading={statusLoadingMap[record.id]}>Khôi phục</Button>
              </Popconfirm>
            ) : (
              
              <Tag color="default" style={{ borderRadius: '4px', color: '#bfbfbf' }}>Đã ẩn</Tag>
            )
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px', background: '#f5f7fa', minHeight: '100vh', width: 'auto', overflow: 'hidden' }}>
      
      {/* Khối Header Tiêu đề & Nút bấm thêm mới */}
      <Flex justify="space-between" align="center" style={{ marginBottom: '20px' }}>
        <div>
          <Title level={3} style={{ margin: 0 }}><HomeOutlined /> Sơ đồ & Quản lý danh sách phòng</Title>
          <Text type="secondary">Cập nhật trạng thái, thiết lập cấu hình phòng lưu trú khách sạn thời gian thực</Text>
        </div>
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
            type="primary" size="large" icon={<PlusOutlined />} onClick={() => handleOpenModal(null)}>
            Thêm phòng mới
          </Button>
      </Flex>

      {/* Khối 3 Card Thống kê ngang hàng */}
      <Row gutter={16} style={{ marginBottom: '20px' }}>
        <Col xs={24} sm={8}>
          <Card variant="borderless" style={{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.03)' }}>
            <Statistic title="Phòng đang hoạt động" value={activeCount} styles={{ content: { color: '#3f8600', fontWeight: '500' } }} prefix={<CheckCircleOutlined />} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card variant="borderless" style={{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.03)' }}>
            <Statistic title="Phòng ngừng hoạt động" value={deletedCount} styles={{ content: { color: '#cf1322', fontWeight: '500' } }} prefix={<DeleteOutlined />} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card variant="borderless" style={{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.03)' }}>
            <Statistic title="Phòng trống sẵn sàng" value={availableCount} styles={{ content: { color: '#1677ff', fontWeight: '500' } }} prefix={<HomeOutlined />} />
          </Card>
        </Col>
      </Row>

      {/* Khối Bộ lọc tích hợp nằm gọn gàng phía dưới bộ Card */}
      <Card style={{ marginBottom: '20px', borderRadius: '8px' }} styles={{ body: { padding: '16px' } }}>
        <Row gutter={[16, 16]} align="middle" justify="space-between">
          
          {/* Nhóm Filter bên trái */}
          <Col xs={24} xl={18}>
            <Space wrap size="small">
              <Input 
                prefix={<SearchOutlined style={{ color: '#bfbfbf' }} />} 
                placeholder="Tìm nhanh số phòng..." 
                value={searchInputValue}
                onChange={(e) => setSearchInputValue(e.target.value)}
                allowClear
                style={{ width: 160 }}
              />
              
              <Select 
                placeholder="Chọn trạng thái" 
                style={{ width: 140 }} 
                allowClear
                value={statusFilter}
                onChange={(val) => { setStatusFilter(val); setPagination(p => ({ ...p, current: 1 })); }}
              >
                <Select.Option value="AVAILABLE">Trống (Sẵn sàng)</Select.Option>
                <Select.Option value="OCCUPIED">Đang ở</Select.Option>
                <Select.Option value="CLEANING">Đang dọn dẹp</Select.Option>
                <Select.Option value="MAINTENANCE">Bảo trì</Select.Option>
              </Select>

              <Select 
                placeholder="Chọn Tầng" 
                style={{ width: 120 }} 
                allowClear
                value={floorFilter}
                onChange={(val) => { setFloorFilter(val); setPagination(p => ({ ...p, current: 1 })); }}
              >
                {availableFloors.map(f => (
                  <Select.Option key={f} value={f}>Tầng {f}</Select.Option>
                ))}
              </Select>

              <Select 
                placeholder="Chọn loại phòng" 
                style={{ width: 160 }} 
                allowClear
                value={roomTypeIdFilter}
                onChange={(val) => { setRoomTypeIdFilter(val); setPagination(p => ({ ...p, current: 1 })); }}
              >
                {roomTypes.map(t => (
                  <Select.Option key={t.id} value={t.id}>{t.name}</Select.Option>
                ))}
              </Select>

              <Button icon={<UndoOutlined />} onClick={handleResetFilters}>Đặt lại</Button>
            </Space>
          </Col>

          {/* Cụm chuyển đổi trạng thái Tab bên phải */}
          <Col xs={24} xl={6} style={{ textAlign: 'right' }}>
            <Segmented
              options={[
                { label: 'Đang hoạt động', value: true, icon: <CheckCircleOutlined /> },
                { label: 'Ngừng hoạt động', value: false, icon: <DeleteOutlined /> },
              ]}
              value={activeTab}
              onChange={(val) => {
                setActiveTab(val);
                setPagination(p => ({ ...p, current: 1 }));
                refreshStats();
              }}
            />
          </Col>
        </Row>
      </Card>

      {/* Khối Bảng Dữ liệu phòng chống tràn tuyệt đối */}
      <Card style={{ borderRadius: '8px' }} styles={{ body: { padding: '0px' } }}>
        {loading && rooms.length === 0 ? (
          <div style={{ padding: '24px' }}><Skeleton active paragraph={{ rows: 6 }} /></div>
        ) : (
          <Table 
            columns={columns} 
            dataSource={rooms} 
            rowKey="id"
            loading={loading}
            scroll={{ x: 'max-content' }} 
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              showSizeChanger: false,
              onChange: (page) => {
                setPagination(prev => ({ ...prev, current: page }));
              }
            }}
          />
        )}
      </Card>

      {/* Form Modal Nhập liệu Thêm / Sửa đổi phòng */}
      <Modal
        title={editingId ? "Cập nhật thông tin cấu hình phòng" : "Thêm mới phòng vào sơ đồ hệ thống"}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={null}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={handleSave} style={{ marginTop: '16px' }}>
          <Row gutter={16}>
            <Col span={12}>
              {/* KHÓA NẾU KHÔNG PHẢI ADMIN: Nhân viên thường không được sửa số phòng */}
              <Form.Item name="roomNumber" label="Số định danh phòng" rules={[{ required: true, message: 'Vui lòng nhập số phòng!' }]}>
                <Input placeholder="Ví dụ: P101, P205, VIP01..." disabled={!isAdmin && !!editingId} />
              </Form.Item>
            </Col>
            <Col span={12}>
              {/* KHÓA NẾU KHÔNG PHẢI ADMIN: Nhân viên thường không được đổi loại phòng khi cập nhật trạng thái */}
              <Form.Item name="roomTypeId" label="Thuộc phân loại phòng" rules={[{ required: true, message: 'Vui lòng chọn loại cấu trúc phòng!' }]}>
                <Select placeholder="Chọn loại phòng định sẵn" disabled={!isAdmin && !!editingId}>
                  {roomTypes.map(t => (
                    <Select.Option key={t.id} value={t.id}>{t.name} ({(t.price)?.toLocaleString('vi-VN')}đ)</Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              {/* KHÓA NẾU KHÔNG PHẢI ADMIN: Nhân viên thường không được đổi số tầng của phòng */}
              <Form.Item name="floor" label="Vị trí tầng" rules={[{ required: true, message: 'Vui lòng chỉ định số tầng!' }]}>
                <InputNumber min={1} style={{ width: '100%' }} placeholder="Nhập số tầng (1, 2, 3...)" disabled={!isAdmin && !!editingId} />
              </Form.Item>
            </Col>
            {editingId && (
              <Col span={12}>
                {/* ĐÂY LÀ Ô DUY NHẤT NHÂN VIÊN THƯỜNG ĐƯỢC CHỌN VÀ THAY ĐỔI */}
                <Form.Item name="status" label="Cập nhật trạng thái thủ công">
                  <Select>
                    <Select.Option value="AVAILABLE">Trống (Sẵn sàng)</Select.Option>
                    <Select.Option value="CLEANING">Đang dọn dẹp</Select.Option>
                    <Select.Option value="MAINTENANCE">Bảo trì / Sửa chữa</Select.Option>
                    <Select.Option value="OCCUPIED" disabled>Đang có khách ở</Select.Option>
                  </Select>
                </Form.Item>
              </Col>
            )}
          </Row>

          <Form.Item style={{ textAlign: 'right', marginBottom: 0, marginTop: '24px' }}>
            <Space>
              <Button onClick={() => setIsModalOpen(false)}>Hủy bỏ</Button>
              <Button type="primary" htmlType="submit" loading={loading}>Xác nhận lưu</Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      {/* Global CSS Style chỉnh chu gọn gàng */}
      <style>{`
        .ant-table-thead > tr > th {
          background: #f8f9fa !important;
          font-weight: 600 !important;
        }
        .ant-select-selector, .ant-input-affine-wrapper, .ant-input-number, .ant-btn {
          border-radius: 6px !important;
        }
        .ant-segmented {
          border-radius: 8px !important;
        }
      `}</style>
    </div>
  );
};

export default RoomPage;