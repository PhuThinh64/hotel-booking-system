import React from 'react';
import {
  Modal,
  Card,
  Row,
  Col,
  Statistic,
  Tag,
  Button,
  Space,
  Typography,
  Tooltip,
  Table,
  Divider,
  Descriptions,
  Popconfirm,
  Alert,
  Empty
} from 'antd';

import {
  PlusOutlined,
  RollbackOutlined,
  UserOutlined,
  HomeOutlined,
  DollarOutlined,
  CalendarOutlined,
  PhoneOutlined
} from '@ant-design/icons';

import dayjs from 'dayjs';

import {
  getBookingNights,
  getServiceAmount,
  getRemainingAmount
} from '../utils/bookingHelpers';

const { Text, Title } = Typography;

const STATUS_CONFIG = {
  PENDING: {
    color: 'default',
    text: 'Chờ xác nhận'
  },
  PENDING_DEPOSIT: {
    color: 'gold',
    text: 'Chờ đặt cọc'
  },
  CONFIRMED: {
    color: 'blue',
    text: 'Đã xác nhận'
  },
  CHECKED_IN: {
    color: 'green',
    text: 'Đang lưu trú'
  },
  CHECKED_OUT: {
    color: 'purple',
    text: 'Đã trả phòng'
  },
  CANCELLED: {
    color: 'red',
    text: 'Đã hủy'
  },
  PENDING_REFUND: {
    color: 'orange',
    text: 'Chờ hoàn tiền'
  }
};

const formatMoney = (value) =>
  `${Number(value || 0).toLocaleString()}₫`;

const BookingDetailModal = ({
  open,
  onCancel,
  selectedBooking,
  onCheckIn,
  onConfirmDeposit,
  onCancelPrompt,
  onCheckoutPrompt,
  onOpenAddRoomModal,
  onOpenAssignRoom,
  onCancelRoomPrompt,
  onOpenChangeRoom,
  onOpenAddService,
  onOpenEditServiceModal,
  onCancelService,
}) => {

  if (!selectedBooking) {
    return (
      <Modal
        open={open}
        onCancel={onCancel}
        centered
        footer={[
          <Button key="close" onClick={onCancel}>
            Đóng
          </Button>
        ]}
      >
        Đang tải dữ liệu...
      </Modal>
    );
  }

  const {
    status,
    paymentMethod,
    id
  } = selectedBooking;

  const isBookingReadOnly = ['CANCELLED', 'CHECKED_OUT', 'PENDING_REFUND'].includes(status);

  const allRoomsAssigned =
    selectedBooking.bookingRooms?.every(
      room =>
        room.roomNumber &&
        room.roomNumber.trim() !== ''
    );

  const activeServiceAmount =
    getServiceAmount(selectedBooking);

  const roomAmount =
    Number(selectedBooking.roomAmount || 0);

  const surchargeAmount =
    Number(selectedBooking.surchargeAmount || 0);

  const totalInvoiceValue =
    roomAmount +
    activeServiceAmount +
    surchargeAmount;

  const depositAmount =
    Number(selectedBooking.depositAmount || 0);

  const refundAmount =
    Number(selectedBooking.refundAmount || 0);

  const remaining =
    getRemainingAmount(selectedBooking);

  const renderModalFooter = () => (
    <Space>
      <Button onClick={onCancel}>
        Đóng
      </Button>

      {status === 'PENDING_DEPOSIT' &&
        paymentMethod === 'CASH' && (
          <>
            <Button
              type="primary"
              onClick={() =>
                onConfirmDeposit(id)
              }
            >
              Xác nhận cọc
            </Button>

            <Button
              danger
              onClick={() =>
                onCancelPrompt()
              }
            >
              Hủy toàn bộ đơn
            </Button>
          </>
        )}

      {status === 'CONFIRMED' && (
        <>
          {allRoomsAssigned ? (
            <Button
              type="primary"
              onClick={() =>
                onCheckIn(id)
              }
            >
              Check-in
            </Button>
          ) : (
            <Tooltip title="Vui lòng xếp đủ phòng trước khi Check-in">
              <Button
                type="primary"
                disabled
              >
                Check-in
              </Button>
            </Tooltip>
          )}

          <Button
            danger
            onClick={() =>
              onCancelPrompt()
            }
          >
            Hủy toàn bộ đơn
          </Button>
        </>
      )}

      {status === 'CHECKED_IN' && (
        <Button
          type="primary"
          danger
          onClick={() =>
            onCheckoutPrompt(id)
          }
        >
          Check-out & Tính tiền
        </Button>
      )}
    </Space>
  );

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      footer={renderModalFooter()}
      width={1300}
      centered
      title={null}
      styles={{
        body: {
          maxHeight: '80vh',
          overflowY: 'auto'
        }
      }}
    >

      {/* HEADER */}

      <Card
        bordered={false}
        style={{
          marginBottom: 20,
          borderRadius: 16,
          boxShadow:
            '0 2px 12px rgba(0,0,0,0.06)'
        }}
      >
        <Row
          justify="space-between"
          align="middle"
        >
          <Col>
            <Space
              direction="vertical"
              size={2}
            >
              <Title
                level={3}
                style={{ margin: 0 }}
              >
                #{selectedBooking.bookingCode}
              </Title>

              <Text type="secondary">
                Tạo lúc{' '}
                {selectedBooking.createdAt
                  ? dayjs(
                      selectedBooking.createdAt
                    ).format(
                      'DD/MM/YYYY HH:mm'
                    )
                  : '--'}
              </Text>
            </Space>
          </Col>

          <Col>
            <Tag
              color={
                STATUS_CONFIG[status]
                  ?.color
              }
              style={{
                padding:
                  '8px 18px',
                borderRadius: 999,
                fontSize: 14,
                fontWeight: 600
              }}
            >
              {
                STATUS_CONFIG[
                  status
                ]?.text
              }
            </Tag>
          </Col>
        </Row>
      </Card>

      {/* KPI */}

      <Row
        gutter={[16, 16]}
        style={{
          marginBottom: 20
        }}
      >
        <Col xs={24} md={6}>
          <Card
            style={{
              borderRadius: 16
            }}
          >
            <Statistic
              title="Tiền phòng"
              value={roomAmount}
              formatter={(v) =>
                Number(
                  v
                ).toLocaleString()
              }
              suffix="₫"
              prefix={
                <HomeOutlined />
              }
            />
          </Card>
        </Col>

        <Col xs={24} md={6}>
          <Card
            style={{
              borderRadius: 16
            }}
          >
            <Statistic
              title="Dịch vụ"
              value={
                activeServiceAmount
              }
              formatter={(v) =>
                Number(
                  v
                ).toLocaleString()
              }
              suffix="₫"
              prefix={
                <DollarOutlined />
              }
            />
          </Card>
        </Col>

        <Col xs={24} md={6}>
          <Card
            style={{
              borderRadius: 16
            }}
          >
            <Statistic
              title="Đã cọc"
              value={
                depositAmount
              }
              formatter={(v) =>
                Number(
                  v
                ).toLocaleString()
              }
              suffix="₫"
              valueStyle={{
                color:
                  '#52c41a'
              }}
            />
          </Card>
        </Col>

        <Col xs={24} md={6}>
          <Card
            style={{
              borderRadius: 16
            }}
          >
            <Statistic
              title="Công nợ"
              value={Math.abs(
                remaining
              )}
              formatter={(v) =>
                Number(
                  v
                ).toLocaleString()
              }
              suffix="₫"
              valueStyle={{
                color:
                  remaining > 0
                    ? '#ff4d4f'
                    : '#52c41a'
              }}
            />
          </Card>
        </Col>
      </Row>

      {/* THÔNG TIN */}

      <Row
        gutter={[16, 16]}
        style={{
          marginBottom: 20
        }}
      >
        <Col xs={24} lg={12}>
          <Card
            title="Thông tin khách hàng"
            style={{
              borderRadius: 16
            }}
          >
            <Descriptions
              column={1}
              size="small"
            >
              <Descriptions.Item label="Khách hàng">
                {selectedBooking.customerName}
              </Descriptions.Item>

              <Descriptions.Item label="SĐT">
                {selectedBooking.customerPhone}
              </Descriptions.Item>

              <Descriptions.Item label="Người liên hệ">
                {selectedBooking.contactName}
              </Descriptions.Item>

              <Descriptions.Item label="SĐT liên hệ">
                {selectedBooking.contactPhone}
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card
            title="Thông tin lưu trú"
            style={{
              borderRadius: 16
            }}
          >
          <Descriptions
            column={1}
            size="small"
          >
        <Descriptions.Item label="Dự kiến nhận phòng">
          {selectedBooking.arrivalDate
            ? dayjs(
                selectedBooking.arrivalDate
              ).format(
                'DD/MM/YYYY HH:mm'
              )
            : '--'}
        </Descriptions.Item>

        <Descriptions.Item label="Dự kiến trả phòng">
          {selectedBooking.departureDate
            ? dayjs(
                selectedBooking.departureDate
              ).format(
                'DD/MM/YYYY HH:mm'
              )
            : '--'}
        </Descriptions.Item>

        <Descriptions.Item label="Check-in thực tế">
          {selectedBooking.actualCheckIn ? (
            <Tag color="success">
              {dayjs(
                selectedBooking.actualCheckIn
              ).format(
                'DD/MM/YYYY HH:mm'
              )}
            </Tag>
          ) : (
            <Text type="secondary">
              Chưa check-in
            </Text>
          )}
        </Descriptions.Item>

        <Descriptions.Item label="Check-out thực tế">
          {selectedBooking.actualCheckOut ? (
            <Tag color="processing">
              {dayjs(
                selectedBooking.actualCheckOut
              ).format(
                'DD/MM/YYYY HH:mm'
              )}
            </Tag>
          ) : (
            <Text type="secondary">
              Chưa check-out
            </Text>
          )}
        </Descriptions.Item>

        <Descriptions.Item label="Số đêm">
          {getBookingNights(
            selectedBooking
          )}{' '}
          đêm
        </Descriptions.Item>

        <Descriptions.Item label="Phương thức TT">
          {paymentMethod}
        </Descriptions.Item>
      </Descriptions>
          </Card>
        </Col>
      </Row>

      {/* PHÒNG */}

      <Card
        title="Danh sách phòng"
        extra={
          (status === 'CONFIRMED' || status === 'PENDING_DEPOSIT') && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={onOpenAddRoomModal}
            >
              Thêm phòng
            </Button>
          )
        }
        style={{
          borderRadius: 16,
          marginBottom: 20,
        }}
      >
        {!selectedBooking.bookingRooms?.length ? (
          <Empty />
        ) : (
          <Row gutter={[16, 16]}>
            {selectedBooking.bookingRooms.map((room) => {
              
              const isCancelled = room.status === 'CANCELLED';

              return (
                <Col xs={24} md={12} xl={8} key={room.bookingRoomId}>
                  <Card
                    size="small"
                    style={{
                      borderRadius: 14,
                      
                      backgroundColor: isCancelled ? '#f5f5f5' : '#ffffff',
                      borderColor: isCancelled ? '#d9d9d9' : '#f0f0f0',
                      cursor: isCancelled ? 'not-allowed' : 'default',
                    }}
                  >
                    <Space
                      direction="vertical"
                      style={{
                        width: '100%',
                        
                        pointerEvents: isCancelled ? 'none' : 'auto',
                      }}
                    >
                      <div>
                        {/* 4. Thêm thuộc tính disabled={isCancelled} cho tất cả các Text để nó mờ đi */}
                        <Text type="secondary" disabled={isCancelled}>
                          Loại phòng
                        </Text>
                        <br />
                        <Text strong disabled={isCancelled}>
                          {room.roomType}
                        </Text>
                      </div>

                      <div>
                        <Text type="secondary" disabled={isCancelled}>
                          Số phòng
                        </Text>
                        <br />
                        <Text strong disabled={isCancelled}>
                          {room.roomNumber || 'Chưa xếp'}
                        </Text>
                      </div>

                      <div>
                        <Text type="secondary" disabled={isCancelled}>
                          Đơn giá
                        </Text>
                        <br />
                        <Text strong disabled={isCancelled}>
                          {formatMoney(room.priceAtOrder)}
                        </Text>
                      </div>

                      <Space wrap>
                        <Button
                          size="small"
                          onClick={() => onOpenAssignRoom(room)}
                          disabled={isCancelled || isBookingReadOnly}
                        >
                          {room.roomNumber ? 'Đổi phòng' : 'Xếp phòng'}
                        </Button>

                        {!isCancelled &&
                          (selectedBooking.status === 'PENDING' ||
                            selectedBooking.status === 'CONFIRMED') && (
                            <Button
                              danger
                              size="small"
                              onClick={() => onCancelRoomPrompt(room)}
                            >
                              Hủy phòng
                            </Button>
                          )}

                        <Button
                          size="small"
                          danger
                          type="dashed"
                          icon={<RollbackOutlined />}
                          onClick={() => onOpenChangeRoom(room)}
                          disabled={
                            isCancelled ||
                            ['CANCELLED', 'CHECKED_OUT', 'PENDING_REFUND'].includes(
                              selectedBooking.status
                            ) || isBookingReadOnly
                          }
                        >
                          Đổi loại
                        </Button>

                        {isCancelled && (
                          <Tag color="error">Đã hủy</Tag>
                        )}
                      </Space>
                    </Space>
                  </Card>
                </Col>
              );
            })}
          </Row>
        )}
      </Card>

      {/* DỊCH VỤ */}

      <Card
        title="Dịch vụ sử dụng"
        extra={
          (status ===
            'CHECKED_IN' ||
            status ===
              'CONFIRMED') && (
            <Button
              type="primary"
              icon={
                <PlusOutlined />
              }
              onClick={
                onOpenAddService
              }
            >
              Thêm dịch vụ
            </Button>
          )
        }
        style={{
          borderRadius: 16,
          marginBottom: 20
        }}
      >
        <Table
          dataSource={
            selectedBooking.bookingServices ||
            []
          }
          pagination={false}
          size="middle"
          rowClassName={(record) => record.status === 'CANCELLED' ? 'row-disabled-custom' : ''}
          rowKey={(
            record,
            index
          ) =>
            record.serviceId ??
            record.id ??
            index
          }
          columns={[
            {
              title:
                'Tên dịch vụ',
              dataIndex:
                'serviceName'
            },
            {
              title:
                'Số lượng',
              dataIndex:
                'quantity',
              align: 'center'
            },
            {
              title:
                'Đơn giá',
              dataIndex:
                'priceAtOrder',
              render: (
                value
              ) =>
                formatMoney(
                  value
                )
            },
            {
              title:
                'Thành tiền',
              render: (
                _
                ,
                record
              ) =>
                formatMoney(
                  Number(
                    record.quantity ||
                      0
                  ) *
                    Number(
                      record.priceAtOrder ||
                        0
                    )
                )
            },
            {
              title:
                'Hành động',
              width: 180,
              render: (
                _
                ,
                record
              ) => {
                if (
                  record.status ===
                  'CANCELLED'
                ) {
                  return (
                    <Tag color="error">
                      Đã hủy
                    </Tag>
                  );
                }

                return (
                  <Space>
                    <Button
                      type="link"
                      onClick={() =>
                        onOpenEditServiceModal(
                          record
                        )
                      }
                      disabled={isBookingReadOnly}
                    >
                      Sửa
                    </Button>

                    <Popconfirm
                      title="Hủy dịch vụ"
                      description="Bạn có chắc muốn hủy?"
                      okText="Có"
                      cancelText="Không"
                      okButtonProps={{
                        danger: true
                      }}
                      onConfirm={() =>
                        onCancelService(
                          record
                        )
                      }
                      disabled={isBookingReadOnly}
                    >
                      <Button
                        type="link"
                        danger
                        disabled={isBookingReadOnly}
                      >
                        Hủy
                      </Button>
                    </Popconfirm>
                  </Space>
                );
              }
            }
          ]}
        />
      </Card>

      {/* HÓA ĐƠN */}

      <Card
        title="Tổng hợp hóa đơn"
        style={{
          borderRadius: 16
        }}
      >
        <Row justify="space-between">
          <Col>
            Tổng tiền phòng
          </Col>
          <Col>
            {formatMoney(
              roomAmount
            )}
          </Col>
        </Row>

        <Divider />

        <Row justify="space-between">
          <Col>
            Tổng tiền dịch vụ
          </Col>
          <Col>
            {formatMoney(
              activeServiceAmount
            )}
          </Col>
        </Row>

        <Divider />

        <Row justify="space-between">
          <Col>
            Phụ thu
          </Col>
          <Col>
            {formatMoney(
              surchargeAmount
            )}
          </Col>
        </Row>

        <Divider />

        <Row justify="space-between">
          <Col>
            <Text strong>
              Tổng giá trị hóa
              đơn
            </Text>
          </Col>

          <Col>
            <Text
              strong
              style={{
                fontSize: 20,
                color:
                  '#fa8c16'
              }}
            >
              {formatMoney(
                totalInvoiceValue
              )}
            </Text>
          </Col>
        </Row>

        <Divider />

        <Row justify="space-between">
          <Col>
            Đã đặt cọc
          </Col>

          <Col
            style={{
              color:
                '#52c41a'
            }}
          >
            -
            {formatMoney(
              depositAmount
            )}
          </Col>
        </Row>

        {refundAmount >
          0 && (
          <>
            <Divider />

            <Row justify="space-between">
              <Col>
                Đã hoàn
              </Col>

              <Col>
                {formatMoney(
                  refundAmount
                )}
              </Col>
            </Row>
          </>
        )}

        <Divider />

       <Divider />

        <Row justify="space-between">
          <Col>
            <Text strong style={{ fontSize: 16 }}>
              {status === 'CANCELLED' 
                ? 'Số dư sau hoàn tiền' 
                : (remaining < 0 ? 'Tiền dư (Hoàn khách)' : 'Còn phải thanh toán')
              }
            </Text>
          </Col>
          <Col>
            <Text
              strong
              style={{
                fontSize: 22,
                
                color: remaining < 0 ? '#52c41a' : (remaining > 0 ? '#ff4d4f' : '#1677ff')
              }}
            >
              {/* Hiển thị: Nếu < 0 thì dùng Math.abs để lấy số dương hiển thị cho đẹp */}
              {remaining === 0 
                ? '0₫ (Đã thanh toán đủ)' 
                : formatMoney(Math.abs(remaining))
              }
            </Text>
          </Col>
        </Row>
      </Card>
    </Modal>
  );
};

export default BookingDetailModal;