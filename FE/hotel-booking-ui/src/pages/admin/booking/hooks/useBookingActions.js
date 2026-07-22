import { useCallback } from 'react';
import { message } from 'antd';
import * as ApiService from '../../../../api/ApiService';

const useBookingActions = (deps) => {
  const {
    selectedBooking, setSelectedBooking,cancelingRoomRecord,
    currentPage, paymentMethod, cancelReason, refundMethod,
    setCancelingRoomRecord, editingService, newQuantity,
    fetchBookings, setLoading,
    setIsModalOpen, setIsCheckoutModalOpen, setIsVnpayModalOpen,
    setIsCancelModalOpen, setIsCancelRoomModalOpen,
    setIsAssignRoomModalOpen, setIsAddRoomModalOpen,
    setIsChangeRoomModalOpen, setIsAddServiceModalOpen, setIsEditServiceModalOpen,
    setAvailableRoomsForAssign, setSelectedBookingRoomIdToAssign,
    setAvailableRoomTypes, setRoomTypesList, setCurrentBookingRoomId,
    setSelectedRoomTypeIdToChange, setAvailableServices,
    setSelectedServiceId, setQuantityToAdd, setSelectedRoomTypeToAdd,
    setVnpayQrUrl,selectedRoomTypeToAdd, 
    selectedRoomIdToAssign, selectedBookingRoomIdToAssign,
    currentBookingRoomId, selectedRoomTypeIdToChange,
    selectedServiceId, quantityToAdd,
    handleOpenDetail, handleCheckIn,
    setRefundMethod, setPaymentMethod
  } = deps;

  // ── Thao tác in hóa đơn nhanh ở Frontend ───────────────────
  const handlePrintInvoice = useCallback(async (id) => {
    try {
      setLoading(true);
      // Gọi API lấy dữ liệu chi tiết của đơn đặt phòng
      const bookingRes = await ApiService.getBookingById(id);
      const bookingDetail = bookingRes?.result ?? bookingRes;
      
      // Đồng thời lấy dữ liệu tính toán hóa đơn sau cùng của phòng (gồm phụ phí, cọc, dịch vụ)
      const previewRes = await ApiService.getCheckoutPreview(id);
      const previewData = previewRes?.result ?? previewRes ?? {};

      const mergedInvoiceData = {
        ...bookingDetail,
        ...previewData
      };

      // 1. Lưu dữ liệu đã gộp vào state dùng chung để InvoiceTemplate nhận được
      setSelectedBooking(mergedInvoiceData);

      // 2. Chờ DOM render xong component hóa đơn ẩn (100ms) rồi gọi lệnh in toàn màn hình
      setTimeout(() => {
        window.print();
      }, 150);

    } catch (err) {
      console.error("Lỗi khi chuẩn bị hóa đơn:", err);
      message.error("Không thể lấy thông tin hóa đơn để in!");
    } finally {
      setLoading(false);
    }
  }, [setLoading, setSelectedBooking]);

  // ── Mở Modal Check-out ──────────────────────────────────
 const handleCheckOutPrompt = useCallback(async (id) => {
    try {
      setLoading(true);
      const bookingRes = await ApiService.getBookingById(id);
      const bookingDetail = bookingRes?.result ?? bookingRes;
      const previewRes = await ApiService.getCheckoutPreview(id);
      const previewData = previewRes?.result ?? previewRes ?? {};

      if (previewData.originalVnpay) {
        setRefundMethod(null);
      } else {
          setRefundMethod('CASH');
      }

      setPaymentMethod('CASH');

      const finalSurcharge = previewData.surchargeAmount ?? bookingDetail.surchargeAmount ?? 0;

     const mergedData = {
        ...bookingDetail,
        ...previewData,
        surchargeAmount: finalSurcharge
      };

      setSelectedBooking(mergedData);
      setIsCheckoutModalOpen(true);
    } catch (err) {
      console.error("Lỗi khi load preview:", err);
      message.error('Lỗi tải thông tin thanh toán!');
    } finally {
      setLoading(false);
    }
  }, [setLoading, setSelectedBooking, setIsCheckoutModalOpen]);

  // ── Xác nhận Check-out ────────────────────────────────────
  const handleConfirmCheckOut = useCallback(async () => {
    if (!selectedBooking) return;
    const bookingId = selectedBooking.bookingId || selectedBooking.id;
    
    // SAFETY NET: Ép finalRefundMethod = null nếu là VNPAY
    const isOriginalVnpay = selectedBooking.originalVnpay === true;
    const finalRefundMethod = isOriginalVnpay ? null : refundMethod;
    
    try {
      const res = await ApiService.checkOut(bookingId, paymentMethod, finalRefundMethod);

      const paymentUrl =
        res?.result?.paymentUrl ||
        res?.paymentUrl;

      // Chỉ mở popup nếu BE thực sự trả về link VNPay
      if (paymentUrl) {
        window.open(paymentUrl, '_blank');

        setVnpayQrUrl(paymentUrl);
        setIsVnpayModalOpen(true);
        setIsCheckoutModalOpen(false);

        return;
      }

      // Không có paymentUrl => checkout đã xử lý xong
      message.success('Check-out thành công!');

      setIsCheckoutModalOpen(false);
      setIsModalOpen(false);

      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Lỗi khi xử lý Check-out');
    }
  }, [
    selectedBooking, paymentMethod, refundMethod, setVnpayQrUrl,
    setIsVnpayModalOpen, setIsCheckoutModalOpen, setIsModalOpen,
    fetchBookings, currentPage,
  ]);

  // ── Xác nhận cọc tiền mặt ─────────────────────────────────────────────────
  const handleConfirmDeposit = useCallback(async (id) => {
    try {
      await ApiService.confirmDeposit(id, { method: 'CASH' });
      message.success('Xác nhận cọc tiền mặt thành công!');
      setIsModalOpen(false);
      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Lỗi xác nhận cọc');
    }
  }, [setIsModalOpen, fetchBookings, currentPage]);

  // ── Mở Modal hủy toàn bộ đơn ─────────────────────────────────────────────
  const handleCancelPrompt = useCallback((setRefundMethod) => {
    if (!selectedBooking) return;
    if (selectedBooking.depositAmount > 0 && selectedBooking.paymentMethod !== 'VNPAY') {
      setRefundMethod('CASH');
    } else {
      setRefundMethod(null);
    }
    setIsCancelModalOpen(true);
  }, [selectedBooking, setIsCancelModalOpen]);

  // ── Thực hiện hủy toàn bộ đơn ────────────────────────────────────────────
  const submitCancelBooking = useCallback(async () => {
    if (!cancelReason.trim()) {
      message.warning('Vui lòng nhập lý do hủy đơn!');
      return;
    }
    try {
      setLoading(true);
      const refund =
        selectedBooking.depositAmount > 0 && selectedBooking.paymentMethod !== 'VNPAY'
          ? refundMethod
          : null;
      await ApiService.cancelFullBooking(selectedBooking.id, refund, cancelReason);
      message.success('Hủy đơn thành công');
      setIsCancelModalOpen(false);
      setIsModalOpen(false);
      setSelectedBooking(null);
      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Hủy đơn thất bại');
    } finally {
      setLoading(false);
    }
  }, [
    cancelReason, refundMethod, selectedBooking,
    setLoading, setIsCancelModalOpen, setIsModalOpen,
    setSelectedBooking, fetchBookings, currentPage,
  ]);

  // ── Mở Modal hủy 1 phòng lẻ ──────────────────────────────────────────────
  const handleCancelRoomPrompt = useCallback((roomRecord, setRefundMethod, setCancelReason) => {
    const activeRooms = selectedBooking?.bookingRooms?.filter((r) => r.status !== 'CANCELLED') || [];
    const isLastRoom =
      activeRooms.length === 1 &&
      activeRooms[0].bookingRoomId === roomRecord.bookingRoomId;
    const isManualBooking = selectedBooking?.paymentMethod !== 'VNPAY';
    const hasDeposit = Number(selectedBooking?.depositAmount || 0) > 0;

    if (isLastRoom && isManualBooking && hasDeposit) {
      setRefundMethod('CASH');
    } else {
      setRefundMethod(null);
    }
    setCancelReason('');
    setCancelingRoomRecord(roomRecord);
    setIsCancelRoomModalOpen(true);
  }, [selectedBooking, setIsCancelRoomModalOpen]);

  // ── Thực hiện hủy 1 phòng lẻ ─────────────────────────────────────────────
  const submitCancelRoom = useCallback(async () => {
    if (!cancelReason.trim()) {
      message.warning('Vui lòng nhập lý do hủy phòng!');
      return;
    }
    try {
      setLoading(true);
      await ApiService.cancelSingleRoom(
        selectedBooking.id,
        cancelingRoomRecord.bookingRoomId,
        refundMethod,
        cancelReason
      );
      message.success('Hủy phòng thành công!');
      setIsCancelRoomModalOpen(false);
      await handleOpenDetail(selectedBooking.id);
      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Hủy phòng thất bại');
    } finally {
      setLoading(false);
    }
  }, [
    cancelReason, refundMethod, selectedBooking, cancelingRoomRecord,
    setLoading, setIsCancelRoomModalOpen, handleOpenDetail,
    fetchBookings, currentPage,
  ]);

  // ── Duyệt hoàn tiền thủ công ─────────────────────────────────────────────
  const handleApproveManualRefund = useCallback(async (bookingId) => {
    try {
      await ApiService.approveManualRefund(bookingId, refundMethod);
      message.success('Đã xác nhận hoàn tiền cho khách');
      setIsModalOpen(false);
      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Lỗi xác nhận hoàn tiền');
    }
  }, [refundMethod, setIsModalOpen, fetchBookings, currentPage]);

  // ── Mở Modal xếp phòng vật lý ────────────────────────────────────────────
  const handleOpenAssignRoom = useCallback(async (bookingRoomRecord) => {
    try {
      setLoading(true);
      const res = await ApiService.getRooms({ 
        page: 0, 
        size: 100, 
        roomNumber: '', 
        status: 'AVAILABLE' 
      });
      const roomsData = res.result?.content ?? res.content ?? [];
      const filtered = roomsData.filter((r) => r.roomType === bookingRoomRecord.roomType);
      setAvailableRoomsForAssign(filtered);
      setSelectedBookingRoomIdToAssign(bookingRoomRecord.bookingRoomId);
      setIsAssignRoomModalOpen(true);
    } catch {
      message.error('Lỗi khi lấy danh sách phòng trống');
    } finally {
      setLoading(false);
    }
  }, [
    setLoading, setAvailableRoomsForAssign,
    setSelectedBookingRoomIdToAssign, setIsAssignRoomModalOpen,
  ]);

  // ── Xác nhận xếp phòng ────────────────────────────────────────────────────
  const handleConfirmAssignRoom = useCallback(async () => {
    if (!selectedRoomIdToAssign) {
      message.warning('Vui lòng chọn một phòng!');
      return;
    }

    // 1. Xác định xem đây là xếp phòng mới hay đổi số phòng (Logic lấy từ AssignRoomModal)
    const currentBookingRoom = selectedBooking?.bookingRooms?.find(
      (r) => r.bookingRoomId === selectedBookingRoomIdToAssign
    );
    const isReassign = !!currentBookingRoom?.roomNumber;

    try {
      await ApiService.assignRoom(selectedBookingRoomIdToAssign, selectedRoomIdToAssign);
      
      // 2. 🔥 SỬA Ở ĐÂY: Hiển thị thông báo động theo trạng thái phòng
      if (isReassign) {
        message.success('Đổi số phòng thành công!');
      } else {
        message.success('Xếp phòng thành công!');
      }

      setIsAssignRoomModalOpen(false);
      await handleOpenDetail(selectedBooking.id);
      fetchBookings(currentPage);
    } catch (err) {
      // Tương tự, bạn cũng có thể tối ưu câu báo lỗi nếu muốn
      const errorMsg = err.response?.data?.message;
      message.error(errorMsg || (isReassign ? 'Lỗi đổi số phòng!' : 'Lỗi xếp phòng!'));
    }
  }, [
    selectedRoomIdToAssign, selectedBookingRoomIdToAssign, selectedBooking,
    setIsAssignRoomModalOpen, handleOpenDetail, fetchBookings, currentPage,
  ]);

  // ── Mở Modal thêm phòng vào đơn ──────────────────────────────────────────
  const handleOpenAddRoom = useCallback(async () => {
    try {
      const { arrivalDate, departureDate } = selectedBooking;
      const res = await ApiService.getAvailableRoomTypesWithCount(arrivalDate, departureDate);
      setAvailableRoomTypes(res.result ?? res ?? []);
      setIsAddRoomModalOpen(true);
    } catch {
      message.error('Không tải được danh sách loại phòng!');
    }
  }, [selectedBooking, setAvailableRoomTypes, setIsAddRoomModalOpen]);

  // ── Xác nhận thêm phòng ───────────────────────────────────────────────────
  const handleConfirmAddRoom = useCallback(async () => {
    if (!selectedRoomTypeToAdd) {
      message.warning('Vui lòng chọn loại phòng!');
      return;
    }
    try {
      setLoading(true);
      await ApiService.addRoom(selectedBooking.id, selectedRoomTypeToAdd);
      message.success('Thêm phòng thành công!');
      setIsAddRoomModalOpen(false);
      setSelectedRoomTypeToAdd(null);
      const updated = await ApiService.getBookingById(selectedBooking.id);
      setSelectedBooking(updated.result ?? updated);
    } catch (err) {
      message.error(err.response?.data?.message || 'Thêm phòng thất bại');
    } finally {
      setLoading(false);
    }
  }, [
    selectedRoomTypeToAdd, selectedBooking, setLoading,
    setIsAddRoomModalOpen, setSelectedRoomTypeToAdd, setSelectedBooking,
  ]);

  // ── Mở Modal đổi loại phòng ───────────────────────────────────────────────
  const handleOpenChangeRoom = useCallback(async (bookingRoomRecord) => {
    try {
      setLoading(true);
      const { arrivalDate, departureDate } = selectedBooking;
      const res = await ApiService.getAvailableRoomTypesWithCount(arrivalDate, departureDate);
      const typesData = res.result ?? res ?? [];
      if (!typesData.length) {
        message.warning('Hiện tại không có loại phòng nào!');
        return;
      }
      setRoomTypesList(typesData);
      setCurrentBookingRoomId(bookingRoomRecord.bookingRoomId ?? bookingRoomRecord.id);
      setSelectedRoomTypeIdToChange(null);
      setIsChangeRoomModalOpen(true);
    } catch {
      message.error('Không thể tải danh sách loại phòng');
    } finally {
      setLoading(false);
    }
  }, [
    selectedBooking, setLoading, setRoomTypesList,
    setCurrentBookingRoomId, setSelectedRoomTypeIdToChange, setIsChangeRoomModalOpen,
  ]);

  // ── Xác nhận đổi loại phòng ───────────────────────────────────────────────
  const handleConfirmChangeRoom = useCallback(async () => {
    if (!selectedRoomTypeIdToChange) {
      message.warning('Vui lòng chọn loại phòng mới!');
      return;
    }
    try {
      setLoading(true);
      await ApiService.changeRoomType(currentBookingRoomId, selectedRoomTypeIdToChange);
      message.success('Đổi loại phòng thành công!');
      setIsChangeRoomModalOpen(false);
      if (selectedBooking?.id) await handleOpenDetail(selectedBooking.id);
      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Đổi loại phòng thất bại');
    } finally {
      setLoading(false);
    }
  }, [
    selectedRoomTypeIdToChange, currentBookingRoomId, selectedBooking,
    setLoading, setIsChangeRoomModalOpen, handleOpenDetail,
    fetchBookings, currentPage,
  ]);

  // ── Mở Modal thêm dịch vụ ────────────────────────────────────────────────
  const handleOpenAddService = useCallback(async () => {
    try {
      const res = await ApiService.getExtraServices(0, 100, '', true);
      setAvailableServices(res?.content ?? res ?? []);
      setSelectedServiceId(null);
      setQuantityToAdd(1);
      setIsAddServiceModalOpen(true);
    } catch {
      message.error('Lỗi lấy danh sách dịch vụ!');
    }
  }, [setAvailableServices, setSelectedServiceId, setQuantityToAdd, setIsAddServiceModalOpen]);

  // ── Xác nhận thêm dịch vụ ────────────────────────────────────────────────
  const handleConfirmAddService = useCallback(async () => {
    if (!selectedServiceId) {
      message.warning('Vui lòng chọn dịch vụ!');
      return;
    }
    try {
      await ApiService.addService({
        bookingId: selectedBooking.id,
        serviceId: selectedServiceId,
        quantity: quantityToAdd,
      });
      message.success('Thêm dịch vụ thành công!');
      setIsAddServiceModalOpen(false);
      setSelectedServiceId(null);
      setQuantityToAdd(1);
      await handleOpenDetail(selectedBooking.id);
      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Thêm dịch vụ thất bại');
    }
  }, [
    selectedServiceId, quantityToAdd, selectedBooking,
    setIsAddServiceModalOpen, setSelectedServiceId, setQuantityToAdd, handleOpenDetail,
  ]);

  // ── Hủy một dịch vụ ──────────────────────────────────────────────────────
  const handleCancelService = useCallback(async (detailIdOrRecord) => {
    const detailId =
      typeof detailIdOrRecord === 'object' ? detailIdOrRecord?.id : detailIdOrRecord;
    if (!detailId) {
      message.error('Không xác định được ID dịch vụ!');
      return;
    }
    try {
      await ApiService.cancelBookingService(detailId);
      message.success('Hủy dịch vụ thành công!');
      fetchBookings(currentPage);
      if (selectedBooking?.id) await handleOpenDetail(selectedBooking.id);
    } catch {
      message.error('Lỗi hệ thống khi hủy dịch vụ');
    }
  }, [fetchBookings, currentPage, selectedBooking, handleOpenDetail]);

  // ── Cập nhật số lượng dịch vụ ────────────────────────────────────────────
  const handleUpdateQuantity = useCallback(async () => {
    try {
      await ApiService.updateBookingService(editingService.id, newQuantity);
      message.success('Cập nhật số lượng thành công!');
      setIsEditServiceModalOpen(false);
      await handleOpenDetail(selectedBooking.id);
      fetchBookings(currentPage);
    } catch (err) {
      message.error(err.response?.data?.message || 'Cập nhật thất bại');
    }
  }, [editingService, newQuantity, setIsEditServiceModalOpen, setSelectedBooking]);

  return {
    handleOpenDetail,
    handleCheckIn,
    handleCheckOutPrompt,
    handleConfirmCheckOut,
    handleConfirmDeposit,
    handleCancelPrompt,
    submitCancelBooking,
    handleCancelRoomPrompt,
    submitCancelRoom,
    handleApproveManualRefund,
    handleOpenAssignRoom,
    handleConfirmAssignRoom,
    handleOpenAddRoom,
    handleConfirmAddRoom,
    handleOpenChangeRoom,
    handleConfirmChangeRoom,
    handleOpenAddService,
    handleConfirmAddService,
    handleCancelService,
    handleUpdateQuantity,
    handlePrintInvoice,
  };
};

export default useBookingActions;
