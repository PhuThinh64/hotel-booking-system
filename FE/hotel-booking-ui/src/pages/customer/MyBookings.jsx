import React, { useEffect, useState, useRef } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';

import {
    Radio, Card, Tag, Descriptions, Typography, Button, Empty, Spin, 
    Flex, Divider, message, Modal, Space, Row, Col, Statistic, Alert
} from 'antd';

import {
    CalendarOutlined, RocketOutlined, FileTextOutlined, CloseCircleOutlined,
    CheckCircleOutlined, SyncOutlined, InfoCircleOutlined, WalletOutlined,
    HomeOutlined, ShoppingOutlined, ArrowLeftOutlined
} from '@ant-design/icons';

import axios from 'axios';
import { cancelFullBooking, cancelSingleRoom, cancelBookingService } from '../../api/ApiService';

const { Title, Text } = Typography;





const STATUS_TAG_MAPPING = {
    CONFIRMED: { color: 'success', text: 'Đã xác nhận' },
    PENDING_DEPOSIT: { color: 'warning', text: 'Chờ đặt cọc' },
    PENDING_REFUND: { color: 'orange', text: 'Chờ hoàn tiền' },
    CANCELLED: { color: 'error', text: 'Đã hủy' },
    CHECKED_IN: { color: 'blue', text: 'Đã nhận phòng' },
    CHECKED_OUT: { color: 'magenta', text: 'Đã trả phòng' },
};

const STATUS_CONFIG_MAPPING = {
    CONFIRMED: { color: 'success', icon: <CheckCircleOutlined />, text: 'Đã xác nhận' },
    PENDING_DEPOSIT: { color: 'processing', icon: <SyncOutlined spin />, text: 'Chờ thanh toán' },
    PENDING_REFUND: { color: 'orange', icon: <InfoCircleOutlined />, text: 'Chờ hoàn tiền' },
    CANCELLED: { color: 'error', icon: <CloseCircleOutlined />, text: 'Đã hủy' },
    CHECKED_IN: { color: 'blue', icon: <HomeOutlined />, text: 'Đã nhận phòng' },
    CHECKED_OUT: { color: 'default', icon: <CheckCircleOutlined />, text: 'Đã trả phòng' }
};





const getRoomIdentity = (room) => {
    return (
        room?.bookingRoomId || room?.booking_room_id || room?.id || 
        room?.roomId || room?.room_id || room?.bookingRoom?.id || null
    );
};

const isRoomCancelled = (room) => {
    const status = String(
        room?.bookingRoomStatus || room?.roomStatus || room?.booking_status || room?.status || ''
    ).trim().toUpperCase();

    return [
        'CANCELLED', 'CANCELED', 'HỦY', 'NULL', 'DELETED', 'INACTIVE'
    ].includes(status) || 
    room?.isActive === 0 || room?.is_active === 0 || room?.active === false;
};

const mergeCancelledRooms = (oldBooking, newBooking) => {
    if (!oldBooking?.bookingRooms) return newBooking;

    const newRooms = newBooking.bookingRooms || [];
    const oldRooms = oldBooking.bookingRooms || [];

    const oldRoomMap = new Map();
    oldRooms.forEach(room => oldRoomMap.set(getRoomIdentity(room), room));

    const roomMap = new Map();

    newRooms.forEach(room => {
        const id = getRoomIdentity(room);
        const oldRoom = oldRoomMap.get(id);
        
        if (oldRoom) {
            room.roomId = room.roomId || oldRoom.roomId;
            room.roomNumber = room.roomNumber || oldRoom.roomNumber;
            room.roomType = room.roomType || oldRoom.roomType;
        }

        if (['CANCELLED', 'PENDING_REFUND'].includes(newBooking.status)) {
            room.status = 'CANCELLED';
            room.bookingRoomStatus = 'CANCELLED';
            room.roomStatus = 'CANCELLED';
        }
        roomMap.set(id, room);
    });

    oldRooms.forEach(room => {
        const id = getRoomIdentity(room);
        
        if (!roomMap.has(id)) {
            roomMap.set(id, {
                ...room,
                status: 'CANCELLED',
                bookingRoomStatus: 'CANCELLED',
                roomStatus: 'CANCELLED'
            });
        } else if (isRoomCancelled(room)) {
            const existingRoom = roomMap.get(id);
            existingRoom.status = 'CANCELLED';
            existingRoom.bookingRoomStatus = 'CANCELLED';
            existingRoom.roomStatus = 'CANCELLED';
        }
    });

    return {
        ...newBooking,
        bookingRooms: Array.from(roomMap.values())
    };
};

const formatVND = (amount) =>
    amount?.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) || '0 ₫';

const formatDate = (dateString) => 
    dateString ? new Date(dateString).toLocaleDateString('vi-VN') : 'N/A';






const MyBookings = () => {
    const navigate = useNavigate();
    const { bookingCode } = useParams();
    const [searchParams] = useSearchParams();

    
    const phoneParam = searchParams.get('phone') || searchParams.get('phoneNumber') || searchParams.get('contactPhone');
    const codeParam = searchParams.get('code') || searchParams.get('bookingCode');

    
    const [historyBookings, setHistoryBookings] = useState([]);
    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [isRefundModalOpen, setIsRefundModalOpen] = useState(false);
    const [selectedRefundMethod, setSelectedRefundMethod] = useState('CASH'); 
    const [bookingToCancel, setBookingToCancel] = useState(null);
    const [roomToCancel, setRoomToCancel] = useState(null); 
    const [cancelType, setCancelType] = useState('');

    const bookingRef = useRef(booking);
    useEffect(() => {
        bookingRef.current = booking;
    }, [booking]);

    
    useEffect(() => {
        if (phoneParam && codeParam) {
            
            fetchBookingByLookup(phoneParam, codeParam);
        } else {
            
            fetchMyHistory();
        }
    }, [phoneParam, codeParam]);

    
    const calculateNights = () => {
        if (!booking?.arrivalDate || !booking?.departureDate) return 1;
        return Math.max(
            1,
            Math.ceil((new Date(booking.departureDate) - new Date(booking.arrivalDate)) / (1000 * 60 * 60 * 24))
        );
    };

    const getRoomPrice = (room) => Number(room?.priceAtOrder || room?.price_at_order || 0);

    const getActiveRoomAmount = () => {
        const rooms = booking?.bookingRooms || [];
        const nights = calculateNights(); 
        
        return rooms.reduce((sum, room) => 
            isRoomCancelled(room) ? sum : sum + (getRoomPrice(room) * nights), 0
        );
    };

    const getAdjustedTotal = () => {
        if (['CANCELLED', 'PENDING_REFUND'].includes(booking?.status)) return 0;
        return getActiveRoomAmount() + Number(booking?.serviceAmount || 0) + Number(booking?.surchargeAmount || 0);
    };

    
    const fetchBookingByLookup = async (phone, code, silent = false) => {
        if (!silent) setLoading(true);
        try {
            const res = await axios.get(`http://localhost:8080/api/v1/bookings/lookup`, {
                params: { phone, bookingCode: code }
            });
            const bookingData = res.data.result;

            if (bookingData) {
                const finalBooking = mergeCancelledRooms(bookingRef.current, bookingData);
                setHistoryBookings([finalBooking]);
                setBooking(finalBooking);
                return finalBooking;
            } else {
                if (!silent) {
                    setHistoryBookings([]);
                    message.warning("Không tìm thấy đơn đặt phòng phù hợp.");
                }
                return null;
            }
        } catch (err) {
            console.log(err);
            if (!silent) {
                message.error(err.response?.data?.message || "Không tìm thấy đơn hàng");
                setHistoryBookings([]);
            }
            return null;
        } finally {
            if (!silent) setLoading(false);
        }
    };

    const reloadCurrentBooking = async (bookingId) => {
        if (phoneParam && codeParam) {
            await fetchBookingByLookup(phoneParam, codeParam, true); 
        } else {
            await refreshMyHistoryWithRetry(bookingId);
        }
    };

    const fetchMyHistory = async (silent = false) => {
        const token = localStorage.getItem("token");
        if (!token) {
            setHistoryBookings([]);
            navigate('/auth');
            return [];
        }

        if (!silent) setLoading(true);
        try {
            const res = await axios.get("http://localhost:8080/api/v1/bookings/my-history", {
                headers: { Authorization: `Bearer ${token}` },
            });
            const result = res.data.result || [];
            
            let finalResult = result;
            if (bookingRef.current?.id) {
                const updatedBooking = result.find((b) => b.id === bookingRef.current.id);
                if (updatedBooking) {
                    const mergedBooking = mergeCancelledRooms(bookingRef.current, updatedBooking);
                    finalResult = result.map((b) => (b.id === bookingRef.current.id ? mergedBooking : b));
                    setBooking(mergedBooking);
                    bookingRef.current = mergedBooking;
                }
            }
            
            setHistoryBookings(finalResult);
            return finalResult;
        } catch (err) {
            console.log(err);
            if (!silent) message.error("Không thể tải lịch sử đơn hàng"); 
            return [];
        } finally {
            if (!silent) setLoading(false);
        }
    };

    const refreshMyHistoryWithRetry = async (bookingId, maxAttempts = 5, delayMs = 600) => {
        let result = [];
        for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
            result = await fetchMyHistory(true);
            if (bookingId) {
                const updatedBooking = result.find((b) => b.id === bookingId);
                if (updatedBooking) {
                    const mergedBooking = mergeCancelledRooms(booking, updatedBooking);
                    setBooking(mergedBooking);
                    bookingRef.current = mergedBooking; 
                    setHistoryBookings(result.map((b) => (b.id === bookingId ? mergedBooking : b)));
                    if (['CANCELLED', 'PENDING_REFUND'].includes(mergedBooking.status)) {
                        return result;
                    }
                }
            } else {
                break;
            }
            if (attempt < maxAttempts) {
                await new Promise((resolve) => setTimeout(resolve, delayMs));
            }
        }
        return result;
    };

    
    const markRoomCancelledLocally = (bookingData, roomRecord) => {
        if (!bookingData || !bookingData.bookingRooms) return;
        const cancelledId = getRoomIdentity(roomRecord);
        const updatedBooking = {
            ...bookingData,
            bookingRooms: bookingData.bookingRooms.map((room) =>
                getRoomIdentity(room) === cancelledId
                    ? {
                        ...room,
                        status: 'CANCELLED',
                        bookingRoomStatus: 'CANCELLED',
                        roomStatus: 'CANCELLED'
                    }
                    : room
            )
        };
        bookingRef.current = updatedBooking;
        setBooking(updatedBooking);
    };

    const handleCancelSingleRoom = (bookingData, roomRecord) => {
        const currentBooking = (bookingData && bookingData.id) ? bookingData : booking;
        if (!currentBooking) return;

        const activeRooms = currentBooking.bookingRooms.filter((r) => !isRoomCancelled(r));
        const isLastRoom = activeRooms.length === 1 && getRoomIdentity(activeRooms[0]) === getRoomIdentity(roomRecord);
        const method = currentBooking.paymentMethod?.toUpperCase();

        if (isLastRoom) {
            if (method === 'VNPAY') {
                Modal.confirm({
                    title: 'Hủy phòng cuối cùng?',
                    content: 'Hủy phòng này đồng nghĩa với việc hủy toàn bộ đơn đặt. Tiền của bạn sẽ được tự động hoàn qua cổng VNPay.',
                    okText: 'Xác nhận',
                    onOk: async () => {
                        try {
                            await cancelSingleRoom(currentBooking.id, roomRecord.bookingRoomId, null, "Khách tự hủy phòng cuối trên website");
                            message.success("Hủy đơn thành công!");
                            markRoomCancelledLocally(currentBooking, roomRecord);
                            await new Promise(resolve => setTimeout(resolve, 300));
                            await reloadCurrentBooking(currentBooking.id);
                        } catch (err) { 
                            message.error(err.response?.data?.message || "Lỗi xử lý"); 
                        }
                    }
                });
            } else {
                setBookingToCancel(currentBooking);
                setRoomToCancel(roomRecord);
                setCancelType('SINGLE_LAST');
                setIsRefundModalOpen(true);
            }
        } else {
            Modal.confirm({
                title: 'Xác nhận hủy phòng này?',
                content: 'Tiền phòng hủy sẽ được khấu trừ lại sau.',
                okText: 'Xác nhận hủy',
                onOk: async () => {
                    try {
                        await cancelSingleRoom(currentBooking.id, roomRecord.bookingRoomId, null, "Khách tự hủy phòng lẻ trên website");
                        message.success("Hủy phòng thành công!");
                        markRoomCancelledLocally(currentBooking, roomRecord);
                        await new Promise(resolve => setTimeout(resolve, 300));
                        await reloadCurrentBooking(currentBooking.id);
                    } catch (err) {
                        message.error("Lỗi hủy phòng");
                    }
                }
            });
        }
    };

    const handleCancelBooking = (bookingItem) => {
        if (bookingItem?.paymentMethod?.toUpperCase() === 'VNPAY') {
            Modal.confirm({
                title: 'Xác nhận hủy đơn?',
                content: 'Đơn hàng VNPay sẽ được tự động hoàn tiền sau khi hủy. Bạn có chắc chắn muốn tiếp tục?',
                okText: 'Xác nhận',
                cancelText: 'Quay lại',
                onOk: async () => {
                    try {
                        setLoading(true);
                        await cancelFullBooking(bookingItem.id, null, "Khách tự hủy");
                        message.success("Hủy đơn thành công, tiền sẽ được hoàn tự động!");
                        await reloadCurrentBooking(bookingItem.id);
                    } catch (err) {
                        message.error("Lỗi khi hủy đơn");
                    } finally {
                        setLoading(false);
                    }
                }
            });
            return; 
        }

        setBookingToCancel(bookingItem);
        setCancelType('FULL'); 
        setIsRefundModalOpen(true);
    };

    const handleCancelService = async (serviceId, serviceName) => {
        Modal.confirm({
            title: 'Xác nhận hủy dịch vụ',
            content: `Bạn có chắc chắn muốn hủy dịch vụ "${serviceName}" không?`,
            okText: 'Xác nhận hủy',
            cancelText: 'Quay lại',
            onOk: async () => {
                try {
                    await cancelBookingService(serviceId);
                    message.success(`Đã hủy dịch vụ "${serviceName}" thành công!`);
                    if (booking?.id) {
                        await reloadCurrentBooking(booking.id);
                    }
                } catch (error) {
                    console.error(error);
                    message.error(error.response?.data?.message || 'Có lỗi xảy ra khi hủy dịch vụ');
                }
            },
        });
    };

    const submitCustomerCancelWithRefund = async () => {
        try {
            setLoading(true);
            if (cancelType === 'FULL') {
                await cancelFullBooking(bookingToCancel.id, selectedRefundMethod, "Khách tự hủy");
            } else {
                await cancelSingleRoom(bookingToCancel.id, roomToCancel.bookingRoomId, selectedRefundMethod, "Khách tự hủy");
            }
            
            message.success("Hủy thành công!");
            setIsRefundModalOpen(false);
            await new Promise(resolve => setTimeout(resolve, 300));
            await reloadCurrentBooking(bookingToCancel?.id);
        } catch (err) {
            message.error("Lỗi: " + (err.response?.data?.message || "Có lỗi xảy ra"));
        } finally {
            setLoading(false);
        }
    };

    const getRefundLabel = () => {
        const method = booking?.refundMethod || booking?.paymentMethod;
        switch (method) {
            case 'VNPAY': return 'Tiền hoàn về ví';
            case 'BANK_TRANSFER': return 'Tiền hoàn chuyển khoản';
            case 'CASH': return 'Tiền hoàn trực tiếp';
            default: return 'Tiền hoàn trả';
        }
    };
    
    const renderStatusTag = (status) => {
        const config = STATUS_TAG_MAPPING[status] || { color: 'default', text: status };
        return <Tag color={config.color}>{config.text}</Tag>;
    };

    const getStatusConfig = (status) => {
        return STATUS_CONFIG_MAPPING[status] || {
            color: 'default',
            icon: <InfoCircleOutlined />,
            text: status
        };
    };

    if (loading) {
        return (
            <Flex justify="center" align="center" style={{ height: '80vh' }}>
                <Spin size="large" tip="Đang tải..." />
            </Flex>
        );
    }

    return (
        <div style={{ maxWidth: '1000px', margin: '40px auto', padding: '0 20px' }}>
            <div
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginBottom: 25,
                    padding: '12px 16px',
                    background: 'linear-gradient(135deg, #f0f5ff 0%, #ffffff 100%)',
                    borderRadius: 14,
                    border: '1px solid #e6f4ff',
                    boxShadow: '0 2px 10px rgba(24,144,255,0.08)'
                }}
            >
                <div
                    onClick={() => navigate('/')}
                    style={{ cursor: 'pointer', padding: '6px 10px', borderRadius: 10, display: 'flex', alignItems: 'center' }}
                >
                    <ArrowLeftOutlined style={{ color: '#1677ff' }} />
                </div>

                <div style={{ flex: 1, display: 'flex', justifyContent: 'center' }}>
                    <div onClick={() => navigate('/')} style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                        <HomeOutlined style={{ color: '#1677ff' }} />
                        <Text style={{ color: '#1677ff', fontWeight: 600 }}>Về trang chủ</Text>
                    </div>
                </div>
                <div style={{ width: 40 }} />
            </div>

            <Title level={2} style={{ textAlign: 'center', marginBottom: '40px' }}>
                <RocketOutlined style={{ color: '#1890ff', marginRight: 12 }} />
                Quản lý đơn hàng
            </Title>

            {historyBookings.length === 0 ? (
                <Empty description="Bạn chưa có đơn đặt phòng nào" />
            ) : (
                <Flex vertical gap={24}>
                    {historyBookings.map((bookingItem) => (
                        <Card
                            key={bookingItem.id}
                            hoverable
                            style={{
                                borderRadius: 16,
                                borderLeft: `6px solid ${bookingItem.status === 'CANCELLED' ? '#ff4d4f' : '#52c41a'}`
                            }}
                            actions={[
                                <Button
                                    type="primary"
                                    icon={<FileTextOutlined />}
                                    onClick={() => {
                                        setBooking(bookingItem);
                                        setIsModalVisible(true);
                                    }}
                                >
                                    Xem chi tiết
                                </Button>,
                                <Button
                                    danger
                                    icon={<CloseCircleOutlined />}
                                    disabled={
                                        bookingItem.status === 'CANCELLED' ||
                                        bookingItem.status === 'CHECKED_OUT' ||
                                        bookingItem.status === 'PENDING_REFUND'
                                    }
                                    onClick={() => handleCancelBooking(bookingItem)}
                                >
                                    Hủy đơn
                                </Button>
                            ]}
                        >
                            <Flex justify="space-between" align="start">
                                <Space direction="vertical" size={0}>
                                    <Text type="secondary">Mã đơn hàng</Text>
                                    <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
                                        {bookingItem.bookingCode}
                                    </Title>
                                </Space>
                                <Tag
                                    icon={getStatusConfig(bookingItem.status).icon}
                                    color={getStatusConfig(bookingItem.status).color}
                                    style={{ padding: '6px 14px', fontSize: 14 }}
                                >
                                    {getStatusConfig(bookingItem.status).text}
                                </Tag>
                            </Flex>

                            <Divider />

                            <Flex justify="space-between" align="center" wrap="wrap">
                                <Space size="large">
                                    <Space direction="vertical" size={0}>
                                        <Text type="secondary"><CalendarOutlined /> Nhận phòng</Text>
                                        <Text strong>{formatDate(bookingItem.arrivalDate)}</Text>
                                    </Space>
                                    <Space direction="vertical" size={0}>
                                        <Text type="secondary"><CalendarOutlined /> Trả phòng</Text>
                                        <Text strong>{formatDate(bookingItem.departureDate)}</Text>
                                    </Space>
                                </Space>

                                <Space direction="vertical" align="end" size={0}>
                                    <Text type="secondary">Tổng thanh toán</Text>
                                    <Text type="danger" style={{ fontSize: 24, fontWeight: 700 }}>
                                        {formatVND(bookingItem.totalAmount)}
                                    </Text>
                                </Space>
                            </Flex>
                        </Card>
                    ))}
                </Flex>
            )}

            <Modal
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                width={1000}
                centered
                footer={null}
                styles={{ body: { padding: 0, overflow: 'hidden', borderRadius: 20 } }}
            >
                {booking && (
                    <div>
                        <div style={{ background: 'linear-gradient(135deg, #1677ff 0%, #69b1ff 100%)', padding: '26px 32px', color: '#fff' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 20, flexWrap: 'wrap' }}>
                                <div>
                                    <Text style={{ color: 'rgba(255,255,255,0.85)', fontSize: 13, letterSpacing: 1 }}>MÃ ĐƠN ĐẶT PHÒNG</Text>
                                    <Title level={2} style={{ color: '#fff', margin: '4px 0 0' }}>{booking.bookingCode}</Title>
                                </div>
                            </div>
                        </div>

                        <div style={{ padding: 28 }}>
                            {!booking.bookingRooms?.some(r => r.roomNumber) && (
                                <Alert
                                    type="info"
                                    showIcon
                                    style={{ marginBottom: 24, borderRadius: 12 }}
                                    message="Số phòng sẽ được khách sạn sắp xếp khi khách nhận phòng."
                                />
                            )}

                            <Row gutter={[20, 20]}>
                                <Col xs={24} lg={14}>
                                    <Card bordered={false} style={{ borderRadius: 18, background: '#fafcff', height: '100%' }}>
                                        <Title level={4} style={{ marginBottom: 24 }}>
                                            <InfoCircleOutlined /> Thông tin đặt phòng
                                        </Title>
                                        <Row gutter={[16, 22]}>
                                            <Col span={12}>
                                                <Text type="secondary">Tên khách hàng</Text>
                                                <div style={{ marginTop: 6, fontWeight: 700, fontSize: 16 }}>
                                                    {booking.contactName || booking.customerName || 'Khách vãng lai'}
                                                </div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Số điện thoại</Text>
                                                <div style={{ marginTop: 6, fontWeight: 600 }}>{booking.contactPhone || '---'}</div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Ngày nhận phòng</Text>
                                                <div style={{ marginTop: 6, fontWeight: 600 }}>{formatDate(booking.arrivalDate)}</div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Ngày trả phòng</Text>
                                                <div style={{ marginTop: 6, fontWeight: 600 }}>{formatDate(booking.departureDate)}</div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Check-in thực tế</Text>
                                                <div style={{ marginTop: 6, fontWeight: 600, color: booking.actualCheckIn ? '#52c41a' : '#999' }}>
                                                    {booking.actualCheckIn ? new Date(booking.actualCheckIn).toLocaleString('vi-VN') : 'Chưa check-in'}
                                                </div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Check-out thực tế</Text>
                                                <div style={{ marginTop: 6, fontWeight: 600, color: booking.actualCheckOut ? '#1677ff' : '#999' }}>
                                                    {booking.actualCheckOut ? new Date(booking.actualCheckOut).toLocaleString('vi-VN') : 'Chưa check-out'}
                                                </div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Trạng thái đơn</Text>
                                                <div style={{ marginTop: 8 }}>
                                                    <Tag
                                                        color={getStatusConfig(booking.status).color}
                                                        style={{ padding: '6px 14px', borderRadius: 30, fontWeight: 600, fontSize: 14 }}
                                                    >
                                                        {getStatusConfig(booking.status).icon} {getStatusConfig(booking.status).text}
                                                    </Tag>
                                                </div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Thời gian lưu trú</Text>
                                                <div style={{ marginTop: 6, fontWeight: 700, fontSize: 16, color: '#1677ff' }}>{calculateNights()} đêm</div>
                                            </Col>
                                            <Col span={12}>
                                                <Text type="secondary">Ngày tạo đơn</Text>
                                                <div style={{ marginTop: 6, fontWeight: 600 }}>{new Date(booking.createdAt).toLocaleString('vi-VN')}</div>
                                            </Col>
                                        </Row>
                                    </Card>
                                </Col>

                                <Col xs={24} lg={10}>
                                    <Card bordered={false} style={{ borderRadius: 18, background: 'linear-gradient(135deg,#fff7e6 0%,#fff 100%)', height: '100%' }}>
                                        <Title level={4} style={{ marginBottom: 24 }}><WalletOutlined /> Chi tiết thanh toán</Title>
                                        {(() => {
                                            const currentTotal = getAdjustedTotal();
                                            const deposit = Number(booking?.depositAmount || 0);
                                            const refund = Number(booking?.refundAmount || 0);
                                            const remainingToPay = Math.max(0, currentTotal - (deposit - refund));

                                            return (
                                                <Flex vertical gap={18}>
                                                    <Flex justify="space-between">
                                                        <Text>Tiền phòng hiện tại</Text>
                                                        <Text strong>{formatVND(getActiveRoomAmount())}</Text>
                                                    </Flex>
                                                    {booking.bookingRooms?.some(isRoomCancelled) && (
                                                        <Text type="danger" style={{ marginTop: -12, fontSize: 12, fontWeight: 500 }}>
                                                            * Đã loại bỏ phòng hủy khỏi hóa đơn
                                                        </Text>
                                                    )}
                                                    <Flex justify="space-between">
                                                        <Text>Tiền dịch vụ</Text>
                                                        <Text strong>{formatVND(booking.serviceAmount)}</Text>
                                                    </Flex>
                                                    {Number(booking?.surchargeAmount) > 0 && (
                                                        <Flex justify="space-between">
                                                            <Text>Phụ phí phát sinh</Text>
                                                            <Text strong style={{ color: '#fa8c16' }}>{formatVND(booking.surchargeAmount)}</Text>
                                                        </Flex>
                                                    )}
                                                    <Flex justify="space-between">
                                                        <Text>Tiền cọc ban đầu</Text>
                                                        <Text strong style={{ color: '#52c41a' }}>{formatVND(deposit)}</Text>
                                                    </Flex>
                                                    <Flex justify="space-between">
                                                        <Text type={refund > 0 ? "danger" : "secondary"} style={{ fontWeight: 500 }}> {getRefundLabel()}</Text>
                                                        <Text strong type={refund > 0 ? "danger" : undefined}>
                                                            {refund > 0 ? `-${formatVND(refund)}` : formatVND(0)}
                                                        </Text>
                                                    </Flex>
                                                    <Divider style={{ margin: '4px 0' }} />
                                                    <Flex justify="space-between" align="center">
                                                        <Text strong>Còn cần thanh toán tại quầy</Text>
                                                        {remainingToPay <= 0 ? (
                                                            <Tag color="success" style={{ fontSize: 13, fontWeight: 700, borderRadius: 20 }}>ĐÃ CỌC ĐỦ 100%</Tag>
                                                        ) : (
                                                            <Text strong style={{ color: '#ff4d4f', fontSize: 16 }}>{formatVND(remainingToPay)}</Text>
                                                        )}
                                                    </Flex>
                                                    <Divider style={{ margin: '4px 0' }} />
                                                    <Flex justify="space-between" align="center">
                                                        <Text strong style={{ fontSize: 16 }}>Tổng thanh toán (Giá trị đơn)</Text>
                                                        <Text style={{ fontSize: 26, fontWeight: 800, color: '#fa541c' }}>{formatVND(currentTotal)}</Text>
                                                    </Flex>
                                                </Flex>
                                            );
                                        })()}
                                    </Card>
                                </Col>
                            </Row>

                            <Divider orientation="left" style={{ marginTop: 34, fontSize: 18, fontWeight: 700 }}><HomeOutlined /> Danh sách phòng</Divider>
                            {booking.bookingRooms?.length > 0 ? (
                                <Row gutter={[18, 18]}>
                                    {booking.bookingRooms.map((room, index) => {
                                        const roomCancelled = isRoomCancelled(room) || booking.status === 'CANCELLED';
                                        return (
                                            <Col xs={24} md={12} key={index}>
                                                <Card 
                                                    hoverable={!roomCancelled} 
                                                    bordered={false}
                                                    style={{
                                                        borderRadius: 18,
                                                        overflow: 'hidden',
                                                        opacity: roomCancelled ? 0.6 : 1,
                                                        cursor: roomCancelled ? 'not-allowed' : 'pointer', 
                                                        pointerEvents: roomCancelled ? 'none' : 'auto',
                                                        background: roomCancelled ? 'linear-gradient(135deg,#f5f5f5 0%,#fafafa 100%)' : 'linear-gradient(135deg,#ffffff 0%,#f9fbff 100%)',
                                                        boxShadow: roomCancelled ? 'none' : '0 6px 18px rgba(0,0,0,0.05)',
                                                    }}
                                                >
                                                    <Space direction="vertical" size={12} style={{ width: '100%' }}>
                                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', gap: 10 }}>
                                                            <div>
                                                                <Text type="secondary" style={{ fontSize: 12 }}>LOẠI PHÒNG</Text>
                                                                <div style={{ fontSize: 20, fontWeight: 700, marginTop: 2 }}>{room.roomType}</div>
                                                            </div>
                                                            <Tag color="blue" style={{ borderRadius: 20, padding: '4px 10px' }}>{room.roomNumber || 'Chưa xếp phòng'}</Tag>
                                                        </div>
                                                        <Divider style={{ margin: '2px 0' }} />
                                                        <Flex justify="space-between"><Text type="secondary">Giá phòng</Text><Text strong>{formatVND(getRoomPrice(room))}</Text></Flex>
                                                        <Flex justify="space-between"><Text type="secondary">Số khách tối đa</Text><Text strong>{room.maxGuests || 2} khách</Text></Flex>
                                                        {roomCancelled ? (
                                                            <Tag color="error" style={{ fontWeight: 600, width: '100%', textAlign: 'center' }}>Phòng đã bị hủy</Tag>
                                                        ) : booking.status === 'CONFIRMED' ? (
                                                            <Button size="small" danger onClick={() => handleCancelSingleRoom(booking, room)}>Hủy phòng</Button>
                                                        ) : null}
                                                    </Space>
                                                </Card>
                                            </Col>
                                        );
                                    })}
                                </Row>
                            ) : (
                                <Empty description="Không có thông tin phòng" />
                            )}

                            <Divider orientation="left" style={{ marginTop: 34, fontSize: 18, fontWeight: 700 }}><ShoppingOutlined /> Dịch vụ đã đặt</Divider>
                            {(booking?.bookingServices || []).length > 0 ? (
                                <Row gutter={[18, 18]}>
                                    {(booking?.bookingServices || []).map((service) => {
                                        const isCancelled = service.status === 'CANCELLED';
                                        return (
                                            <Col xs={24} md={12} key={service.id}>
                                                <Card
                                                    hoverable={!isCancelled}
                                                    bordered={false}
                                                    style={{
                                                        borderRadius: 18,
                                                        overflow: 'hidden',
                                                        opacity: isCancelled ? 0.6 : 1,
                                                        cursor: isCancelled ? 'not-allowed' : 'pointer',
                                                        pointerEvents: isCancelled ? 'none' : 'auto',
                                                        background: isCancelled ? 'linear-gradient(135deg,#f5f5f5 0%,#fafafa 100%)' : 'linear-gradient(135deg,#ffffff 0%,#f9fbff 100%)',
                                                        boxShadow: isCancelled ? 'none' : '0 6px 18px rgba(0,0,0,0.05)',
                                                    }}
                                                >
                                                    <Space direction="vertical" size={12} style={{ width: '100%' }}>
                                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', gap: 10 }}>
                                                            <div>
                                                                <Text type="secondary" style={{ fontSize: 12 }}>DỊCH VỤ</Text>
                                                                <div style={{ fontSize: 18, fontWeight: 700, marginTop: 2 }}>{service.serviceName || 'Dịch vụ'}</div>
                                                            </div>
                                                            <Tag color={isCancelled ? 'error' : 'blue'} style={{ borderRadius: 20, padding: '4px 10px' }}>{isCancelled ? 'Đã hủy' : 'Đang sử dụng'}</Tag>
                                                        </div>
                                                        <Divider style={{ margin: '2px 0' }} />
                                                        <Flex justify="space-between"><Text type="secondary">Số lượng</Text><Text strong>{service.quantity}</Text></Flex>
                                                        <Flex justify="space-between"><Text type="secondary">Đơn giá</Text><Text strong>{Number(service.priceAtOrder || 0).toLocaleString()}₫</Text></Flex>
                                                        <Flex justify="space-between" align="center"><Text type="secondary">Thành tiền</Text><Text strong style={{ fontSize: 16, color: '#1890ff' }}>{Number(service.totalPrice || 0).toLocaleString()}₫</Text></Flex>
                                                        {isCancelled ? (
                                                            <Tag color="error" style={{ fontWeight: 600, width: '100%', textAlign: 'center', marginTop: '10px', borderRadius: '6px' }}>Dịch vụ đã bị hủy</Tag>
                                                        ) : !['CANCELLED', 'CHECKED_OUT'].includes(booking?.status) && (
                                                            <Button block danger size="small" onClick={() => handleCancelService(service.id, service.serviceName)} style={{ marginTop: '10px' }}>Hủy dịch vụ</Button>
                                                        )}
                                                    </Space>
                                                </Card>
                                            </Col>
                                        );
                                    })}
                                </Row>
                            ) : (
                                <Empty description="Không có dịch vụ nào được đặt" />
                            )}

                            <div style={{ marginTop: 40, display: 'flex', justifyContent: 'flex-end', gap: 12, flexWrap: 'wrap' }}>
                                {booking.status !== 'CANCELLED' && booking.status !== 'CHECKED_OUT' && booking.status !== 'PENDING_REFUND' && (
                                    <Button
                                        danger
                                        size="large"
                                        icon={<CloseCircleOutlined />}
                                        onClick={() => handleCancelBooking(booking)}
                                        style={{ borderRadius: 12, height: 46, paddingInline: 26 }}
                                    >
                                        Hủy đơn
                                    </Button>
                                )}
                                <Button
                                    size="large"
                                    onClick={() => setIsModalVisible(false)}
                                    style={{ borderRadius: 12, height: 44, paddingInline: 24, fontWeight: 600 }}
                                >
                                    Đóng
                                </Button>
                            </div>
                        </div>
                    </div>
                )}
            </Modal>

            <Modal
                title={<Space><CloseCircleOutlined style={{ color: '#ff4d4f' }} /><b>Lựa chọn hình thức nhận tiền hoàn</b></Space>}
                open={isRefundModalOpen}
                onCancel={() => setIsRefundModalOpen(false)}
                footer={[
                    <Button key="close" onClick={() => setIsRefundModalOpen(false)}>Quay lại</Button>,
                    <Button key="confirm" danger type="primary" loading={loading} onClick={submitCustomerCancelWithRefund}>Xác nhận hủy đơn</Button>
                ]}
                centered
                width={450}
            >
                <div style={{ padding: '8px 0' }}>
                    <Typography.Paragraph>
                        <Text>Vui lòng chọn phương thức nhận tiền hoàn cọc:</Text>
                    </Typography.Paragraph>
                    <div style={{ backgroundColor: '#f5f5f5', padding: '16px', borderRadius: '8px', marginTop: '12px' }}>
                        <Radio.Group 
                            onChange={(e) => setSelectedRefundMethod(e.target.value)} 
                            value={selectedRefundMethod}
                            style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}
                        >
                            <Radio value="CASH">Nhận bằng Tiền mặt (Tại quầy lễ tân)</Radio>
                            <Radio value="BANK_TRANSFER">Nhận qua Chuyển khoản Ngân hàng</Radio>
                        </Radio.Group>
                    </div>
                    
                    {selectedRefundMethod === 'BANK_TRANSFER' && (
                        <Alert 
                            type="info" 
                            showIcon 
                            message="Lưu ý về Chuyển khoản"
                            description="Sau khi xác nhận, quản trị viên khách sạn sẽ kiểm tra thông tin và tiến hành chuyển khoản hoàn tiền cho bạn trong vòng 24h làm việc." 
                            style={{ marginTop: '12px' }}
                        />
                    )}
                </div>
            </Modal>
        </div>
    );
};

export default MyBookings;