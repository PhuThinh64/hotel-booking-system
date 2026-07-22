import React, { useEffect, useState, useCallback } from 'react';
import { 
  Table, Button, Space, Input, Card, Modal, Form, InputNumber, 
  message, Popconfirm, Row, Col, Statistic, Image, Upload, 
  Flex, Segmented, Skeleton, Typography, Tag 
} from 'antd';
import { 
  PlusOutlined, TagsOutlined, DollarOutlined, UploadOutlined, 
  SearchOutlined, DeleteOutlined, EditOutlined, UndoOutlined, 
  CheckCircleOutlined, UserOutlined 
} from '@ant-design/icons';
import * as ApiService from '../../api/ApiService';

const { Title, Text } = Typography;
const BASE_URL = 'http://localhost:8080';

const RoomTypePage = () => {
  const [form] = Form.useForm();
  
  
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const isAdmin = user.role === 'ROLE_ADMIN';

  
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [statusLoadingMap, setStatusLoadingMap] = useState({});
  
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [fileList, setFileList] = useState([]);

  
  const [searchInputValue, setSearchInputValue] = useState('');
  const [nameFilter, setNameFilter] = useState('');
  const [activeTab, setActiveTab] = useState(true); 
  const [pagination, setPagination] = useState({ current: 1, pageSize: 5, total: 0 });
  const [sortConfig, setSortConfig] = useState(undefined); 

  
  const [activeCount, setActiveCount] = useState(0);
  const [deletedCount, setDeletedCount] = useState(0);
  const [highestPrice, setHighestPrice] = useState(0);

  
  useEffect(() => {
    const handler = setTimeout(() => {
      setNameFilter(searchInputValue);
      setPagination(prev => ({ ...prev, current: 1 }));
    }, 400);
    return () => clearTimeout(handler);
  }, [searchInputValue]);

  
  const refreshStats = useCallback(async () => {
    try {
      const [resActive, resDeleted, resMaxPrice] = await Promise.all([
        ApiService.getRoomTypes({ active: true, page: 0, size: 1 }).catch(() => ({ totalElements: 0 })),
        ApiService.getRoomTypes({ active: false, page: 0, size: 1 }).catch(() => ({ totalElements: 0 })),
        
        ApiService.getRoomTypes({ active: true, page: 0, size: 1, sort: 'price,desc' }).catch(() => ({ content: [] }))
      ]);

      setActiveCount(resActive?.totalElements || 0);
      setDeletedCount(resDeleted?.totalElements || 0);
      setHighestPrice(resMaxPrice?.content?.[0]?.price || 0);
    } catch (e) {
      console.error("Lỗi cập nhật số liệu thống kê loại phòng:", e);
    }
  }, []);

  
  const fetchRoomTypes = useCallback(async (
    page = 1, 
    pageSize = 5, 
    name = nameFilter, 
    active = activeTab, 
    sort = sortConfig
  ) => {
    setLoading(true);
    try {
      const res = await ApiService.getRoomTypes({
        page: page - 1,
        size: pageSize,
        name: name || undefined,
        active: active,
        sort: sort || undefined
      });

      
      const content = res?.content || res?.data?.content || [];
      const total = res?.totalElements || res?.data?.totalElements || 0;

      setData(content);
      setPagination(prev => ({ ...prev, current: page, total: total }));

      
      if (active) {
        setActiveCount(total);
      } else {
        setDeletedCount(total);
      }
    } catch (err) {
      console.error("Lỗi tải danh sách RoomType:", err);
      message.error("Không thể kết nối máy chủ để tải danh sách loại phòng!");
    } finally {
      setLoading(false);
    }
  }, [nameFilter, activeTab, sortConfig]);

  
  useEffect(() => {
    fetchRoomTypes(pagination.current, pagination.pageSize, nameFilter, activeTab, sortConfig);
  }, [fetchRoomTypes, pagination.current, pagination.pageSize, nameFilter, activeTab, sortConfig]);

  useEffect(() => {
    refreshStats();
  }, [refreshStats]);

  
  const handleTableChange = (newPagination, filters, sorter) => {
    setPagination(prev => ({ ...prev, current: newPagination.current, pageSize: newPagination.pageSize }));
    
    if (sorter && sorter.field && sorter.order) {
      const dir = sorter.order === 'ascend' ? 'asc' : 'desc';
      setSortConfig(`${sorter.field},${dir}`);
    } else {
      setSortConfig(undefined);
    }
  };

  
  const openModal = (record = null) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue(record);
      const rawImage = record.imageUrl;
      const previewUrl = rawImage
        ? (rawImage.startsWith('http') ? rawImage : `${BASE_URL}${rawImage.startsWith('/') ? '' : '/'}${rawImage}`)
        : null;
      setFileList(previewUrl ? [{
        uid: '-1',
        name: previewUrl.split('/').pop() || 'image.jpg',
        status: 'done',
        url: previewUrl,
        response: { result: rawImage },
      }] : []);
    } else {
      setEditingId(null);
      form.resetFields();
      setFileList([]);
    }
    setIsModalOpen(true);
  };

  
  const handleSave = async (values) => {
    try {
      if (editingId) {
        await ApiService.updateRoomType(editingId, values);
        message.success("Cập nhật thông tin loại phòng thành công!");
      } else {
        await ApiService.createRoomType(values);
        message.success("Khởi tạo loại phòng mới thành công!");
      }
      setIsModalOpen(false);
      fetchRoomTypes(pagination.current, pagination.pageSize, nameFilter, activeTab, sortConfig);
      refreshStats();
    } catch (err) {
      const errorMsg = err.response?.data?.message || "Thao tác xử lý dữ liệu thất bại!";
      message.error(errorMsg);
    }
  };

  
  const handleToggleActive = async (id, currentName) => {
    setStatusLoadingMap(prev => ({ ...prev, [id]: true }));
    try {
      await ApiService.deleteRoomType(id);
      if (activeTab) {
        message.success(`Đã tạm ẩn loại phòng [${currentName}] vào thùng rác thành công`);
      } else {
        message.success(`Đã khôi phục trạng thái hoạt động bình thường cho loại phòng [${currentName}]`);
      }
      await fetchRoomTypes(pagination.current, pagination.pageSize, nameFilter, activeTab, sortConfig);
      await refreshStats();
    } catch (err) {
      message.error(err.response?.data?.message || "Thay đổi trạng thái cấu hình thất bại!");
    } finally {
      setStatusLoadingMap(prev => ({ ...prev, [id]: false }));
    }
  };

  
  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80, align: 'center' },
    { 
      title: 'Tên loại phòng', 
      dataIndex: 'name', 
      key: 'name', 
      render: (text) => <strong style={{ color: '#1677ff' }}>{text}</strong> 
    },
    { 
      title: 'Giá niêm yết (Một đêm)', 
      dataIndex: 'price', 
      key: 'price',
      sorter: true, 
      render: (price) => (
        <span style={{ color: '#ff4d4f', fontWeight: 600 }}>
          {Number(price).toLocaleString('vi-VN')} đ/đêm
        </span>
      )
    },
    { 
      title: 'Sức chứa tối đa', 
      dataIndex: 'maxGuest', 
      key: 'maxGuest',
      align: 'center',
      sorter: true,
      render: (count) => (
        <Tag color="cyan" icon={<UserOutlined />} style={{ padding: '2px 8px', borderRadius: '4px', fontWeight: 500 }}>
          {count} khách
        </Tag>
      )
    },
    {
      title: 'Hình ảnh không gian',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      align: 'center',
      width: 160,
      render: (url) => {
        const imageSrc = url ? (url.startsWith('http') ? url : `${BASE_URL}${url.startsWith('/') ? '' : '/'}${url}`) : null;
        return imageSrc ? (
          <Image 
            src={imageSrc} 
            width={120} 
            height={75} 
            style={{ objectFit: 'cover', borderRadius: 6, boxShadow: '0 2px 4px rgba(0,0,0,0.06)' }} 
            alt="Room Type Layout" 
          />
        ) : <Text type="secondary">Chưa cập nhật ảnh</Text>;
      },
    },
    {
      title: 'Tiện ích tóm tắt',
      dataIndex: 'description',
      key: 'description',
      width: 220, 
      render: (text) => (
        <Text
          
          ellipsis={{ 
            tooltip: { 
              title: text, 
              placement: 'topLeft',
              overlayStyle: { maxWidth: '300px' } 
            } 
          }}
          style={{ maxWidth: 200, cursor: 'pointer' }}
        >
          {text || <span style={{ color: '#bfbfbf', fontStyle: 'italic' }}>Chưa có mô tả</span>}
        </Text>
      )
    },
    
    ...(isAdmin ? [{
      title: 'Thao tác',
      key: 'action',
      align: 'center',
      width: 180,
      render: (_, record) => (
        <Space size="middle">
          {activeTab ? (
            <>
              <Button type="text" icon={<EditOutlined style={{ color: '#1677ff' }} />} onClick={() => openModal(record)}>Sửa</Button>
              <Popconfirm
                title={`Bạn chắc chắn muốn tạm ẩn cấu hình loại phòng [${record.name}] này chứ?`}
                onConfirm={() => handleToggleActive(record.id, record.name)}
                okText="Ẩn đi" cancelText="Hủy bỏ" okButtonProps={{ danger: true }}
              >
                <Button type="text" danger icon={<DeleteOutlined />} loading={statusLoadingMap[record.id]}>Tạm ẩn</Button>
              </Popconfirm>
            </>
          ) : (
            <Popconfirm
              title={`Khôi phục hoạt động cho loại phòng [${record.name}]?`}
              onConfirm={() => handleToggleActive(record.id, record.name)}
              okText="Khôi phục" cancelText="Hủy bỏ"
            >
              <Button type="text" style={{ color: '#52c41a' }} icon={<UndoOutlined />} loading={statusLoadingMap[record.id]}>Khôi phục</Button>
            </Popconfirm>
          )}
        </Space>
      ),
    }] : [])
  ];

  return (
    <div style={{ padding: '24px', background: '#f5f7fa', minHeight: '100vh', width: 'auto', overflow: 'hidden' }}>
      
      {/* Khối Tiêu Đề Điều Hướng */}
      <Flex justify="space-between" align="center" style={{ marginBottom: '20px' }}>
        <div>
          <Title level={3} style={{ margin: 0 }}><TagsOutlined /> Định hình Cấu trúc & Loại phòng</Title>
          <Text type="secondary">Thiết lập quy chuẩn định mức giá, quy định số lượng khách cư trú áp dụng toàn hệ thống</Text>
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
            type="primary" size="large" icon={<PlusOutlined />} onClick={() => openModal(null)}>Thêm loại phòng mới</Button>
      </Flex>

      {/* Khối Thẻ Thống Kê Nâng Cao - Realtime 3 Cột */}
      <Row gutter={16} style={{ marginBottom: '20px' }}>
        <Col xs={24} sm={8}>
          <Card variant="borderless" style={{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.03)' }}>
            <Statistic title="Loại phòng hoạt động" value={activeCount} styles={{ content: { color: '#3f8600' } }} prefix={<CheckCircleOutlined />} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card variant="borderless" style={{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.03)' }}>
            <Statistic title="Phòng đã ngừng hoạt động" value={deletedCount} styles={{ content: { color: '#cf1322' } }} prefix={<DeleteOutlined />} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card variant="borderless" style={{ borderRadius: '8px', boxShadow: '0 2px 8px rgba(0,0,0,0.03)' }}>
            <Statistic 
              title="Phòng có phí cao nhất (còn hoạt động)" 
              value={highestPrice} 
              styles={{ content: { color: '#fa8c16', fontWeight: 'bold' } }} 
              prefix={<DollarOutlined />} 
              suffix="đ"
            />
          </Card>
        </Col>
      </Row>

      {/* Thanh Công Cụ Bộ Lọc Tích Hợp Segmented Tab */}
      <Card style={{ marginBottom: '20px', borderRadius: '8px' }} styles={{ body: { padding: '16px' } }}>
        <Row gutter={[16, 16]} align="middle" justify="space-between">
          <Col xs={24} sm={12} md={8}>
            <Input 
              prefix={<SearchOutlined style={{ color: '#bfbfbf' }} />} 
              placeholder="Tìm nhanh theo tên định danh loại phòng..." 
              value={searchInputValue}
              onChange={(e) => setSearchInputValue(e.target.value)}
              allowClear
            />
          </Col>
          <Col xs={24} sm={12} md={6} style={{ textAlign: 'right' }}>
            <Segmented
              options={[
                { label: 'Đang hoạt động', value: true, icon: <CheckCircleOutlined /> },
                { label: 'Ngừng hoạt động', value: false, icon: <DeleteOutlined /> },
              ]}
              value={activeTab}
              onChange={(val) => {
                setActiveTab(val);
                setPagination(p => ({ ...p, current: 1 }));
                setSortConfig(undefined); 
                refreshStats();
              }}
            />
          </Col>
        </Row>
      </Card>

      {/* Khối Bảng Dữ Liệu Phân Trang */}
      <Card style={{ borderRadius: '8px' }} styles={{ body: { padding: '0px' } }}>
        {loading && data.length === 0 ? (
          <div style={{ padding: '24px' }}><Skeleton active paragraph={{ rows: 6 }} /></div>
        ) : (
          <Table 
            columns={columns} 
            dataSource={data} 
            rowKey="id" 
            loading={loading}
            onChange={handleTableChange}
            scroll={{ x: 'max-content' }}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              showSizeChanger: false,
            }}
          />
        )}
      </Card>

      {/* Form Modal Nhập Liệu Thêm/Sửa */}
      <Modal 
        title={editingId ? "Cập nhật thông tin loại phòng" : "Khởi tạo cấu trúc loại phòng mới"} 
        open={isModalOpen} 
        onCancel={() => setIsModalOpen(false)} 
        onOk={() => form.submit()}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={handleSave} style={{ marginTop: '16px' }}>
          <Form.Item name="name" label="Tên loại phòng" rules={[
            { required: true, message: 'Vui lòng nhập tên phân loại phòng!' },
            { min: 3, message: 'Độ dài ký tự tối thiểu của loại phòng là 3!' }
          ]}>
            <Input placeholder="Ví dụ: Phòng Đơn Standard, Phòng Cặp Đôi Deluxe, Suite VIP..." />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="price" label="Giá mỗi đêm (VNĐ)" rules={[
                { required: true, message: 'Vui lòng định mức giá phòng!' }
              ]}>
                <InputNumber 
                  min={0} 
                  style={{ width: '100%' }} 
                  placeholder="Nhập số tiền..."
                  formatter={v => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} 
                  parser={v => v.replace(/\$\s?|(,*)/g, '')}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="maxGuest" label="Giới hạn số khách lưu trú" rules={[
                { required: true, message: 'Vui lòng giới hạn khách cư trú!' }
              ]}>
                <InputNumber min={1} max={20} style={{ width: '100%' }} placeholder="Ví dụ: 2, 4..." />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label="Hình ảnh minh họa không gian phòng">
            <Form.Item name="imageUrl" noStyle rules={[{ required: true, message: 'Vui lòng tải lên hình ảnh đại diện!' }]}>
              <Input type="hidden" />
            </Form.Item>
            <Upload
              listType="picture-card"
              fileList={fileList}
              action={`${BASE_URL}/api/v1/roomtype/upload`}
              headers={{ Authorization: `Bearer ${localStorage.getItem('token')}` }}
              maxCount={1}
              onChange={({ file, fileList: newFileList }) => {
                setFileList(newFileList);
                if (file.status === 'done') {
                  const returnedPath = file.response?.result || file.response?.data?.result || '';
                  form.setFieldsValue({ imageUrl: returnedPath });
                  message.success('Upload và đồng bộ ảnh lên hệ thống lưu trữ thành công!');
                } else if (file.status === 'error') {
                  message.error('Quá trình tải ảnh lên thất bại, kiểm tra lại kết nối mạng!');
                }
              }}
              onRemove={() => {
                setFileList([]);
                form.setFieldsValue({ imageUrl: '' });
              }}
            >
              {fileList.length >= 1 ? null : (
                <div>
                  <UploadOutlined />
                  <div style={{ marginTop: 8 }}>Tải ảnh lên</div>
                </div>
              )}
            </Upload>
          </Form.Item>

          <Form.Item name="description" label="Mô tả chi tiết trang thiết bị / tiện ích">
            <Input.TextArea placeholder="Thông số diện tích m², trang bị bồn tắm, hướng view ban công..." rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Global CSS Inject đảm bảo bo góc mượt mà tinh tế */}
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

export default RoomTypePage;