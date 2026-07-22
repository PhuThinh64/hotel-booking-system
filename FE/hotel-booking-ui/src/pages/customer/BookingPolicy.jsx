import React from 'react';
import { Card, Typography, Table, Tag, Alert, Row, Col, Divider } from 'antd';
import { 
  WalletOutlined, 
  ClockCircleOutlined, 
  InfoCircleOutlined, 
  CheckCircleOutlined,
  ArrowRightOutlined,
  DollarOutlined,
  CreditCardOutlined
} from '@ant-design/icons';

const { Title, Paragraph, Text } = Typography;

const BookingPolicy = () => {
  
  const timePolicyColumns = [
    {
      title: 'Thời gian hủy phòng (Trước ngày check-in)',
      dataIndex: 'time',
      key: 'time',
      render: (text) => <span style={{ fontWeight: 600, color: '#262626' }}>{text}</span>,
    },
    {
      title: 'Mức hoàn trả tiền cọc',
      dataIndex: 'refundPercent',
      key: 'refundPercent',
      align: 'center',
      render: (percent) => {
        let color = 'red';
        if (percent === '100%') color = 'green';
        if (percent === '50%') color = 'orange';
        return (
          <Tag color={color} style={{ fontSize: '14px', padding: '4px 12px', borderRadius: '6px', fontWeight: '600' }}>
            {percent}
          </Tag>
        );
      },
    },
  ];

  const timePolicyData = [
    { key: '1', time: 'Dưới 3 ngày (< 3 ngày)', refundPercent: '0%' },
    { key: '2', time: 'Từ 3 đến 7 ngày (3 - 7 ngày)', refundPercent: '50%' },
    { key: '3', time: 'Trên 7 ngày (> 7 ngày)', refundPercent: '100%' },
  ];

  return (
    <div style={{ maxWidth: '1050px', margin: '0 auto', padding: '40px 20px' }}>
      {/* Inject CSS tạo hiệu ứng nhấc khối mượt mà khi hover */}
      <style>{`
        .policy-card {
          height: 100%;
          border-radius: 12px !important;
          overflow: hidden;
          transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
        }
        .policy-card:hover {
          transform: translateY(-5px);
          box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
        }
        .info-block {
          padding: 16px;
          border-radius: 8px;
          border-left: 4px solid #d9d9d9;
        }
        .info-block.vnpay { 
          border-left-color: #1890ff; 
          background: rgba(24, 144, 255, 0.04); 
        }
        .info-block.cash { 
          border-left-color: #faad14; 
          background: rgba(250, 173, 20, 0.04); 
        }
      `}</style>

      <Card 
        bordered={false} 
        style={{ 
          boxShadow: '0 4px 20px rgba(0,0,0,0.04)', 
          borderRadius: '16px',
          padding: '20px'
        }}
      >
        {/* Tiêu đề chính */}
        <div style={{ textAlign: 'center', marginBottom: '40px' }}>
          <Title level={2} style={{ color: '#002c6c', fontWeight: '700', marginBottom: '8px', letterSpacing: '0.5px' }}>
            QUY ĐỊNH ĐẶT PHÒNG & CHÍNH SÁCH HOÀN TIỀN
          </Title>
          <Text type="secondary" style={{ fontSize: '15px' }}>
            Thông tin minh bạch nhằm đảm bảo quyền lợi tối ưu cho quý khách tại <b>Lotus Hotel</b>
          </Text>
          <div style={{ width: '60px', height: '3px', background: '#1890ff', margin: '16px auto 0 auto', borderRadius: '2px' }}></div>
        </div>

        {/* 1. Quy định đặt phòng */}
        <section style={{ marginBottom: '40px' }}>
          <Title level={4} style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#002c6c' }}>
            <CheckCircleOutlined style={{ color: '#52c41a' }} /> 1. Quy định đặt cọc giữ phòng
          </Title>
          <Alert
            message={
              <span style={{ fontSize: '15px', lineHeight: '1.6', color: '#141414' }}>
                Để đảm bảo giữ phòng thành công trên hệ thống, quý khách vui lòng <b>thanh toán tối thiểu 40% tổng giá trị đơn đặt phòng</b> làm tiền đặt cọc.
              </span>
            }
            type="info"
            showIcon
            style={{ borderRadius: '10px', padding: '14px 20px', background: '#f0f5ff', borderColor: '#adc6ff' }}
          />
        </section>

        {/* 2. Thời gian và tỷ lệ hoàn tiền */}
        <section style={{ marginBottom: '40px' }}>
          <Title level={4} style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#002c6c' }}>
            <ClockCircleOutlined style={{ color: '#1890ff' }} /> 2. Mức hoàn tiền theo thời gian hủy đơn
          </Title>
          <Paragraph style={{ fontSize: '14px', color: '#595959', marginBottom: '16px' }}>
            Tỷ lệ hoàn trả tiền cọc giữ phòng được tính toán dựa trên khoảng thời gian quý khách gửi yêu cầu hủy thực tế so với giờ nhận phòng (Check-in) dự kiến:
          </Paragraph>
          <Table 
            columns={timePolicyColumns} 
            dataSource={timePolicyData} 
            pagination={false} 
            bordered 
            style={{ overflow: 'hidden', borderRadius: '8px' }}
          />
        </section>

        <Divider style={{ borderColor: '#f0f0f0', margin: '40px 0' }} />

        {/* 3. Quy trình xử lý tiền hoàn chi tiết - ĐÃ GOM THEO PHƯƠNG THỨC THANH TOÁN */}
        <section>
          <Title level={4} style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '24px', color: '#002c6c' }}>
            <WalletOutlined style={{ color: '#faad14' }} /> 3. Quy trình nhận lại tiền hoàn trả (Refund Workflow)
          </Title>
          
          <Row gutter={[24, 24]}>
            {/* Cột 1: Đơn VNPAY (Gom cực gọn) */}
            <Col xs={24} md={12}>
              <Card 
                className="policy-card"
                title={<span><CreditCardOutlined style={{ marginRight: '8px' }} /> ĐƠN THANH TOÁN QUA VNPAY</span>} 
                type="inner" 
                style={{ borderColor: '#d6e4ff' }} 
                headStyle={{ background: '#f0f5ff', color: '#003a8c', fontWeight: '700', fontSize: '15px' }}
              >
                <div className="info-block vnpay" style={{ height: 'calc(100% - 4px)', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                  <div style={{ marginBottom: '12px' }}>
                    <Tag color="blue" style={{ fontSize: '12px', fontWeight: '600', padding: '2px 8px' }}>HOÀN TIỀN TỰ ĐỘNG 100%</Tag>
                  </div>
                  <Paragraph style={{ color: '#333', fontSize: '14px', lineHeight: '1.7', margin: 0 }}>
                    Dù quý khách thực hiện <b>hủy lẻ một vài phòng</b> hay <b>hủy toàn bộ đơn hàng</b>, hệ thống sẽ 
                    <b> tự động chuyển trả lại số tiền hoàn cọc hợp lệ</b> vào thẳng tài khoản ngân hàng hoặc thẻ mà quý khách đã sử dụng để thanh toán trước đó. 
                    Quý khách không cần thực hiện thêm bất kỳ thủ tục thủ công nào.
                  </Paragraph>
                </div>
              </Card>
            </Col>

            {/* Cột 2: Đơn Tiền Mặt (Phân nhánh rõ ràng bên trong) */}
            <Col xs={24} md={12}>
              <Card 
                className="policy-card"
                title={<span><DollarOutlined style={{ marginRight: '8px' }} /> ĐƠN THANH TOÁN TIỀN MẶT (CASH)</span>} 
                type="inner" 
                style={{ borderColor: '#ffe8e6' }} 
                headStyle={{ background: '#fffbe6', color: '#d46b08', fontWeight: '700', fontSize: '15px' }}
              >
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  
                  {/* Nhánh 1: Hủy lẻ */}
                  <div className="info-block cash">
                    <div style={{ fontWeight: '700', marginBottom: '6px', color: '#262626', fontSize: '14px' }}>
                      <ArrowRightOutlined style={{ color: '#faad14', marginRight: '6px' }} /> Trường hợp Hủy lẻ phòng:
                    </div>
                    <Text style={{ display: 'block', color: '#434343', fontSize: '13.5px', lineHeight: '1.5', paddingLeft: '18px' }}>
                      Số tiền dư sau khi giảm bớt phòng sẽ được <b>tạm giữ trên hệ thống</b>. Tại thời điểm làm thủ tục trả phòng (Checkout), khách sạn sẽ thực hiện đối trừ hóa đơn và hoàn trả lại phần tiền thừa trực tiếp cho quý khách.
                    </Text>
                  </div>

                  {/* Nhánh 2: Hủy toàn bộ đơn */}
                  <div className="info-block cash">
                    <div style={{ fontWeight: '700', marginBottom: '6px', color: '#262626', fontSize: '14px' }}>
                      <ArrowRightOutlined style={{ color: '#faad14', marginRight: '6px' }} /> Trường hợp Hủy toàn bộ đơn:
                    </div>
                    <Text style={{ display: 'block', color: '#434343', fontSize: '13.5px', lineHeight: '1.5', paddingLeft: '18px', marginBottom: '6px' }}>
                      Quý khách được quyền tự do lựa chọn một trong hai phương thức để nhận lại tiền cọc:
                    </Text>
                    <ul style={{ paddingLeft: '36px', margin: 0, fontSize: '13.5px', color: '#595959', lineHeight: '1.8' }}>
                      <li><b>Nhận tiền mặt (CASH):</b> Vui lòng nhận trực tiếp tại quầy Lễ tân khách sạn.</li>
                      <li><b>Chuyển khoản (Banking):</b> Vui lòng liên hệ Hotline khách sạn để cung cấp số tài khoản chính chủ.</li>
                    </ul>
                  </div>

                </div>
              </Card>
            </Col>
          </Row>
        </section>

        {/* Hộp lưu ý chân trang */}
        <div style={{ 
          marginTop: '40px', 
          textAlign: 'center', 
          background: '#fafafa', 
          padding: '16px', 
          borderRadius: '10px',
          border: '1px dashed #d9d9d9'
        }}>
          <Text type="secondary" style={{ fontStyle: 'italic', fontSize: '13.5px' }}>
            <InfoCircleOutlined style={{ color: '#1890ff' }} /> Mọi thắc mắc hoặc cần hỗ trợ tra soát giao dịch hoàn tiền, xin vui lòng liên hệ đường dây nóng hiển thị phía dưới chân trang để tổng đài viên phục vụ kịp thời.
          </Text>
        </div>

      </Card>
    </div>
  );
};

export default BookingPolicy;