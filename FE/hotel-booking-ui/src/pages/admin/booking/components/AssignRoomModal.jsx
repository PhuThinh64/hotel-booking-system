import React from 'react';
import { Modal, Select } from 'antd';

const AssignRoomModal = ({
  open,
  onCancel,
  selectedBooking,
  selectedBookingRoomIdToAssign,
  availableRoomsForAssign,
  selectedRoomIdToAssign,
  onChangeSelectedRoomId,
  onConfirm,
}) => {
  const currentBookingRoom = selectedBooking?.bookingRooms?.find(
    (r) => r.bookingRoomId === selectedBookingRoomIdToAssign
  );

  const isReassign = !!currentBookingRoom?.roomNumber;

  return (
    <Modal
      title={isReassign ? 'Đổi phòng' : 'Xếp phòng vật lý'}
      open={open}
      onOk={onConfirm}
      onCancel={onCancel}
    >
      <p>
        {isReassign
          ? `Chọn phòng mới (hiện tại ${currentBookingRoom.roomNumber})`
          : 'Chọn phòng trống'}
      </p>
      <Select
        style={{ width: '100%' }}
        placeholder="-- Chọn phòng --"
        onChange={onChangeSelectedRoomId}
        value={selectedRoomIdToAssign}
      >
        {availableRoomsForAssign.map((r) => (
          <Select.Option key={r.id} value={r.id}>
            Phòng {r.roomNumber} - Tầng {r.floor}
          </Select.Option>
        ))}
      </Select>
    </Modal>
  );
};

export default AssignRoomModal;
