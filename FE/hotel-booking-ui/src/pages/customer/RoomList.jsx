import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs'; 

import {
  Tabs,
  Card,
  Col,
  Row,
  Tag,
  Button,
  DatePicker,
  Space,
  message,
  Empty,
  Spin,
  Modal,
  Form,
  Input,
  InputNumber,
  Typography,
  Checkbox,
  Divider,
  Badge
} from 'antd';

import {
  InfoCircleOutlined,
  CalendarOutlined,
  AuditOutlined,
  UserOutlined,
  FileTextOutlined,
  ShoppingCartOutlined,
  PlusOutlined,
  MinusOutlined,
  DeleteOutlined,
  FireOutlined,
  SearchOutlined,
  HomeOutlined,
  CreditCardOutlined
} from '@ant-design/icons';

import * as ApiService from '../../api/ApiService';

const { RangePicker } = DatePicker;
const { Title, Text } = Typography;

const BASE_URL = "http://localhost:8080";

const RoomList = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [roomTypes, setRoomTypes] = useState([]);
  const [dates, setDates] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);
  const [selectedRooms, setSelectedRooms] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [extraServices, setExtraServices] = useState([]);
  const [isBookingForOthers, setIsBookingForOthers] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState('VNPAY');
  const [publicServices, setPublicServices] = useState([]);

  const [currentUser] = useState(() => {
    try {
      const storedData = JSON.parse(localStorage.getItem('user'));
      return storedData?.user || storedData || null;
    } catch {
      return null;
    }
  });

  const isAdmin =
    currentUser?.role === "ROLE_ADMIN" ||
    currentUser?.role === "ROLE_RECEPTIONIST";

  const isReceptionBooking = isAdmin;

  const disabledPastDates = (current) => {
    return current && current < dayjs().startOf('day');
  };

  useEffect(() => {
    fetchRoomData();
    fetchServices();
  }, []);

  useEffect(() => {
    if (isModalOpen && currentUser && !isAdmin) {
      form.setFieldsValue({
        fullName: currentUser.fullName || undefined,
        phoneNumber: currentUser.phoneNumber || undefined,
      });
    }
  }, [isModalOpen, currentUser, isAdmin, form]);

  const fetchRoomData = async () => {
    setLoading(true);
    try {
      const res = await ApiService.getRoomTypes(0, 100);
      console.log("Dữ liệu thực tế getRoomTypes:", res);
      const roomData = res?.result?.content || res?.content || res?.result || [];
      setRoomTypes(roomData);
    } catch (err) {
      console.log(err);
      message.error("Không thể tải danh sách phòng");
    } finally {
      setLoading(false);
    }
  };

  const fetchServices = async () => {
    try {
      setLoading(true);
      const res = await ApiService.getPublicServices();
      const data = res?.content || [];
      setPublicServices(data);
    } catch (err) {
      console.error("Lỗi:", err);
      message.error("Không thể tải dịch vụ");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!dates || dates.length !== 2) {
      message.warning("Vui lòng chọn ngày nhận và trả phòng");
      return;
    }

    setLoading(true);
    try {
      const checkIn = dates[0]
        .startOf('day')
        .add(14, 'hour')
        .format('YYYY-MM-DDTHH:mm:ss');

      const checkOut = dates[1]
        .startOf('day')
        .add(12, 'hour')
        .format('YYYY-MM-DDTHH:mm:ss');

      const res = await ApiService.getAvailableRoomTypesWithCount(checkIn, checkOut);
      const availableData = res?.result || res?.data?.result || res || [];
      
      setRoomTypes(availableData);
      setHasSearched(true);
      setSelectedRooms([]);
    } catch (err) {
      console.log(err);
      message.error("Không thể tìm phòng trống");
    } finally {
      setLoading(false);
    }
  };

  const handlePhoneChange = async (e, targetField = 'fullName') => {
    const phone = e.target.value;

    if (phone.length < 10) {
      return; 
    }

    if (phone.length === 10) {
      try {
        const response = await ApiService.getCustomerByPhone(phone);
        const customer =
          response?.result ||
          response?.data?.result ||
          response?.data ||
          response;

        if (customer?.fullName) {
          form.setFieldsValue({
            [targetField]: customer.fullName,
          });
          message.success("Đã tự động điền thông tin khách quen");
        }
      } catch (err) {
        const status = err?.response?.status || err?.status;
        if (status === 404 || status === 400) {
          form.setFieldsValue({ [targetField]: undefined });
          message.info("Khách hàng mới. Vui lòng nhập họ tên.");
        } else {
          console.error("Lỗi tra cứu khách hàng:", err);
        }
      }
    }
  };

  const handleAddRoom = (roomType) => {
    if (!dates || dates.length !== 2) {
      message.warning("Vui lòng chọn ngày trước");
      return;
    }

    const targetId = roomType.roomTypeId || roomType.id;
    const existed = selectedRooms.find(r => (r.roomTypeId || r.id) === targetId);
    const currentQuantity = existed?.quantity || 0;
    const available = roomType.availableCount || 0;

    if (currentQuantity >= available) {
      message.warning(`Chỉ còn ${available} phòng ${roomType.name}`);
      return;
    }

    setSelectedRooms(prev => {
      const existedRoom = prev.find(r => (r.roomTypeId || r.id) === targetId);
      if (existedRoom) {
        return prev.map(r =>
          (r.roomTypeId || r.id) === targetId
            ? { ...r, quantity: r.quantity + 1 }
            : r
        );
      }
      return [...prev, { ...roomType, id: targetId, roomTypeId: targetId, quantity: 1 }];
    });

    message.success(`Đã thêm ${roomType.name}`);
  };

  const handleDecreaseRoom = (roomId) => {
    setSelectedRooms(prev =>
      prev
        .map(room =>
          room.id === roomId
            ? { ...room, quantity: room.quantity - 1 }
            : room
        )
        .filter(room => room.quantity > 0)
    );
  };

  const handleRemoveRoom = (roomId) => {
    setSelectedRooms(prev => prev.filter(r => r.id !== roomId));
  };

  const getNights = () => {
    if (!dates || dates.length !== 2) return 1;
    const start = dates[0].startOf('day');
    const end = dates[1].startOf('day');
    const nights = end.diff(start, 'day');
    return nights > 0 ? nights : 1;
  };

  const calculateRoomTotal = () => {
    const nights = getNights();
    return selectedRooms.reduce(
      (sum, room) => sum + room.price * room.quantity * nights,
      0
    );
  };

  const handleConfirmBooking = async (values) => {
    if (selectedRooms.length === 0) {
      message.warning("Vui lòng chọn ít nhất 1 phòng");
      return;
    }

    try {
      setLoading(true);
      const selectedServices = (values.services || []).map(s => ({
        serviceId: Number(s.serviceId),
        quantity: Number(s.quantity)
      }));

      const formattedCheckIn = values.dates[0].startOf('day').hour(14).minute(0).second(0).format("YYYY-MM-DDTHH:mm:ss");
      const formattedCheckOut = values.dates[1].startOf('day').hour(12).minute(0).second(0).format("YYYY-MM-DDTHH:mm:ss");
      const targetPhone = values.isOther ? values.contactPhone : values.phoneNumber;

      const payload = {
        customerId: currentUser?.id ? Number(currentUser.id) : null,
        fullName: values.fullName,
        phoneNumber: values.phoneNumber,
        roomTypes: selectedRooms.map((room) => ({
          roomTypeId: Number(room.roomTypeId || room.id),
          quantity: Number(room.quantity),
        })),
        services: selectedServices,
        checkIn: formattedCheckIn,
        checkOut: formattedCheckOut,
        contactName: values.isOther ? values.contactName : values.fullName,
        contactPhone: targetPhone, 
        depositAmount: 0,
        paymentMethod: paymentMethod, 
      };

      const res = await ApiService.createBooking(payload);
      const result = res?.result || res;

      message.success("Đặt phòng thành công!");
      setSelectedRooms([]);
      setIsModalOpen(false);
      form.resetFields();

      if (paymentMethod?.toUpperCase() === "VNPAY" && result?.paymentUrl) {
        window.location.href = result.paymentUrl;
        return;
      }

      if (result?.bookingCode) {
        navigate(`/my-bookings?phone=${targetPhone}&code=${result.bookingCode}`);
      } else {
        navigate("/my-bookings");
      }
    } catch (error) {
      console.error(error);
      message.error(error?.response?.data?.message || "Đặt phòng thất bại");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ background: '#f4f7fa', minHeight: '100vh', fontFamily: 'Inter, system-ui, sans-serif' }}>
      {/* FRESH & LIGHT HERO BANNER */}
      <div
        style={{
          padding: '60px 20px 140px',
          background: 'linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)',
          textAlign: 'center',
          position: 'relative'
        }}
      >
        <Title level={1} style={{ color: '#ffffff', marginBottom: 10, fontWeight: 800, letterSpacing: '-0.5px' }}>
          LOTUS HOTEL 
        </Title>
        <Text style={{ color: '#e0f2fe', fontSize: 16, fontWeight: 400 }}>
          Trải nghiệm kỳ nghỉ dưỡng đẳng cấp quốc tế cùng Lotus Hotel
        </Text>

        {/* FLOATING CONSOLE BOX */}
        <Card
          style={{
            maxWidth: 1000,
            margin: '30px auto 0',
            borderRadius: 16,
            boxShadow: '0 10px 25px rgba(0, 0, 0, 0.08)',
            background: '#ffffff',
            position: 'absolute',
            left: 20,
            right: 20,
            bottom: -65,
            zIndex: 10
          }}
          bodyStyle={{ padding: '20px 30px' }}
        >
          <Tabs
            centered
            items={[
              {
                key: '1',
                label: (
                  <span style={{ fontSize: 15, fontWeight: 600 }}>
                    <CalendarOutlined /> Chọn ngày đặt phòng
                  </span>
                ),
                children: (
                  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 16, marginTop: 10, flexWrap: 'wrap' }}>
                    <RangePicker
                      size="large"
                      format="DD/MM/YYYY"
                      onChange={setDates}
                      disabledDate={disabledPastDates}
                      style={{ width: 360, borderRadius: 8, height: 46 }}
                    />
                    <Button
                      type="primary"
                      size="large"
                      loading={loading}
                      onClick={handleSearch}
                      icon={<SearchOutlined />}
                      style={{ borderRadius: 8, fontWeight: 600, height: 46, padding: '0 28px', background: '#2563eb', border: 'none' }}
                    >
                      Kiểm tra phòng trống
                    </Button>
                  </div>
                )
              },
              {
                key: '2',
                label: (
                  <span style={{ fontSize: 15, fontWeight: 600 }}>
                    <AuditOutlined /> Tra cứu mã hồ sơ (Booking)
                  </span>
                ),
                children: (
                  <Form
                    layout="inline"
                    onFinish={(values) => {
                      navigate(`/my-bookings?phone=${values.phoneNumber}&code=${values.code}`);
                    }}
                    style={{ justifyContent: 'center', gap: 12, marginTop: 10 }}
                  >
                    <Form.Item 
                      name="phoneNumber"
                      rules={[
                        { required: true, message: 'Vui lòng nhập số điện thoại' },
                        { pattern: /^0[0-9]{9}$/, message: 'SĐT phải gồm 10 chữ số!' }
                      ]}
                    >
                      <Input
                        size="large"
                        prefix={<UserOutlined style={{ color: '#94a3b8' }} />}
                        placeholder="Số điện thoại liên hệ"
                        style={{ width: 240, borderRadius: 8, height: 46 }}
                      />
                    </Form.Item>

                    <Form.Item 
                      name="code"
                      rules={[{ required: true, message: 'Vui lòng nhập mã đơn' }]}
                    >
                      <Input
                        size="large"
                        prefix={<FileTextOutlined style={{ color: '#94a3b8' }} />}
                        placeholder="Mã code đơn hàng"
                        style={{ width: 220, borderRadius: 8, height: 46 }}
                      />
                    </Form.Item>

                    <Button 
                      type="primary" 
                      size="large" 
                      htmlType="submit"
                      style={{ borderRadius: 8, fontWeight: 600, height: 46, padding: '0 24px', background: '#1e3a8a', border: 'none' }}
                    >
                      Tìm kiếm đơn đặt
                    </Button>
                  </Form>
                )
              }
            ]}
          />
        </Card>
      </div>

      {/* COMPONENT BODY / ROOM LIST */}
      <div style={{ padding: '120px 6% 60px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 30 }}>
          <div>
            <Title level={3} style={{ fontWeight: 700, color: '#1e3a8a', margin: 0 }}>
              Danh Sách Hạng Phòng Khả Dụng
            </Title>
            <Text type="secondary" style={{ color: '#64748b', fontSize: 14 }}>
              Vui lòng chọn hạng phòng và số lượng phù hợp với nhu cầu lưu trú của bạn
            </Text>
          </div>

          {selectedRooms.length > 0 && (
            <Badge count={selectedRooms.reduce((sum, r) => sum + r.quantity, 0)} offset={[-4, 4]}>
              <Button
                type="primary"
                size="large"
                icon={<ShoppingCartOutlined />}
                style={{ borderRadius: 8, fontWeight: 600, background: '#2563eb', height: 42 }}
                onClick={() => {
                  setIsModalOpen(true);
                  form.setFieldsValue({ dates });
                }}
              >
                Xem giỏ phòng chọn
              </Button>
            </Badge>
          )}
        </div>

        <Spin spinning={loading} size="large">
          <Row gutter={[24, 24]}>
            {roomTypes.map(type => {
              const selected = selectedRooms.find(r => r.id === type.id);
              return (
                <Col xs={24} md={12} lg={8} key={type.id}>
                  <Card
                    hoverable
                    style={{
                      borderRadius: 12,
                      overflow: 'hidden',
                      border: '1px solid #e2e8f0',
                      boxShadow: '0 4px 12px rgba(0,0,0,0.03)',
                      background: '#ffffff'
                    }}
                    bodyStyle={{ padding: 20 }}
                    cover={
                      <div style={{ height: 240, overflow: 'hidden', position: 'relative' }}>
                        <img
                          alt={type.name}
                          src={
                            type.imageUrl
                              ? `${BASE_URL}${type.imageUrl}`
                              : 'https://vapa.vn/wp-content/uploads/2022/12/anh-khach-san-dep.jpg'
                          }
                          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                      </div>
                    }
                  >
                    <Space direction="vertical" style={{ width: '100%' }} size={12}>
                      <div>
                        <Title level={4} style={{ marginBottom: 4, fontWeight: 700, color: '#1e293b' }}>
                          {type.name}
                        </Title>
                        <Tag color="blue" style={{ borderRadius: 4, border: 'none', fontWeight: 500 }}>
                          <UserOutlined /> Tối đa: {type.maxGuest} khách
                        </Tag>
                      </div>

                      <Text type="secondary" style={{ minHeight: 40, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden', color: '#64748b', fontSize: 13 }}>
                        {type.description}
                      </Text>

                      {/* 🌟 THAY ĐỔI: HIỂN THỊ SỐ PHÒNG CÒN LẠI TRONG CARD */}
                      <div 
                        style={{ 
                          background: hasSearched && type.availableCount <= 0 ? '#fef2f2' : '#f0fdf4', 
                          padding: '8px 12px', 
                          borderRadius: 6, 
                          border: hasSearched && type.availableCount <= 0 ? '1px solid #fca5a5' : '1px solid #bbf7d0',
                          display: 'flex',
                          alignItems: 'center',
                          gap: 6
                        }}
                      >
                        <HomeOutlined style={{ color: hasSearched && type.availableCount <= 0 ? '#ef4444' : '#16a34a' }} />
                        <Text strong style={{ fontSize: 13, color: hasSearched && type.availableCount <= 0 ? '#dc2626' : '#15803d' }}>
                          {hasSearched ? `Số phòng còn trống: ${type.availableCount} phòng` : "Trạng thái: Sẵn sàng phục vụ"}
                        </Text>
                      </div>

                      <Divider style={{ margin: '4px 0', borderColor: '#f1f5f9' }} />

                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                          <Text type="secondary" style={{ fontSize: 11, color: '#94a3b8', textTransform: 'uppercase', fontWeight: 600 }}>Giá phòng/Đêm</Text>
                          <br />
                          <Text style={{ fontSize: 22, fontWeight: 800, color: '#ef4444' }}>
                            {type.price?.toLocaleString()}đ
                          </Text>
                        </div>

                        {/* 🌟 THAY ĐỔI: ĐỔI MÀU NÚT THÊM PHÒNG SANG MÀU XANH NGỌC TEAL SÁNG SỦA */}
                        {!selected ? (
                          <Button
                            type="primary"
                            size="large"
                            disabled={hasSearched && type.availableCount <= 0}
                            icon={<PlusOutlined />}
                            onClick={() => handleAddRoom(type)}
                            style={{ 
                              height: 40, 
                              borderRadius: 6, 
                              fontWeight: 600, 
                              background: hasSearched && type.availableCount <= 0 ? '#cbd5e1' : '#0d9488', 
                              border: 'none', 
                              padding: '0 16px' 
                            }}
                          >
                            Chọn phòng
                          </Button>
                        ) : (
                          <Space style={{ background: '#f8fafc', padding: '4px 8px', borderRadius: 6, border: '1px solid #cbd5e1' }}>
                            <Button
                              shape="circle"
                              size="small"
                              icon={<MinusOutlined />}
                              onClick={() => handleDecreaseRoom(type.id)}
                              style={{ border: 'none', background: '#ffffff' }}
                            />
                            <Text strong style={{ fontSize: 15, minWidth: 20, textAlign: 'center', display: 'inline-block' }}>
                              {selected.quantity}
                            </Text>
                            <Button
                              shape="circle"
                              size="small"
                              type="primary"
                              icon={<PlusOutlined />}
                              onClick={() => handleAddRoom(type)}
                              style={{ border: 'none', background: '#0d9488' }}
                            />
                          </Space>
                        )}
                      </div>
                    </Space>
                  </Card>
                </Col>
              );
            })}
          </Row>

          {roomTypes.length === 0 && !loading && (
            <Empty description="Không tìm thấy cấu hình phòng thích hợp trong khoảng thời gian lựa chọn" style={{ padding: '60px 0' }} />
          )}
        </Spin>
      </div>

      {/* FLOATING SELECTED PANELS */}
      {selectedRooms.length > 0 && (
        <div
          style={{
            position: 'fixed',
            right: 24,
            bottom: 24,
            width: 380,
            background: '#ffffff',
            borderRadius: 12,
            overflow: 'hidden',
            boxShadow: '0 20px 40px rgba(0, 0, 0, 0.15)',
            zIndex: 1000,
            border: '1px solid #e2e8f0'
          }}
        >
          <div style={{ padding: '16px 20px', background: '#1e3a8a', color: '#ffffff' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Text strong style={{ color: '#ffffff', fontSize: 16 }}>
                <ShoppingCartOutlined /> Phòng đã lựa chọn
              </Text>
              <Tag color="cyan" style={{ fontWeight: 700, border: 'none' }}>
                {selectedRooms.reduce((sum, r) => sum + r.quantity, 0)} Phòng
              </Tag>
            </div>
          </div>

          <div style={{ maxHeight: 220, overflowY: 'auto', padding: '16px 20px', background: '#f8fafc' }}>
            <Space direction="vertical" style={{ width: '100%' }} size={10}>
              {selectedRooms.map(room => (
                <div key={room.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#fff', padding: 10, borderRadius: 6, border: '1px solid #edf2f7' }}>
                  <div style={{ flex: 1 }}>
                    <Text strong style={{ color: '#334155', fontSize: 13.5 }}>{room.name}</Text>
                    <br />
                    <Text style={{ color: '#ef4444', fontWeight: 600, fontSize: 13 }}>{room.price?.toLocaleString()}đ</Text>
                  </div>
                  <Space style={{ background: '#f1f5f9', padding: '2px 4px', borderRadius: 4 }}>
                    <Button size="small" type="text" onClick={() => handleDecreaseRoom(room.id)}>-</Button>
                    <Text strong>{room.quantity}</Text>
                    <Button size="small" type="text" onClick={() => handleAddRoom(room)}>+</Button>
                  </Space>
                </div>
              ))}
            </Space>
          </div>

          <div style={{ padding: '16px 20px', borderTop: '1px solid #e2e8f0', background: '#ffffff' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <Text style={{ color: '#64748b', fontSize: 13 }}>Tạm tính:</Text>
              <Text style={{ fontSize: 20, fontWeight: 800, color: '#ef4444' }}>
                {calculateRoomTotal().toLocaleString()}đ
              </Text>
            </div>
            {/* 🌟 THAY ĐỔI: ĐỔI MÀU NÚT LẬP HỒ SƠ SANG MÀU XANH NGỌC TEAL ĐỒNG BỘ */}
            <Button
              type="primary"
              size="large"
              block
              icon={<FireOutlined />}
              style={{ height: 44, borderRadius: 6, fontWeight: 700, background: '#0d9488', border: 'none' }}
              onClick={() => {
                setIsModalOpen(true);
                form.setFieldsValue({ dates });
              }}
            >
              Tiếp tục lập hồ sơ đặt
            </Button>
          </div>
        </div>
      )}

      {/* 🌟 THAY ĐỔI: THIẾT KẾ TOÀN BỘ UI CỦA MODAL THEO PHONG CÁCH HIỆN ĐẠI, BỐ CỤC SÁNG SỦA KHÔNG GÂY TỐI TĂM */}
      <Modal
        open={isModalOpen}
        onCancel={() => {
          setIsModalOpen(false);
          form.resetFields();
        }}
        footer={[
          <Button
            key="close"
            style={{ borderRadius: 6, fontWeight: 500, height: 40 }}
            onClick={() => {
              setIsModalOpen(false);
              form.resetFields();
            }}
          >
            Hủy bỏ
          </Button>,
          isAdmin && (
            <Button
              key="cash"
              style={{
                backgroundColor: '#f97316',
                borderColor: '#f97316',
                color: '#fff',
                borderRadius: 6,
                fontWeight: 600,
                height: 40
              }}
              onClick={() => {
                setPaymentMethod('CASH');
                form.submit();
              }}
            >
              Thu tiền mặt (Lễ tân)
            </Button>
          ),
          <Button
            key="vnpay"
            type="primary"
            style={{ borderRadius: 6, fontWeight: 600, height: 40, background: '#2563eb', border: 'none' }}
            onClick={() => {
              setPaymentMethod('VNPAY');
              form.submit();
            }}
          >
            Thanh toán qua VNPay
          </Button>
        ]}
        width={750}
        centered
        confirmLoading={loading}
        bodyStyle={{ padding: '0px 10px 15px' }}
      >
        {/* HEADER MODAL */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '20px 0', borderBottom: '2px solid #3b82f6', marginBottom: 20 }}>
          <div style={{ background: '#eff6ff', padding: '8px 12px', borderRadius: 8 }}>
            <FileTextOutlined style={{ fontSize: 22, color: '#2563eb' }} />
          </div>
          <div>
            <Title level={4} style={{ margin: 0, fontWeight: 800, color: '#1e3a8a' }}>
              PHIẾU XÁC NHẬN THÔNG TIN ĐẶT PHÒNG
            </Title>
            <Text type="secondary" style={{ fontSize: 13 }}>Hệ thống tổng hợp chi tiết phòng, thông tin khách cư trú và hóa đơn</Text>
          </div>
        </div>

        <Form 
          form={form} 
          layout="vertical"  
          onFinish={handleConfirmBooking}
          onFinishFailed={() => {
            message.error("Vui lòng điền đầy đủ thông tin bắt buộc!");
          }}
        >
          {/* SECTION 1: SELECTED ROOMS SUMMARY */}
          <div style={{ marginBottom: 20 }}>
            <div style={{ padding: '6px 12px', background: '#f1f5f9', borderRadius: 6, marginBottom: 12 }}>
              <Text strong style={{ color: '#334155', fontSize: 13.5 }}>1. Danh sách hạng phòng cư trú</Text>
            </div>
            <div style={{ border: '1px solid #e2e8f0', borderRadius: 8, padding: '12px 16px', background: '#ffffff' }}>
              {selectedRooms.map((room, idx) => {
                const nights = getNights();
                const itemTotal = room.price * room.quantity * nights;
                return (
                  <div key={room.id}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div>
                        <Text strong style={{ fontSize: 14, color: '#1e293b' }}>{room.name}</Text>
                        <br />
                        <Text type="secondary" style={{ fontSize: 12.5 }}>
                          Số lượng: <strong style={{ color: '#1e3a8a' }}>{room.quantity}</strong> phòng × <strong style={{ color: '#1e3a8a' }}>{nights}</strong> đêm
                        </Text>
                      </div>
                      <Text strong style={{ color: '#334155', fontSize: 14 }}>
                        {itemTotal.toLocaleString()}đ
                      </Text>
                    </div>
                    {idx < selectedRooms.length - 1 && <Divider style={{ margin: '10px 0', borderColor: '#f1f5f9' }} />}
                  </div>
                );
              })}
            </div>
          </div>

          {/* SECTION 2: CUSTOMER INFO */}
          <div style={{ marginBottom: 20 }}>
            <div style={{ padding: '6px 12px', background: '#f1f5f9', borderRadius: 6, marginBottom: 12 }}>
              <Text strong style={{ color: '#334155', fontSize: 13.5 }}>2. Thông tin khách hàng đăng ký</Text>
            </div>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item
                  name="phoneNumber"
                  label={<span style={{ fontWeight: 600, fontSize: 13 }}>Số điện thoại người đặt</span>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập số điện thoại' },
                    { pattern: /^0[0-9]{9}$/, message: 'Số điện thoại phải gồm 10 chữ số!' }
                  ]}
                >
                  <Input placeholder="Nhập 10 số điện thoại" size="large" style={{ borderRadius: 6 }} onChange={(e) => handlePhoneChange(e, 'fullName')}/>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item
                  name="fullName"
                  label={<span style={{ fontWeight: 600, fontSize: 13 }}>Họ và tên người đặt</span>}
                  rules={[
                    { required: true, message: 'Vui lòng nhập họ tên' },
                    { pattern: /^[\s\p{L}\p{M}]+$/u, message: 'Họ tên chỉ gồm chữ cái!' }
                  ]}
                >
                  <Input placeholder="Nhập đầy đủ họ tên" size="large" style={{ borderRadius: 6 }} />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="isOther" valuePropName="checked" style={{ margin: '4px 0 0' }}>
              <Checkbox onChange={(e) => setIsBookingForOthers(e.target.checked)}>
                Yêu cầu cấu hình ủy quyền lưu trú (Đặt hộ người khác)
              </Checkbox>
            </Form.Item>

            {isBookingForOthers && (
              <div style={{ marginTop: 12, padding: '14px', background: '#f0f9ff', borderRadius: 8, border: '1px solid #bae6fd' }}>
                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item
                      name="contactPhone"
                      label={<span style={{ fontWeight: 600, color: '#0369a1', fontSize: 12.5 }}>SĐT người lưu trú thực tế</span>}
                      rules={[
                        { required: true, message: 'Vui lòng nhập số điện thoại người ở' },
                        { pattern: /^0[0-9]{9}$/, message: 'SĐT không hợp lệ!' }
                      ]}
                    >
                      <Input placeholder="Nhập SĐT khách ở" size="large" style={{ borderRadius: 6 }} onChange={(e) => handlePhoneChange(e, 'contactName')} />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item
                      name="contactName"
                      label={<span style={{ fontWeight: 600, color: '#0369a1', fontSize: 12.5 }}>Họ tên người lưu trú thực tế</span>}
                      rules={[
                        { required: true, message: 'Vui lòng nhập tên người ở' },
                        { pattern: /^[\s\p{L}\p{M}]+$/u, message: 'Tên không hợp lệ!' }
                      ]}
                    >
                      <Input placeholder="Nhập họ tên khách ở" size="large" style={{ borderRadius: 6 }} />
                    </Form.Item>
                  </Col>
                </Row>
              </div>
            )}
          </div>

          {/* SECTION 3: TIME SELECTION */}
          <div style={{ marginBottom: 20 }}>
            <div style={{ padding: '6px 12px', background: '#f1f5f9', borderRadius: 6, marginBottom: 12 }}>
              <Text strong style={{ color: '#334155', fontSize: 13.5 }}>3. Thời gian lưu trú</Text>
            </div>
            <Form.Item name="dates" rules={[{ required: true, message: 'Vui lòng xác nhận ngày nhận/trả phòng' }]} style={{ margin: 0 }}>
              <RangePicker
                style={{ width: '100%', borderRadius: 6, height: 40 }}
                format="DD/MM/YYYY"
                onChange={(val) => setDates(val)}
                disabledDate={disabledPastDates}
              />
            </Form.Item>
          </div>

          {/* SECTION 4: ADDON SERVICES */}
          <div style={{ marginBottom: 20 }}>
            <div style={{ padding: '6px 12px', background: '#f1f5f9', borderRadius: 6, marginBottom: 12 }}>
              <Text strong style={{ color: '#334155', fontSize: 13.5 }}>4. Dịch vụ khách sạn bổ sung (Tùy chọn)</Text>
            </div>

            <Form.Item name="services" initialValue={[]} hidden>
              <Input />
            </Form.Item>

            <Form.Item shouldUpdate noStyle>
              {({ getFieldValue, setFieldsValue }) => {
                const value = getFieldValue('services') || [];
                const onChange = (newValue) => setFieldsValue({ services: newValue });

                const handleCardClick = (serviceId) => {
                  const exists = value.find(item => item.serviceId === serviceId);
                  if (exists) {
                    onChange(value.filter(item => item.serviceId !== serviceId));
                  } else {
                    onChange([...value, { serviceId, quantity: 1 }]);
                  }
                };

                const handleQuantityChange = (e, serviceId, newQty) => {
                  e.stopPropagation();
                  if (newQty < 1) return;
                  onChange(value.map(item => item.serviceId === serviceId ? { ...item, quantity: newQty } : item));
                };

                return (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                    {publicServices.map((service) => {
                      const selectedItem = value.find(item => item.serviceId === service.id);
                      const isSelected = !!selectedItem;

                      return (
                        <div
                          key={service.id}
                          onClick={() => handleCardClick(service.id)}
                          style={{
                            background: isSelected ? '#ecfdf5' : '#ffffff',
                            border: isSelected ? '1px solid #10b981' : '1px solid #e2e8f0',
                            borderRadius: 8,
                            padding: '10px 16px',
                            cursor: 'pointer',
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            transition: 'all 0.15s ease'
                          }}
                        >
                          <div>
                            <Text strong style={{ color: '#334155', fontSize: 13.5 }}>{service.name}</Text>
                            <span style={{ fontSize: 12, color: '#64748b', marginLeft: 10 }}>({service.description})</span>
                          </div>

                          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }} onClick={(e) => e.stopPropagation()}>
                            <Tag color={isSelected ? 'success' : 'default'} style={{ fontWeight: 600 }}>
                              +{service.price?.toLocaleString()}đ
                            </Tag>

                            {isSelected && (
                              <Space style={{ background: '#fff', border: '1px solid #a7f3d0', padding: '1px 4px', borderRadius: 4 }}>
                                <Button size="small" type="text" onClick={(e) => handleQuantityChange(e, service.id, selectedItem.quantity - 1)}>-</Button>
                                <Text strong>{selectedItem.quantity}</Text>
                                <Button size="small" type="text" onClick={(e) => handleQuantityChange(e, service.id, selectedItem.quantity + 1)}>+</Button>
                              </Space>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                );
              }}
            </Form.Item>
          </div>

          {/* SECTION 5: ACCOUNTING BILL */}
          <Form.Item shouldUpdate={(prevValues, currentValues) => prevValues.services !== currentValues.services}>
            {({ getFieldValue }) => {
              const selectedServices = getFieldValue('services') || [];
              const serviceTotal = selectedServices.reduce((sum, item) => {
                const targetService = publicServices.find(s => s.id === item.serviceId);
                return sum + (targetService ? (targetService.price * item.quantity) : 0);
              }, 0);

              const roomTotal = calculateRoomTotal();
              const totalAmount = roomTotal + serviceTotal;
              const deposit = totalAmount * 0.4;

              return (
                <div style={{ background: '#f8fafc', border: '1px solid #cbd5e1', borderRadius: 8, padding: 16 }}>
                  <Row justify="space-between" style={{ marginBottom: 6 }}>
                    <Text type="secondary">Cước phí tiền phòng thực tế:</Text>
                    <Text strong>{roomTotal.toLocaleString()}đ</Text>
                  </Row>
                  <Row justify="space-between" style={{ marginBottom: 6 }}>
                    <Text type="secondary">Cước dịch vụ chọn thêm:</Text>
                    <Text strong>{serviceTotal.toLocaleString()}đ</Text>
                  </Row>
                  <Row justify="space-between" style={{ marginBottom: 12 }}>
                    <Text strong style={{ color: '#1e3a8a' }}>Tổng chi phí toàn đơn đặt phòng:</Text>
                    <Text strong style={{ color: '#2563eb', fontSize: 15 }}>{totalAmount.toLocaleString()}đ</Text>
                  </Row>
                  <Divider style={{ margin: '8px 0', borderColor: '#cbd5e1' }} />
                  <Row justify="space-between" align="middle" style={{ marginTop: 4 }}>
                    <div>
                      <Text strong style={{ color: '#1e293b', fontSize: 14 }}>
                        Tiền đặt cọc giữ chỗ (Ký quỹ trước 40%):
                      </Text>
                    </div>
                    <Text style={{ fontSize: 24, fontWeight: 800, color: '#dc2626' }}>
                      {deposit.toLocaleString()}đ
                    </Text>
                  </Row>
                </div>
              );
            }}
          </Form.Item>
          
        </Form>
      </Modal>
    </div>
  );
};

export default RoomList;