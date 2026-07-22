import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from "react-router-dom";
import { Spin, Result, Button, Card, Descriptions, Divider, List, Tag, Typography } from 'antd';
import axios from 'axios';

const { Title, Text } = Typography;

const PaymentResult = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [status, setStatus] = useState('loading');
    const [data, setData] = useState(null);
    const [phoneFromUrl, setPhoneFromUrl] = useState(''); 

    useEffect(() => {
        const fetchBookingInfo = async () => {
            const urlParams = new URLSearchParams(window.location.search);
            const statusParam = urlParams.get('status');
            const bookingCodeParam = urlParams.get('bookingCode');
            const phoneParam = urlParams.get('phone'); 

            if (phoneParam) {
                setPhoneFromUrl(phoneParam);
            }

            
            
            if (statusParam === 'success') {
                setStatus('success');

                
                if (bookingCodeParam) {
                    try {
                        const res = await axios.get(`http://localhost:8080/api/v1/bookings/lookup`, {
                            params: {
                                phone: phoneParam,
                                bookingCode: bookingCodeParam
                            }
                        });
                        setData(res.data.result);
                    } catch (error) {
                        console.error("Giao dịch thành công nhưng không fetch được chi tiết đơn:", error);
                        
                    }
                }
            } else {
                setStatus('error');
            }
            setLoading(false);
        };
        fetchBookingInfo();
    }, [location]);

    
    const handleViewDetail = () => {
        const activePhone = phoneFromUrl || data?.contactPhone || data?.customerPhone || data?.phoneNumber || '';
        const activeCode = data?.bookingCode || new URLSearchParams(window.location.search).get('bookingCode') || '';

        
        navigate(`/my-bookings?phone=${activePhone}&code=${activeCode}`);
    };

    if (loading) return <div style={{ textAlign: 'center', marginTop: 100 }}><Spin size="large" /></div>;

    return (
        <div style={{ padding: '40px 20px', maxWidth: '800px', margin: '0 auto' }}>
            {status === 'success' ? (
                <Card bordered={false} style={{ boxShadow: '0 4px 12px rgba(0,0,0,0.1)', borderRadius: '12px' }}>
                    <Result
                        status="success"
                        title={<Title level={2}>Thanh toán thành công!</Title>}
                        subTitle="Cảm ơn bạn đã tin tưởng dịch vụ. Giao dịch của bạn đã được hệ thống ghi nhận thành công."
                    />
                    
                    {/* Hiển thị chi tiết thông tin đơn hàng nếu có dữ liệu từ API */}
                    {data && (
                        <>
                            <Divider />
                            <Descriptions title="Thông tin đơn đặt phòng" bordered column={1}>
                                <Descriptions.Item label="Mã đặt phòng">{data?.bookingCode}</Descriptions.Item>
                                
                                {/* 🌟 ĐA DẠNG FALLBACK: Ưu tiên hiển thị tên người ở thực tế (contactName) */}
                                <Descriptions.Item label="Khách hàng">
                                    {data?.contactName || data?.customerName || data?.fullName || 'Khách hàng'}
                                </Descriptions.Item>
                                
                                {/* 🌟 Ưu tiên hiển thị SĐT người ở thực tế */}
                                <Descriptions.Item label="Số điện thoại">
                                    {phoneFromUrl || data?.contactPhone || data?.customerPhone || data?.phoneNumber || 'N/A'}
                                </Descriptions.Item>
                                
                                <Descriptions.Item label="Tổng tiền">
                                    <Text strong type="danger">{data?.totalAmount?.toLocaleString()} VND</Text>
                                </Descriptions.Item>
                                <Descriptions.Item label="Trạng thái">
                                    <Tag color="green">{data?.status || 'Đã xác nhận'}</Tag>
                                </Descriptions.Item>
                            </Descriptions>

                            {data?.bookingRooms && data.bookingRooms.length > 0 && (
                                <>
                                    <Title level={5} style={{ marginTop: 20 }}>Danh sách loại phòng:</Title>
                                    <List
                                        dataSource={data.bookingRooms}
                                        renderItem={room => (
                                            <List.Item>
                                                <List.Item.Meta
                                                    title={room.roomType || room.roomTypeName}
                                                    description={`Giá tại thời điểm đặt: ${room.priceAtOrder?.toLocaleString()} VND`}
                                                />
                                            </List.Item>
                                        )}
                                    />
                                </>
                            )}
                        </>
                    )}

                    <div style={{ marginTop: 30, display: 'flex', gap: '12px', justifyContent: 'center' }}>
                        {/* Chỉ hiện nút xem đơn hàng khi có đủ mã đơn (Luồng đặt phòng mới) */}
                        {(data?.bookingCode || new URLSearchParams(window.location.search).get('bookingCode')) && (
                            <Button type="primary" onClick={handleViewDetail}>
                                Xem chi tiết đơn hàng
                            </Button>
                        )}
                        <Button onClick={() => navigate('/')}>Về trang chủ</Button>
                    </div>
                </Card>
            ) : (
                <Result
                    status="error"
                    title="Thanh toán thất bại"
                    subTitle="Đã có lỗi xảy ra hoặc giao dịch đã bị hủy từ phía ngân hàng. Vui lòng kiểm tra lại số dư tài khoản của bạn."
                    extra={[
                        <Button type="primary" key="home" onClick={() => navigate('/')}>Quay lại trang chủ</Button>,
                        <Button key="retry" onClick={() => navigate('/')}>Thử lại</Button>
                    ]}
                />
            )}
        </div>
    );
};

export default PaymentResult;