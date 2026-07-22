import React, { useState, useEffect, useCallback } from 'react';
import { Card, Button, message, Space, Input, Select, DatePicker, Flex, Typography } from 'antd';
import { WalletOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import * as ApiService from '../../../api/ApiService';

import BookingStatistics from './components/BookingStatistics';
import BookingTable from './components/BookingTable';
import BookingDetailModal from './components/BookingDetailModal';
import CheckoutModal from './components/CheckoutModal';
import AssignRoomModal from './components/AssignRoomModal';
import AddRoomModal from './components/AddRoomModal';
import ChangeRoomModal from './components/ChangeRoomModal';
import AddServiceModal from './components/AddServiceModal';
import EditServiceModal from './components/EditServiceModal';
import CancelBookingModal from './components/CancelBookingModal';
import CancelRoomModal from './components/CancelRoomModal';
import VnpayModal from './components/VnpayModal';
import InvoiceTemplate from './components/InvoiceTemplate';

import useBookingActions from './hooks/useBookingActions';
import useVnpayListener from './hooks/useVnpayListener';

const { Title, Text } = Typography;

const BookingPage = () => {
  const navigate = useNavigate();

  // Core List State
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);

  // Selected Item / Detail Modal State
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Check-out State
  const [isCheckoutModalOpen, setIsCheckoutModalOpen] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [vnpayQrUrl, setVnpayQrUrl] = useState(null);
  const [isVnpayModalOpen, setIsVnpayModalOpen] = useState(false);

  // Assign Room State
  const [isAssignRoomModalOpen, setIsAssignRoomModalOpen] = useState(false);
  const [availableRoomsForAssign, setAvailableRoomsForAssign] = useState([]);
  const [selectedRoomIdToAssign, setSelectedRoomIdToAssign] = useState(null);
  const [selectedBookingRoomIdToAssign, setSelectedBookingRoomIdToAssign] = useState(null);

  // Add Room State
  const [isAddRoomModalOpen, setIsAddRoomModalOpen] = useState(false);
  const [availableRoomTypes, setAvailableRoomTypes] = useState([]);
  const [selectedRoomTypeToAdd, setSelectedRoomTypeToAdd] = useState(null);

  // Change Room Type State
  const [isChangeRoomModalOpen, setIsChangeRoomModalOpen] = useState(false);
  const [currentBookingRoomId, setCurrentBookingRoomId] = useState(null);
  const [roomTypesList, setRoomTypesList] = useState([]);
  const [selectedRoomTypeIdToChange, setSelectedRoomTypeIdToChange] = useState(null);

  // Service Management State
  const [isAddServiceModalOpen, setIsAddServiceModalOpen] = useState(false);
  const [availableServices, setAvailableServices] = useState([]);
  const [selectedServiceId, setSelectedServiceId] = useState(null);
  const [quantityToAdd, setQuantityToAdd] = useState(1);

  const [isEditServiceModalOpen, setIsEditServiceModalOpen] = useState(false);
  const [editingService, setEditingService] = useState(null);
  const [newQuantity, setNewQuantity] = useState(1);

  // Cancel Booking / Room State
  const [isCancelModalOpen, setIsCancelModalOpen] = useState(false);
  const [cancelReason, setCancelReason] = useState('');
  const [refundMethod, setRefundMethod] = useState('CASH');

  const [isCancelRoomModalOpen, setIsCancelRoomModalOpen] = useState(false);
  const [cancelingRoomRecord, setCancelingRoomRecord] = useState(null);


  // Filter 
  const [filters, setFilters] = useState({
    bookingCode: '',
    status: null,
    dateRange: null // [dayjs, dayjs]
  });


  // 1. Fetch bookings callback
  const fetchBookings = useCallback(async (page = 0) => {
    setLoading(true);
    try {
      // Xử lý định dạng ngày tháng trước khi gửi
      const startDate = filters.dateRange ? filters.dateRange[0].format('YYYY-MM-DDTHH:mm:ss') : null;
      const endDate = filters.dateRange ? filters.dateRange[1].format('YYYY-MM-DDTHH:mm:ss') : null;

      // Gọi API với bộ lọc
      const response = await ApiService.getAllBookings({
        page: page, 
        size: 10,
        code: filters.bookingCode,
        status: filters.status,
        start: startDate,
        end: endDate
      });
      
      const content = response?.content || response?.result?.content || [];
      const total = response?.totalElements || response?.result?.totalElements || 0;

      setBookings(content);
      setTotalElements(total);
      setCurrentPage(page);
    } catch (err) {
      console.error('Lỗi fetch:', err);
      message.error('Lỗi tải danh sách: ' + (err.message || 'Vui lòng kiểm tra lại'));
    } finally {
      setLoading(false);
    }
  }, [filters]);

  // 2. Initial load
  useEffect(() => {
    fetchBookings(0);
  }, [fetchBookings]);

  useEffect(() => {
    const handleBeforePrint = () => {
      document.title = "Lotus_Hotel_Invoice"; 
    };
    const handleAfterPrint = () => {
      document.title = "hotel-booking-ui"; 
    };

    window.addEventListener('beforeprint', handleBeforePrint);
    window.addEventListener('afterprint', handleAfterPrint);
    
    return () => {
      window.removeEventListener('beforeprint', handleBeforePrint);
      window.removeEventListener('afterprint', handleAfterPrint);
    };
  }, []);

  const handleOpenDetail = useCallback(async (id) => {
    try {
      const res = await ApiService.getBookingById(id);
      setSelectedBooking(res.result ?? res);
      setIsModalOpen(true);
    } catch (err) {
      message.error('Không thể tải chi tiết đơn');
    }
  }, []);

  const handleCheckIn = useCallback(async (id) => {
    try {
      await ApiService.checkIn(id);
      message.success('Check-in thành công');
      fetchBookings(currentPage);

      if (selectedBooking?.id === id) {
        handleOpenDetail(id);
      }
    } catch (err) {
      message.error(err.response?.data?.message || 'Check-in thất bại');
    }
  }, [fetchBookings, currentPage, selectedBooking, handleOpenDetail]);

  // 3. Instantiate hooks
  const actions = useBookingActions({
    selectedBooking,
    setSelectedBooking,
    currentPage,
    paymentMethod,
    cancelReason,
    refundMethod,
    cancelingRoomRecord,
    setCancelingRoomRecord,
    newQuantity,
    editingService,
    fetchBookings,
    setLoading,
    setIsModalOpen,
    setIsCheckoutModalOpen,
    setIsVnpayModalOpen,
    setIsCancelModalOpen,
    setIsCancelRoomModalOpen,
    setIsAssignRoomModalOpen,
    setIsAddRoomModalOpen,
    setIsChangeRoomModalOpen,
    setIsAddServiceModalOpen,
    setIsEditServiceModalOpen,
    setAvailableRoomsForAssign,
    setSelectedBookingRoomIdToAssign,
    setAvailableRoomTypes,
    setRoomTypesList,
    setCurrentBookingRoomId,
    setSelectedRoomTypeIdToChange,
    setAvailableServices,
    setSelectedServiceId,
    setQuantityToAdd,
    setSelectedRoomTypeToAdd,
    setVnpayQrUrl,
    selectedBookingRoomIdToAssign,
    selectedRoomIdToAssign,
    currentBookingRoomId,
    selectedRoomTypeIdToChange,
    selectedRoomTypeToAdd,
    selectedServiceId,
    quantityToAdd,
    handleOpenDetail,
    handleCheckIn,
    setRefundMethod,
    setPaymentMethod
  });

  // VNPay payment success Broadcast Channel listener
  useVnpayListener(
    useCallback(() => {
      setIsVnpayModalOpen(false);
      setIsCheckoutModalOpen(false);
      setIsModalOpen(false);
      fetchBookings(currentPage);
    }, [currentPage, fetchBookings])
  );

  // Local helper UI handlers
  const handleOpenEditModal = (service) => {
    setEditingService(service);
    setNewQuantity(service.quantity);
    setIsEditServiceModalOpen(true);
  };

  const handleCancelPromptLocal = () => {
    actions.handleCancelPrompt(setRefundMethod);
  };

  const handleCancelRoomPromptLocal = (roomRecord) => {
    actions.handleCancelRoomPrompt(roomRecord, setRefundMethod, setCancelReason);
  };

  return (
    <div style={{ padding: '24px', background: '#f5f7fa', minHeight: '100vh' }}>
      
      {/* 🌟 ĐÃ THÊM: Khu vực Tiêu đề trang và nút Quản lý hoàn tiền đẩy lên đầu trang độc lập */}
      <Flex justify="space-between" align="center" style={{ marginBottom: '24px' }} wrap="wrap" gap="middle">
        <div>
          <Title level={3} style={{ margin: 0, fontWeight: 700, color: '#1f1f1f' }}>
            Quản lý Giao dịch Đặt phòng
          </Title>
          <Text type="secondary" style={{ fontSize: '14px' }}>
            Hệ thống quản lý trạng thái đặt phòng, xử lý nhận phòng (Check-in), trả phòng (Check-out) và kiểm soát hoàn tiền cọc.
          </Text>
        </div>

        <Button
          type="primary"
          icon={<WalletOutlined />}
          onClick={() => navigate('/admin/refund-management')}
          style={{ 
              height: 40, 
              padding: '0 20px',
              fontWeight: 600, 
              borderRadius: 6,
              boxShadow: '0 2px 4px rgba(22, 119, 255, 0.15)'
          }}
        >
          Quản lý hoàn tiền
        </Button>
      </Flex>

      {/* Các khối thống kê tổng đài đặt phòng */}
      <BookingStatistics totalElements={totalElements} bookings={bookings} />

      {/* Khối quản trị dữ liệu chính */}
      <Card bordered={false} style={{ borderRadius: 12, boxShadow: '0 2px 8px rgba(0,0,0,0.04)', marginTop: 24 }}>
        
        {/* Thanh công cụ tìm kiếm và bộ lọc nâng cao */}
        <Card size="small" style={{ marginBottom: 20, background: '#fafafa', border: '1px solid #f0f0f0', borderRadius: 8 }}>
          <Space wrap>
            <Input 
              placeholder="Tìm mã đơn" 
              allowClear 
              value={filters.bookingCode}
              onChange={(e) => setFilters(prev => ({...prev, bookingCode: e.target.value}))} 
              style={{ width: 180, height: 36, borderRadius: 6 }}
            />
            <Select
              placeholder="Trạng thái"
              allowClear
              value={filters.status}
              style={{ width: 160, height: 36 }}
              onChange={(val) => setFilters(prev => ({...prev, status: val}))}
              options={[
                { value: 'CONFIRMED', label: 'Xác nhận' },
                { value: 'CHECKED_IN', label: 'Đang lưu trú' },
                { value: 'CHECKED_OUT', label: 'Đã trả phòng' },
                { value: 'CANCELLED', label: 'Đã hủy' },
                { value: 'PENDING_REFUND', label: 'Chờ hoàn tiền'}
              ]}
            />
            <DatePicker.RangePicker 
              style={{ height: 36, borderRadius: 6 }}
              value={filters.dateRange}
              onChange={(val) => setFilters(prev => ({...prev, dateRange: val}))} 
            />
            <Button type="primary" onClick={() => {
              setFilters({ bookingCode: '', status: null, dateRange: null });
              fetchBookings(0);
            }} style={{ height: 36, width: 70 }}>
              Reset
            </Button>
          </Space>
        </Card>

        {/* Bảng hiển thị thông tin đặt phòng */}
        <BookingTable
          bookings={bookings}
          loading={loading}
          totalElements={totalElements}
          currentPage={currentPage}
          onPageChange={fetchBookings}
          onOpenDetail={actions.handleOpenDetail}
          onCheckIn={actions.handleCheckIn}
          onCheckOutPrompt={actions.handleCheckOutPrompt}
          onPrintInvoice={actions.handlePrintInvoice}
        />
      </Card>

      {/* Hệ thống Modals điều hướng tác vụ giữ nguyên */}
      <BookingDetailModal
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        selectedBooking={selectedBooking}
        onCheckIn={actions.handleCheckIn}
        onConfirmDeposit={actions.handleConfirmDeposit}
        onCancelPrompt={handleCancelPromptLocal}
        onCheckoutPrompt={actions.handleCheckOutPrompt}
        onOpenAddRoomModal={actions.handleOpenAddRoom}
        onOpenAssignRoom={actions.handleOpenAssignRoom}
        onCancelRoomPrompt={handleCancelRoomPromptLocal}
        onOpenChangeRoom={actions.handleOpenChangeRoom}
        onOpenAddService={actions.handleOpenAddService}
        onOpenEditServiceModal={handleOpenEditModal}
        onCancelService={actions.handleCancelService}
      />

      <CheckoutModal
        open={isCheckoutModalOpen}
        onCancel={() => setIsCheckoutModalOpen(false)}
        selectedBooking={selectedBooking}
        paymentMethod={paymentMethod}
        onChangePaymentMethod={setPaymentMethod}
        refundMethod={refundMethod}
        onChangeRefundMethod={setRefundMethod}
        onConfirmCheckout={actions.handleConfirmCheckOut}
      />

      <AssignRoomModal
        open={isAssignRoomModalOpen}
        onCancel={() => setIsAssignRoomModalOpen(false)}
        selectedBooking={selectedBooking}
        selectedBookingRoomIdToAssign={selectedBookingRoomIdToAssign}
        availableRoomsForAssign={availableRoomsForAssign}
        selectedRoomIdToAssign={selectedRoomIdToAssign}
        onChangeSelectedRoomId={setSelectedRoomIdToAssign}
        onConfirm={actions.handleConfirmAssignRoom}
      />

      <AddRoomModal
        open={isAddRoomModalOpen}
        onCancel={() => setIsAddRoomModalOpen(false)}
        availableRoomTypes={availableRoomTypes}
        selectedRoomTypeToAdd={selectedRoomTypeToAdd}
        onChangeSelectedRoomType={setSelectedRoomTypeToAdd}
        onConfirm={actions.handleConfirmAddRoom}
      />

      <ChangeRoomModal
        open={isChangeRoomModalOpen}
        onCancel={() => setIsChangeRoomModalOpen(false)}
        roomTypesList={roomTypesList}
        selectedRoomTypeIdToChange={selectedRoomTypeIdToChange}
        onChangeSelectedRoomTypeId={setSelectedRoomTypeIdToChange}
        onConfirm={actions.handleConfirmChangeRoom}
      />

      <AddServiceModal
        open={isAddServiceModalOpen}
        onCancel={() => setIsAddServiceModalOpen(false)}
        availableServices={availableServices}
        selectedServiceId={selectedServiceId}
        onChangeSelectedServiceId={setSelectedServiceId}
        quantityToAdd={quantityToAdd}
        onChangeQuantityToAdd={setQuantityToAdd}
        onConfirm={actions.handleConfirmAddService}
      />

      <EditServiceModal
        open={isEditServiceModalOpen}
        onCancel={() => setIsEditServiceModalOpen(false)}
        newQuantity={newQuantity}
        onChangeNewQuantity={setNewQuantity}
        onConfirm={actions.handleUpdateQuantity}
      />

      <CancelBookingModal
        open={isCancelModalOpen}
        onCancel={() => setIsCancelModalOpen(false)}
        selectedBooking={selectedBooking}
        cancelReason={cancelReason}
        onChangeCancelReason={setCancelReason}
        refundMethod={refundMethod}
        onChangeRefundMethod={setRefundMethod}
        onSubmit={actions.submitCancelBooking}
        loading={loading}
      />

      <CancelRoomModal
        open={isCancelRoomModalOpen}
        onCancel={() => setIsCancelRoomModalOpen(false)}
        selectedBooking={selectedBooking}
        cancelingRoomRecord={cancelingRoomRecord}
        cancelReason={cancelReason}
        onChangeCancelReason={setCancelReason}
        refundMethod={refundMethod}
        onChangeRefundMethod={setRefundMethod}
        onSubmit={actions.submitCancelRoom}
        loading={loading}
      />

      <VnpayModal open={isVnpayModalOpen} onCancel={() => setIsVnpayModalOpen(false)} />
      
      <InvoiceTemplate invoiceData={selectedBooking} />

      <style>{`
        .ant-table-thead > tr > th {
          background: #f8f9fa !important;
          font-weight: 600 !important;
        }
        .ant-select-selector, .ant-input-affine-wrapper, .ant-picker, .ant-btn {
          border-radius: 6px !important;
        }
      `}</style>
    </div>
  );
};

export default BookingPage;