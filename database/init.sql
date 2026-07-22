-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th7 18, 2026 lúc 09:27 AM
-- Phiên bản máy phục vụ: 9.1.0
-- Phiên bản PHP: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `hotel_booking`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
CREATE TABLE IF NOT EXISTS `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint DEFAULT NULL,
  `operator_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_id` bigint NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_audit_logs_employees` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=244 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `audit_logs`
--

INSERT INTO `audit_logs` (`id`, `employee_id`, `operator_name`, `module`, `action`, `target_id`, `description`, `created_at`) VALUES
(1, 1, '', 'BOOKING', 'CANCEL_ROOM_ITEM', 329, '[Nhân viên: admin] Hủy lẻ phòng ID #419 thuộc đơn #329. Tổng tiền đơn mới giảm xuống còn: 700,000 VND', '2026-06-16 05:33:28'),
(2, 1, '', 'BOOKING', 'CANCEL_FULL', 329, '[Nhân viên: admin] Hủy toàn bộ đơn #329. Phương thức gốc: CASH. Tiền hoàn: 0 VND', '2026-06-16 05:33:52'),
(3, 1, '', 'BOOKING', 'CANCEL_FULL', 325, '[Nhân viên: admin] Hủy toàn bộ đơn #325. Phương thức gốc: VNPAY. Tiền hoàn: 0 VND', '2026-06-16 05:36:59'),
(4, 1, '', 'PAYMENT', 'APPROVE_REFUND', 330, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #330. Số tiền: 448,000 VND bằng hình thức: CASH', '2026-06-16 05:53:05'),
(5, 1, '', 'PAYMENT', 'APPROVE_REFUND', 334, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #334. Số tiền: 400,000 VND bằng hình thức: CASH', '2026-06-16 08:59:13'),
(6, 1, '', 'PAYMENT', 'APPROVE_REFUND', 335, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #335. Số tiền: 440,000 VND bằng hình thức: CASH', '2026-06-16 09:02:25'),
(7, 1, '', 'PAYMENT', 'APPROVE_REFUND', 337, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #337. Số tiền: 620,000 VND bằng hình thức: BANK_TRANSFER', '2026-06-16 09:09:07'),
(8, 1, '', 'PAYMENT', 'APPROVE_REFUND', 340, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #340. Số tiền: 448,000 VND bằng hình thức: CASH', '2026-06-16 09:22:17'),
(9, 1, '', 'PAYMENT', 'APPROVE_REFUND', 343, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #343. Số tiền: 1,340,000 VND bằng hình thức: CASH', '2026-06-17 03:18:58'),
(10, 1, '', 'PAYMENT', 'APPROVE_REFUND', 342, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #342. Số tiền: 1,280,000 VND bằng hình thức: CASH', '2026-06-17 03:24:35'),
(11, 1, '', 'PAYMENT', 'APPROVE_REFUND', 344, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #344. Số tiền: 628,000 VND bằng hình thức: BANK_TRANSFER', '2026-06-17 04:20:31'),
(12, 1, '', 'BOOKING', 'CHECK_IN', 354, 'Thực hiện Check-in thành công cho mã đơn #354. Trạng thái các phòng liên quan chuyển sang ĐANG Ở (OCCUPIED).', '2026-06-17 05:42:13'),
(13, 1, '', 'BOOKING', 'CHECK_OUT', 354, 'Khởi tạo checkout VNPay cho đơn #354. Tổng tiền  950,000 VND. Còn thanh toán  570,000 VND.', '2026-06-17 05:42:19'),
(14, 1, '', 'BOOKING', 'CHECK_IN', 352, 'Thực hiện Check-in thành công cho mã đơn #352. Trạng thái các phòng liên quan chuyển sang ĐANG Ở (OCCUPIED).', '2026-06-17 05:42:49'),
(15, 1, '', 'BOOKING', 'CHECK_OUT', 352, 'Khởi tạo checkout VNPay cho đơn #352. Tổng tiền  1,600,000 VND. Còn thanh toán  960,000 VND.', '2026-06-17 05:43:01'),
(16, 1, '', 'BOOKING', 'CHECK_OUT', 352, 'Check-out tiền mặt cho đơn #352. Tổng tiền  1,600,000 VND. Thu thêm  960,000 VND. Hoàn khách  0 VND.', '2026-06-17 08:06:50'),
(17, 1, '', 'BOOKING', 'CHECK_IN', 358, 'Thực hiện Check-in thành công cho mã đơn #358. Trạng thái các phòng liên quan chuyển sang ĐANG Ở (OCCUPIED).', '2026-06-19 08:10:26'),
(18, 1, '', 'BOOKING', 'CHECK_OUT', 358, 'Khởi tạo checkout VNPay cho đơn #358. Tổng tiền  750,000 VND. Còn thanh toán  590,000 VND.', '2026-06-19 08:11:14'),
(19, 1, '', 'BOOKING', 'CHECK_IN', 359, 'Thực hiện Check-in thành công cho mã đơn #359. Trạng thái các phòng liên quan chuyển sang ĐANG Ở (OCCUPIED).', '2026-06-21 08:13:44'),
(20, 1, '', 'BOOKING', 'CHECK_OUT', 359, 'Khởi tạo checkout VNPay cho đơn #359. Tổng tiền  1,450,000 VND. Còn thanh toán  1,150,000 VND.', '2026-06-21 08:17:35'),
(21, 1, '', 'BOOKING', 'CHECK_OUT', 359, 'Khởi tạo checkout VNPay cho đơn #359. Tổng tiền  1,450,000 VND. Còn thanh toán  1,150,000 VND.', '2026-06-21 08:17:56'),
(22, 1, '', 'BOOKING', 'CHECK_OUT', 358, 'Khởi tạo checkout VNPay cho đơn #358. Tổng tiền  750,000 VND. Còn thanh toán  590,000 VND.', '2026-06-21 08:18:10'),
(23, 1, '', 'PAYMENT', 'APPROVE_REFUND', 347, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #347. Số tiền: 640,000 VND bằng hình thức: CASH', '2026-06-13 08:25:44'),
(24, 1, '', 'BOOKING', 'CHECK_IN', 280, 'Thực hiện Check-in thành công cho mã đơn #280. Trạng thái các phòng liên quan chuyển sang ĐANG Ở (OCCUPIED).', '2026-06-19 09:46:19'),
(25, 1, '', 'BOOKING', 'CHECK_OUT', 359, 'Khởi tạo checkout VNPay cho đơn #359. Tổng tiền  750,000 VND. Còn thanh toán  450,000 VND.', '2026-06-19 10:20:54'),
(26, 1, '', 'BOOKING', 'CHECK_OUT', 358, 'Khởi tạo checkout VNPay cho đơn #358. Tổng tiền  750,000 VND. Còn thanh toán  590,000 VND.', '2026-06-19 10:23:09'),
(27, 1, '', 'BOOKING', 'CHECK_OUT', 359, 'Check-out tiền mặt cho đơn #359. Tổng tiền  750,000 VND. Thu thêm  450,000 VND. Hoàn khách  0 VND.', '2026-06-19 10:25:08'),
(28, 1, '', 'BOOKING', 'CHECK_OUT', 358, 'Khởi tạo checkout VNPay cho đơn #358. Tổng tiền  750,000 VND. Còn thanh toán  590,000 VND.', '2026-06-19 10:25:26'),
(29, 1, '', 'PAYMENT', 'APPROVE_REFUND', 348, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #348. Số tiền: 640,000 VND bằng hình thức: CASH', '2026-06-19 10:55:07'),
(30, 1, '', 'PAYMENT', 'APPROVE_REFUND', 349, 'Nhân viên [admin] đã xác nhận HOÀN TIỀN THỦ CÔNG cho đơn #349. Số tiền: 640,000 VND bằng hình thức: BANK_TRANSFER', '2026-06-19 10:55:10'),
(31, 1, '', 'BOOKING', 'CHECK_IN', 428, 'Thực hiện Check-in thành công cho mã đơn #428. Trạng thái các phòng liên quan chuyển sang ĐANG Ở (OCCUPIED).', '2026-06-21 15:36:04'),
(32, 2, 'Nguyễn Văn An (Lễ Tân)', 'CUSTOMER', 'UPDATE', 10, 'Số CCCD/Passport đổi từ [Trống] thành [089204012021]; Giới tính đổi từ [Trống] thành [MALE]', '2026-06-22 10:57:29'),
(33, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'ADD_ROOM_TYPE', 425, 'Thêm phòng loại [Deluxe Ocean View] vào đơn đặt phòng #425.', '2026-06-22 13:33:00'),
(34, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 425, 'Đổi loại phòng cho bản ghi #622 sang loại ID: 4.', '2026-06-22 13:33:07'),
(35, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 629, 'Phòng cụ thể đổi từ [Trống] thành [com.example.hotel_booking.room.entity.Room@5e558eeb]', '2026-06-22 13:33:12'),
(36, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 425, 'Thêm dịch vụ [Đưa đón sân bay] (Số lượng: 1) vào đơn đặt phòng #425.', '2026-06-22 13:33:17'),
(37, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 335, 'Số lượng dịch vụ đổi từ [1] thành [4]', '2026-06-22 13:33:21'),
(38, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 425, 'Hủy dịch vụ [Thuê xe máy] khỏi đơn đặt phòng #425.', '2026-06-22 13:33:23'),
(39, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 423, 'Tiền phòng đổi từ [1100000.00] thành [0]; Tiền dịch vụ đổi từ [0.00] thành [0]; Tổng tiền đơn đổi từ [1100000.00] thành [0]; Lý do hủy đơn đổi từ [Trống] thành [sdffdsfdssdfsfd]', '2026-06-22 13:35:18'),
(40, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 594, 'Phòng cụ thể đổi từ [Trống] thành [com.example.hotel_booking.room.entity.Room@795e4483]', '2026-06-22 13:41:15'),
(41, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 597, 'Phòng cụ thể đổi từ [Trống] thành [Room(ID: 27)]', '2026-06-22 13:49:58'),
(42, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 596, 'Phòng cụ thể đổi từ [Trống] thành [Room(ID: 25)]', '2026-06-22 13:50:05'),
(43, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 369, 'Tiền phòng đổi từ [1550000.00] thành [0]; Tiền dịch vụ đổi từ [120000.00] thành [0]; Tổng tiền đơn đổi từ [1670000.00] thành [0]; Lý do hủy đơn đổi từ [Trống] thành [369]', '2026-06-22 14:00:54'),
(44, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 421, 'Thêm dịch vụ [Thuê xe máy] (Số lượng: 2) vào đơn đặt phòng #421.', '2026-06-22 14:13:07'),
(45, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 421, 'Số lượng dịch vụ đổi từ [1] thành [4]', '2026-06-22 14:13:23'),
(46, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 422, 'Phòng cụ thể đổi từ [Trống] thành [Room(ID: 24)]', '2026-06-22 14:23:55'),
(47, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 421, 'Phòng cụ thể: [Phòng 104 (ID: 26)] -> [Phòng 103 (ID: 25)]', '2026-06-22 14:52:41'),
(48, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 421, 'Số lượng dịch vụ: [4] -> [3]', '2026-06-22 14:53:17'),
(49, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 421, 'Số lượng dịch vụ dịch vụ [Giặt ủi]: [3] -> [2]', '2026-06-22 14:58:06'),
(50, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 421, 'Hủy phòng (BookingRoom ID: 597) trong đơn. Lý do: 12ád. Hình thức hoàn: null', '2026-06-22 15:13:49'),
(51, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 421, 'Hủy dịch vụ [Thuê xe máy] khỏi đơn đặt phòng #421.', '2026-06-22 15:39:45'),
(52, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 429, 'Hủy phòng (Suite Family) trong đơn. Lý do: ac1. Hình thức hoàn: null', '2026-06-22 16:08:42'),
(53, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 429, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-06-22T23:11:16.108330]', '2026-06-22 16:11:16'),
(54, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 428, 'Tổng tiền đơn: [710000.00] -> [815000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-07-15T14:16:22.861898]', '2026-07-15 07:16:23'),
(55, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 429, 'Tiền phụ thu: [Trống] -> [0.00], Thời gian Check-out thực tế: [Trống] -> [2026-06-22T23:51:29.358092]', '2026-06-22 16:51:29'),
(56, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 429, 'Thời gian Check-out thực tế: [2026-06-22T23:51:29.358092] -> [2026-06-22T23:51:33.754630]', '2026-06-22 16:51:34'),
(57, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'UPDATE', 429, 'Trạng thái đặt phòng: [CHECKED_IN] -> [PENDING_REFUND], Tổng tiền đơn: [550000.00] -> [655000.00], Tiền phụ thu: [0.00] -> [105000.00], Thời gian Check-out thực tế: [2026-06-22T23:51:33.754630] -> [2026-07-29T13:24:57.244948]', '2026-07-29 06:24:57'),
(58, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'REFUND', 429, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #429. Tổng tiền hoàn trả khách: 45,000 VND bằng hình thức [BANK_TRANSFER].', '2026-07-29 06:51:25'),
(59, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 428, 'Thời gian Check-out thực tế: [2026-07-15T14:16:22.861898] -> [2026-07-15T13:53:11.367382]', '2026-07-15 06:53:12'),
(60, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 436, 'Xác nhận thanh toán thành công cho đơn #436. Số tiền: 34000000 VND. Mã GD: 15594236', '2026-07-15 07:28:29'),
(61, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 437, 'Xác nhận thanh toán thành công cho đơn #437. Số tiền: 34000000 VND. Mã GD: 15594241', '2026-07-15 07:31:32'),
(62, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 436, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-07-29T14:32:50.354995]', '2026-07-29 07:32:50'),
(63, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 437, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-07-29T14:32:51.080999]', '2026-07-29 07:32:51'),
(64, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 438, 'Xác nhận thanh toán thành công cho đơn #438. Số tiền: 64000000 VND. Mã GD: 15594243', '2026-07-31 07:38:51'),
(65, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 439, 'Xác nhận thanh toán thành công cho đơn #439. Số tiền: 64000000 VND. Mã GD: 15594244', '2026-07-31 07:39:56'),
(66, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 438, 'Hủy phòng (Suite Family) trong đơn. Lý do: a. Hình thức hoàn: null', '2026-08-18 07:40:35'),
(67, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 438, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-18T14:40:48.149585]', '2026-08-18 07:40:48'),
(68, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 438, 'Hủy dịch vụ [Giặt ủi] khỏi đơn đặt phòng #438.', '2026-08-18 07:40:54'),
(69, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 439, 'Hủy phòng (Suite Family) trong đơn. Lý do: h. Hình thức hoàn: null', '2026-08-18 07:46:10'),
(70, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 439, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-18T14:46:39.842637]', '2026-08-18 07:46:40'),
(71, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 438, 'Trạng thái đặt phòng: [CHECKED_IN] -> [PENDING_REFUND], Tổng tiền đơn: [-290000.00] -> [455000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-08-20T14:49:54.558918]', '2026-08-20 07:49:55'),
(72, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 439, 'Trạng thái đặt phòng: [CHECKED_IN] -> [PENDING_REFUND], Tổng tiền đơn: [400000.00] -> [505000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-08-20T14:52:38.884232]', '2026-08-20 07:52:39'),
(73, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 440, 'Xác nhận thanh toán thành công cho đơn #440. Số tiền: 64000000 VND. Mã GD: 15594251', '2026-08-20 07:54:34'),
(74, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 441, 'Xác nhận thanh toán thành công cho đơn #441. Số tiền: 64000000 VND. Mã GD: 15594252', '2026-08-20 07:55:10'),
(75, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 440, 'Hủy phòng (Suite Family) trong đơn. Lý do: 1. Hình thức hoàn: null', '2026-09-19 08:01:13'),
(76, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 441, 'Hủy phòng (Suite Family) trong đơn. Lý do: 2. Hình thức hoàn: null', '2026-09-19 08:01:18'),
(77, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 440, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-09-19T15:01:19.226416]', '2026-09-19 08:01:19'),
(78, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 441, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-09-19T15:01:20.066929]', '2026-09-19 08:01:20'),
(79, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 440, 'Trạng thái đặt phòng: [CHECKED_IN] -> [PENDING_REFUND], Tổng tiền đơn: [400000.00] -> [505000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-09-21T14:25:02.196180]', '2026-09-21 07:25:02'),
(80, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 441, 'Trạng thái đặt phòng: [CHECKED_IN] -> [PENDING_REFUND], Tổng tiền đơn: [400000.00] -> [505000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-09-21T14:26:29.418932]', '2026-09-21 07:26:29'),
(81, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 442, 'Xác nhận thanh toán thành công cho đơn #442. Số tiền: 64000000 VND. Mã GD: 15594266', '2026-09-21 07:28:04'),
(82, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 443, 'Xác nhận thanh toán thành công cho đơn #443. Số tiền: 64000000 VND. Mã GD: 15594267', '2026-09-21 07:28:39'),
(83, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 442, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-09-21T14:31:08.002533]', '2026-09-21 07:31:08'),
(84, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 443, 'Hủy phòng (Suite Family) trong đơn. Lý do: a. Hình thức hoàn: null', '2026-09-21 07:31:52'),
(85, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 443, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-09-21T14:31:53.877219]', '2026-09-21 07:31:54'),
(86, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 444, 'Xác nhận thanh toán thành công cho đơn #444. Số tiền: 64000000 VND. Mã GD: 15595074', '2026-06-23 00:36:25'),
(87, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 445, 'Xác nhận thanh toán thành công cho đơn #445. Số tiền: 64000000 VND. Mã GD: 15595079', '2026-06-23 00:37:15'),
(88, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 444, 'Hủy phòng (Suite Family) trong đơn. Lý do: a. Hình thức hoàn: null', '2026-07-20 00:45:45'),
(89, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 445, 'Hủy phòng (Suite Family) trong đơn. Lý do: b. Hình thức hoàn: null', '2026-07-20 00:45:56'),
(90, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 444, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-07-20T14:45:57.973888]', '2026-07-20 00:45:58'),
(91, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 445, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-07-20T14:45:58.784889]', '2026-07-20 00:45:59'),
(92, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 444, 'Trạng thái đặt phòng: [CHECKED_IN] -> [CHECKED_OUT], Tổng tiền đơn: [400000.00] -> [505000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-07-22T13:49:17.232213]', '2026-07-21 23:49:17'),
(93, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 445, 'Thêm dịch vụ [Thuê xe máy] (Số lượng: 3) vào đơn đặt phòng #445.', '2026-07-21 23:50:05'),
(94, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 445, 'Tổng tiền đơn: [210000.00] -> [955000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-07-22T13:50:31.764645]', '2026-07-21 23:50:32'),
(95, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 447, 'Xác nhận thanh toán thành công cho đơn #447. Số tiền: 64000000 VND. Mã GD: 15595180', '2026-06-23 01:18:10'),
(96, NULL, 'Hệ thống', 'PAYMENT', 'SUCCESS', 448, 'Xác nhận thanh toán thành công cho đơn #448. Số tiền: 56000000 VND. Mã GD: 15595185', '2026-06-23 01:19:00'),
(97, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 446, 'Hủy phòng (Suite Family) trong đơn. Lý do: 1. Hình thức hoàn: null', '2026-08-08 00:19:20'),
(98, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 447, 'Hủy phòng (Suite Family) trong đơn. Lý do: 2. Hình thức hoàn: null', '2026-08-08 00:19:26'),
(99, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 448, 'Hủy phòng (Deluxe City View) trong đơn. Lý do: 3. Hình thức hoàn: null', '2026-08-08 00:19:31'),
(100, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 446, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-08T14:19:33.649452]', '2026-08-08 00:19:34'),
(101, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 447, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-08T14:19:34.321885]', '2026-08-08 00:19:34'),
(102, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 448, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-08T14:19:34.967884]', '2026-08-08 00:19:35'),
(103, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 446, 'Trạng thái đặt phòng: [CHECKED_IN] -> [CHECKED_OUT], Tổng tiền đơn: [400000.00] -> [505000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-08-10T14:19:49.454628]', '2026-08-10 00:19:49'),
(104, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 447, 'Thêm dịch vụ [Ăn sáng Buffet] (Số lượng: 3) vào đơn đặt phòng #447.', '2026-08-10 00:21:15'),
(105, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 447, 'Tổng tiền đơn: [120000.00] -> [865000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-08-10T14:25:11.922034]', '2026-08-10 00:25:12'),
(106, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 448, 'Trạng thái đặt phòng: [CHECKED_IN] -> [CHECKED_OUT], Tổng tiền đơn: [850000.00] -> [955000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-08-10T14:26:50.437711]', '2026-08-10 00:26:50'),
(107, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 443, 'Thêm dịch vụ [Giặt ủi] (Số lượng: 1) vào đơn đặt phòng #443.', '2026-08-10 00:28:03'),
(108, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 449, 'Hủy phòng (Suite Family) trong đơn. Lý do: 1. Hình thức hoàn: null', '2026-08-31 00:39:53'),
(109, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 450, 'Hủy phòng (Suite Family) trong đơn. Lý do: 2. Hình thức hoàn: null', '2026-08-31 00:39:59'),
(110, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 449, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-31T14:40:03.409806]', '2026-08-31 00:40:03'),
(111, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 450, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-31T14:40:04.168866]', '2026-08-31 00:40:04'),
(112, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 451, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-31T14:40:06.888862]', '2026-08-31 00:40:07'),
(113, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 452, 'Trạng thái đặt phòng: [CONFIRMED] -> [CHECKED_IN], Thời gian Check-in thực tế: [Trống] -> [2026-08-31T14:40:08.793856]', '2026-08-31 00:40:09'),
(114, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 449, 'Trạng thái đặt phòng: [CHECKED_IN] -> [PENDING_REFUND], Tổng tiền đơn: [400000.00] -> [505000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-09-02T14:40:24.646269]', '2026-09-02 00:40:25'),
(115, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 450, 'Trạng thái đặt phòng: [CHECKED_IN] -> [PENDING_REFUND], Tổng tiền đơn: [400000.00] -> [505000.00], Tiền phụ thu: [Trống] -> [105000.00], Thời gian Check-out thực tế: [Trống] -> [2026-09-02T14:40:40.131439]', '2026-09-02 00:40:40'),
(116, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 451, 'Trạng thái đặt phòng: [CHECKED_IN] -> [CHECKED_OUT], Tổng tiền đơn: [550000.00] -> [715000.00], Tiền phụ thu: [Trống] -> [165000.00], Thời gian Check-out thực tế: [Trống] -> [2026-09-02T14:40:58.154299]', '2026-09-02 00:40:58'),
(117, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 452, 'Tổng tiền đơn: [750000.00] -> [975000.00], Tiền phụ thu: [Trống] -> [225000.00], Thời gian Check-out thực tế: [Trống] -> [2026-09-02T14:41:28.572389]', '2026-09-02 00:41:29'),
(118, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'REFUND', 449, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #449. Tổng tiền hoàn trả khách: 135,000 VND bằng hình thức [CASH].', '2026-09-02 00:42:54'),
(119, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'REFUND', 440, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #440. Tổng tiền hoàn trả khách: 135,000 VND bằng hình thức [CASH].', '2026-09-02 00:45:09'),
(120, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'REFUND', 450, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #450. Tổng tiền hoàn trả khách: 135,000 VND bằng hình thức [BANK_TRANSFER].', '2026-09-02 00:45:12'),
(121, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ASSIGN_ROOM', 443, 'Phòng cụ thể phòng [Chưa xếp phòng]: [Trống] -> [Phòng 101 (ID: 22)]', '2026-06-23 02:02:16'),
(122, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 443, 'Số lượng dịch vụ dịch vụ [Giặt ủi]: [2] -> [3]', '2026-06-23 02:05:20'),
(123, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'UPDATE', 443, 'Thêm dịch vụ [123sda ] (Số lượng: 1) vào đơn đặt phòng #443.', '2026-06-23 02:06:59'),
(124, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'REFUND', 441, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #441. Tổng tiền hoàn trả khách: 135,000 VND bằng hình thức [CASH].', '2026-06-23 02:57:30'),
(125, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'ADD_SERVICE', 443, 'Thêm dịch vụ [Thuê xe máy] (Số lượng: 2) vào đơn đặt phòng #443.', '2026-06-23 02:58:18'),
(126, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CHECK_IN', 453, 'Đã thực hiện: CHECK_IN', '2026-06-23 02:59:09'),
(127, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_SERVICE', 453, 'Hủy dịch vụ [Thuê xe máy] khỏi đơn đặt phòng #453.', '2026-06-23 02:59:20'),
(128, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_SERVICE', 453, 'Hủy dịch vụ [Đưa đón sân bay] khỏi đơn đặt phòng #453.', '2026-06-23 02:59:22'),
(129, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_SERVICE', 453, 'Hủy dịch vụ [Giặt ủi] khỏi đơn đặt phòng #453.', '2026-06-23 02:59:23'),
(130, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_ROOM_ITEM', 454, 'Hủy phòng (Deluxe Ocean View) trong đơn. Lý do: 1a. Hình thức hoàn: null', '2026-06-23 03:00:50'),
(131, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CHECK_IN', 454, 'Đã thực hiện: CHECK_IN', '2026-06-23 03:00:53'),
(132, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_SERVICE', 454, 'Hủy dịch vụ [Giặt ủi] khỏi đơn đặt phòng #454.', '2026-06-23 03:00:59'),
(133, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_SERVICE', 454, 'Hủy dịch vụ [Ăn sáng Buffet] khỏi đơn đặt phòng #454.', '2026-06-23 03:01:01'),
(134, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_SERVICE', 454, 'Hủy dịch vụ [Đưa đón sân bay] khỏi đơn đặt phòng #454.', '2026-06-23 03:01:02'),
(135, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CHECK_OUT', 454, 'Đã thực hiện: CHECK_OUT', '2026-06-23 03:01:07'),
(136, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'REFUND', 454, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #454. Tổng tiền hoàn trả khách: 278,000 VND bằng hình thức [BANK_TRANSFER].', '2026-06-23 03:01:55'),
(137, NULL, 'VNPay System', 'PAYMENT', 'SUCCESS', 455, 'Xác nhận thanh toán thành công cho đơn #455. Số tiền: 14000000 VND. Mã GD: 15595526', '2026-06-23 03:14:54'),
(138, NULL, 'VNPay System', 'PAYMENT', 'SUCCESS', 456, 'Xác nhận thanh toán thành công cho đơn #456. Số tiền: 22000000 VND. Mã GD: 15595553', '2026-06-23 03:29:37'),
(139, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 456, 'Hủy phòng (Deluxe City View) trong đơn. Lý do: Khách tự hủy phòng cuối trên website. Hình thức hoàn: null', '2026-06-23 21:54:06'),
(140, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_IN', 458, 'Đã thực hiện: CHECK_IN', '2026-06-24 04:48:45'),
(141, NULL, 'VNPay System', 'PAYMENT', 'SUCCESS', 460, 'Xác nhận thanh toán thành công cho đơn #460. Số tiền: 26000000 VND. Mã GD: 15598364', '2026-06-24 22:49:47'),
(142, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_OUT', 443, 'Đã thực hiện: CHECK_OUT', '2026-06-24 22:57:02'),
(143, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ASSIGN_ROOM', 458, 'Phòng cụ thể phòng [Chưa xếp phòng]: [Trống] -> [Phòng 104 (ID: 26)]', '2026-06-24 23:25:56'),
(144, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ASSIGN_ROOM', 458, 'Phòng cụ thể phòng [104]: [Phòng 104 (ID: 26)] -> [Phòng 202 (ID: 27)]', '2026-06-24 23:26:09'),
(145, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_NUMBER', 458, 'Đổi số phòng cho bản ghi phòng #674 từ phòng [202] sang phòng [104].', '2026-06-24 23:28:55'),
(146, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_NUMBER', 458, 'Đổi số phòng cho bản ghi phòng #674 từ phòng [104] sang phòng [103].', '2026-06-24 23:29:07'),
(147, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_NUMBER', 458, 'Đổi số phòng cho bản ghi phòng #674 từ phòng [103] sang phòng [202].', '2026-06-24 23:29:13'),
(148, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_NUMBER', 458, 'Đổi số phòng cho bản ghi phòng #674 từ phòng [202] sang phòng [103].', '2026-06-24 23:32:33'),
(149, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_NUMBER', 458, 'Đổi số phòng cho bản ghi phòng #674 từ phòng [103] sang phòng [202].', '2026-06-24 23:32:37'),
(150, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 462, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK462. Tổng tiền: 2,400,000đ. Đã thu cọc (40%): 960,000đ', '2026-06-24 23:34:10'),
(151, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ASSIGN_ROOM', 455, 'Xếp số phòng [104] cho bản ghi phòng #671.', '2026-06-24 23:38:54'),
(152, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_NUMBER', 455, 'Đổi số phòng cho bản ghi phòng #671 từ phòng [104] sang phòng [103].', '2026-06-24 23:39:08'),
(153, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_TYPE', 455, 'Đổi loại phòng cho bản ghi #671 sang loại ID: 7.', '2026-06-24 23:39:18'),
(154, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'DELETE', 10, 'Đã xóa khách hàng: test gi do', '2026-06-24 23:56:45'),
(155, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'UPDATE', 3, 'Thay đổi trạng thái nhân viên angola quiang linh vl sang: INACTIVE', '2026-06-24 23:59:20'),
(156, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'ACTIVATE', 10, 'Đã kích hoạt lại khách hàng: test gi do', '2026-06-25 00:39:43'),
(157, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'DEACTIVATE', 10, 'Đã khóa khách hàng: test gi do', '2026-06-25 00:39:46'),
(158, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 463, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK463. Tổng tiền: 2,800,000đ. Đã thu cọc (40%): 1,120,000đ', '2026-06-25 02:19:47'),
(159, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 464, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK464. Tổng tiền: 570,000đ. Đã thu cọc (40%): 228,000đ', '2026-06-25 02:24:20'),
(160, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_SERVICE', 466, 'Hủy dịch vụ [Đưa đón sân bay] khỏi đơn đặt phòng #466.', '2026-06-25 02:33:46'),
(161, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 467, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK467. Tổng tiền: 350,000đ. Đã thu cọc (40%): 140,000đ', '2026-06-25 02:34:07'),
(162, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'DELETE', 17, 'Xóa mềm khách hàng: Nguyen Van A', '2026-06-25 02:58:15'),
(163, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'RESTORE', 17, 'Khôi phục khách hàng: Nguyen Van A', '2026-06-25 02:59:14'),
(164, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'DELETE', 9, 'Xóa mềm khách hàng: test abc ', '2026-06-25 03:03:09'),
(165, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'DELETE', 3, 'Xóa mềm (vô hiệu hóa) nhân viên: angola quiang linh vl', '2026-06-25 06:45:36'),
(166, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'RESTORE', 3, 'Khôi phục nhân viên: angola quiang linh vl', '2026-06-25 06:45:56'),
(167, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'DELETE', 3, 'Xóa mềm (vô hiệu hóa) nhân viên: angola quiang linh vl', '2026-06-25 06:47:45'),
(168, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'RESTORE', 3, 'Khôi phục nhân viên: angola quiang linh vl', '2026-06-25 06:48:25'),
(169, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'DELETE', 3, 'Xóa mềm (vô hiệu hóa) nhân viên: angola quiang linh vl', '2026-06-25 06:50:38'),
(170, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'RESTORE', 3, 'Khôi phục nhân viên: angola quiang linh vl', '2026-06-25 06:50:46'),
(171, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'DELETE', 3, 'Xóa mềm (vô hiệu hóa) nhân viên: angola quiang linh vl', '2026-06-25 06:57:53'),
(172, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'RESTORE', 3, 'Khôi phục nhân viên: angola quiang linh vl', '2026-06-25 06:57:58'),
(173, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM', 'CLEANING_COMPLETED', 22, 'Xác nhận dọn sạch phòng: 101', '2026-06-25 07:36:46'),
(174, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'DELETE', 3, 'Xóa mềm (vô hiệu hóa) nhân viên: angola quiang linh vl', '2026-06-25 08:14:10'),
(175, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'UPDATE', 16, 'Cập nhật loại phòng [Camp Nou]. Giá mới: 2123123123123', '2026-06-25 08:45:37'),
(176, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'UPDATE', 16, 'Cập nhật loại phòng [Camp Nou]. Giá mới: 1232131', '2026-06-25 08:45:47'),
(177, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'UPDATE', 16, 'Cập nhật loại phòng [Camp Nou]. Giá mới: 700000', '2026-06-25 08:45:56'),
(178, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'UPDATE', 16, 'Cập nhật loại phòng [Camp Nou]. Giá mới: 7000000', '2026-06-25 22:50:17'),
(179, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 469, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK469. Tổng tiền: 3,440,000đ. Đã thu cọc (40%): 1,376,000đ', '2026-06-26 06:33:27'),
(180, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_SERVICE', 470, 'Hủy dịch vụ [Thuê xe máy] khỏi đơn đặt phòng #470.', '2026-06-26 06:38:29'),
(181, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_ROOM_ITEM', 470, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy phòng lẻ trên website. Hình thức hoàn: null', '2026-06-26 06:38:33'),
(182, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_ROOM_ITEM', 470, 'Hủy phòng (Deluxe Ocean View) trong đơn. Lý do: Khách tự hủy phòng lẻ trên website. Hình thức hoàn: null', '2026-06-26 06:38:44'),
(183, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 472, 'Hủy phòng (Deluxe Ocean View) trong đơn. Lý do: Khách tự hủy phòng lẻ trên website. Hình thức hoàn: null', '2026-06-26 07:38:06'),
(184, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 472, 'Hủy phòng (Deluxe City View) trong đơn. Lý do: Khách tự hủy phòng cuối trên website. Hình thức hoàn: null', '2026-06-26 07:38:17'),
(185, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 473, 'Hủy phòng (Deluxe Ocean View) trong đơn. Lý do: Khách tự hủy phòng lẻ trên website. Hình thức hoàn: null', '2026-06-26 07:40:45'),
(186, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 473, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy phòng cuối trên website. Hình thức hoàn: null', '2026-06-26 07:41:11'),
(187, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CREATE_BOOKING_CASH', 474, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK474. Tổng tiền: 1,070,000đ. Đã thu cọc (40%): 428,000đ', '2026-06-26 07:41:48'),
(188, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_ROOM_ITEM', 474, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy. Hình thức hoàn: BANK_TRANSFER', '2026-06-26 07:42:04'),
(189, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_TYPE', 470, 'Đổi loại phòng cho bản ghi #688 sang loại ID: 8.', '2026-06-26 07:44:14'),
(190, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_IN', 470, 'Đã thực hiện: CHECK_IN', '2026-06-26 07:44:21'),
(191, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 475, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy phòng cuối trên website. Hình thức hoàn: null', '2026-06-26 07:47:46'),
(192, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 476, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy phòng lẻ trên website. Hình thức hoàn: null', '2026-06-26 07:49:20'),
(193, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 476, 'Hủy phòng (Standard Double) trong đơn. Lý do: Khách tự hủy phòng cuối trên website. Hình thức hoàn: null', '2026-06-26 07:49:44'),
(194, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CREATE_BOOKING_CASH', 477, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK477. Tổng tiền: 470,000đ. Đã thu cọc (40%): 188,000đ', '2026-06-26 07:50:13'),
(195, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_ROOM_ITEM', 477, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy. Hình thức hoàn: BANK_TRANSFER', '2026-06-26 07:50:41'),
(196, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 478, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy phòng lẻ trên website. Hình thức hoàn: null', '2026-06-26 08:13:16'),
(197, NULL, 'Hệ thống', 'BOOKING', 'CANCEL_ROOM_ITEM', 478, 'Hủy phòng (Standard Double) trong đơn. Lý do: Khách tự hủy phòng cuối trên website. Hình thức hoàn: null', '2026-06-26 08:13:21'),
(198, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CREATE_BOOKING_CASH', 479, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK479. Tổng tiền: 650,000đ. Đã thu cọc (40%): 260,000đ', '2026-06-26 08:14:15'),
(199, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'CANCEL_ROOM_ITEM', 479, 'Hủy phòng (Standard Twin) trong đơn. Lý do: Khách tự hủy. Hình thức hoàn: BANK_TRANSFER', '2026-06-26 08:14:42'),
(200, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ASSIGN_ROOM', 469, 'Xếp số phòng [104] cho bản ghi phòng #686.', '2026-06-26 08:15:44'),
(201, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHANGE_ROOM_NUMBER', 469, 'Đổi số phòng cho bản ghi phòng #686 từ phòng [104] sang phòng [202].', '2026-06-26 08:15:53'),
(202, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ADD_SERVICE', 469, 'Thêm dịch vụ [Nước suối/Nước ngọt] (Số lượng: 1) vào đơn đặt phòng #469.', '2026-06-26 08:16:01'),
(203, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_IN', 469, 'Đã thực hiện: CHECK_IN', '2026-06-26 08:16:09'),
(204, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_OUT', 469, 'Đã thực hiện: CHECK_OUT', '2026-06-26 08:16:55'),
(205, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'REFUND', 479, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #479. Tổng tiền hoàn trả khách: 260,000 VND bằng hình thức [BANK_TRANSFER].', '2026-06-26 08:17:35'),
(206, 2, 'Nguyễn Văn An (Lễ Tân)', 'BOOKING', 'REFUND', 438, 'Xác nhận hoàn tiền thủ công cho đơn đặt phòng #438. Tổng tiền hoàn trả khách: 185,000 VND bằng hình thức [CASH].', '2026-06-26 08:20:07'),
(207, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_SERVICE', 470, 'Hủy dịch vụ [Giặt ủi] khỏi đơn đặt phòng #470.', '2026-06-28 23:47:25'),
(208, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 480, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK480. Tổng tiền: 1,600,000đ. Đã thu cọc (40%): 640,000đ', '2026-06-29 06:47:32'),
(209, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_ROOM_ITEM', 480, 'Hủy phòng (Suite Family) trong đơn. Lý do: a1bc. Hình thức hoàn: null', '2026-06-29 06:47:59'),
(210, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_IN', 480, 'Đã thực hiện: CHECK_IN', '2026-06-29 10:55:39'),
(211, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ADD_SERVICE', 480, 'Thêm dịch vụ [Đưa đón sân bay] (Số lượng: 2) vào đơn đặt phòng #480.', '2026-06-29 10:55:51'),
(212, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_OUT', 480, 'Đã thực hiện: CHECK_OUT', '2026-06-29 10:55:54'),
(213, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 481, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK481. Tổng tiền: 1,600,000đ. Đã thu cọc (40%): 640,000đ', '2026-06-29 11:10:12'),
(214, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 482, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK482. Tổng tiền: 1,600,000đ. Đã thu cọc (40%): 640,000đ', '2026-06-29 11:10:28'),
(215, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_IN', 481, 'Đã thực hiện: CHECK_IN', '2026-06-29 11:10:34'),
(216, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_ROOM_ITEM', 482, 'Hủy phòng (Suite Family) trong đơn. Lý do: a. Hình thức hoàn: null', '2026-06-29 11:10:44'),
(217, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ADD_SERVICE', 482, 'Thêm dịch vụ [Ăn sáng Buffet] (Số lượng: 1) vào đơn đặt phòng #482.', '2026-06-29 11:10:53'),
(218, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_IN', 482, 'Đã thực hiện: CHECK_IN', '2026-06-29 11:10:55'),
(219, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ADD_SERVICE', 482, 'Thêm dịch vụ [Đưa đón sân bay] (Số lượng: 1) vào đơn đặt phòng #482.', '2026-06-29 11:11:06'),
(220, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_OUT', 482, 'Đã thực hiện: CHECK_OUT', '2026-06-29 11:11:09'),
(221, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_SERVICE', 468, 'Hủy dịch vụ [Đưa đón sân bay] khỏi đơn đặt phòng #468.', '2026-06-29 11:12:37'),
(222, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ADD_SERVICE', 467, 'Thêm dịch vụ [Thuê xe máy] (Số lượng: 1) vào đơn đặt phòng #467.', '2026-06-29 11:14:15'),
(223, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CANCEL_BOOKING', 467, 'Đã thực hiện: CANCEL_BOOKING', '2026-06-29 11:14:23'),
(224, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CHECK_OUT', 481, 'Đã thực hiện: CHECK_OUT', '2026-06-29 11:18:02'),
(225, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'CREATE_BOOKING_CASH', 483, 'Lễ tân tạo đơn hàng trực tiếp và thu tiền mặt tại quầy. Mã đơn: BOOK483. Tổng tiền: 2,100,000đ. Đã thu cọc (40%): 840,000đ', '2026-06-30 06:03:28'),
(226, NULL, 'user', 'BOOKING', 'CANCEL_BOOKING', 483, 'Đã thực hiện: CANCEL_BOOKING', '2026-06-30 07:20:55'),
(227, NULL, 'user', 'BOOKING', 'CANCEL_BOOKING', 484, 'Đã thực hiện: CANCEL_BOOKING', '2026-06-30 07:22:14'),
(228, 1, 'Nguyễn Phú Thịnh (Admin)', 'BOOKING', 'ADD_SERVICE', 468, 'Thêm dịch vụ [Thuê xe máy] (Số lượng: 4) vào đơn đặt phòng #468.', '2026-06-30 07:26:14'),
(229, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM', 'CREATE', 38, 'Tạo phòng mới: 123321 (Loại: Suite Family)', '2026-06-30 07:27:50'),
(230, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'DELETE', 14, 'Xóa loại phòng: Thinh a b', '2026-06-30 07:28:39'),
(231, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'DELETE', 14, 'Xóa loại phòng: Thinh a b', '2026-06-30 07:29:11'),
(232, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'DELETE', 16, 'Xóa loại phòng: Camp Nou', '2026-06-30 07:29:23'),
(233, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'UPDATE', 14, 'Cập nhật loại phòng [Thinh a b]. Giá mới: 250000', '2026-06-30 07:38:25'),
(234, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'UPDATE', 6, 'Cập nhật loại phòng [Deluxe City View]. Giá mới: 650000', '2026-06-30 07:38:45'),
(235, 1, 'Nguyễn Phú Thịnh (Admin)', 'ROOM_TYPE', 'UPDATE', 6, 'Cập nhật loại phòng [Deluxe City View]. Giá mới: 550000', '2026-06-30 07:39:01'),
(236, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'DELETE', 12, 'Xóa mềm khách hàng: Nguyen Thinh', '2026-06-30 07:40:44'),
(237, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'RESTORE', 12, 'Khôi phục khách hàng: Nguyen Thinh', '2026-06-30 07:41:14'),
(238, 1, 'Nguyễn Phú Thịnh (Admin)', 'CUSTOMER', 'DELETE', 12, 'Xóa mềm khách hàng: Nguyen Thinh', '2026-06-30 07:41:19'),
(239, 1, 'Nguyễn Phú Thịnh (Admin)', 'EMPLOYEE', 'CREATE', 4, 'Tạo mới nhân viên: Lễ Tân (Tài khoản: lt2)', '2026-06-30 07:43:02');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bookings`
--

DROP TABLE IF EXISTS `bookings`;
CREATE TABLE IF NOT EXISTS `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `contact_name` varchar(255) NOT NULL,
  `contact_phone` varchar(255) NOT NULL,
  `status` varchar(20) DEFAULT 'PENDING_DEPOSIT',
  `room_amount` decimal(38,2) DEFAULT NULL,
  `service_amount` decimal(38,2) DEFAULT NULL,
  `deposit_amount` decimal(38,2) DEFAULT NULL,
  `surcharge_amount` decimal(38,2) DEFAULT NULL,
  `total_amount` decimal(38,2) DEFAULT NULL,
  `arrival_date` datetime DEFAULT NULL,
  `departure_date` datetime DEFAULT NULL,
  `actual_check_in` datetime(6) DEFAULT NULL,
  `actual_check_out` datetime(6) DEFAULT NULL,
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `booking_code` (`booking_code`),
  KEY `idx_bookings_status_expiry` (`status`,`expiry_date`),
  KEY `idx_bookings_dates` (`arrival_date`,`departure_date`),
  KEY `idx_bookings_customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=493 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `bookings`
--

INSERT INTO `bookings` (`id`, `booking_code`, `customer_id`, `contact_name`, `contact_phone`, `status`, `room_amount`, `service_amount`, `deposit_amount`, `surcharge_amount`, `total_amount`, `arrival_date`, `departure_date`, `actual_check_in`, `actual_check_out`, `cancel_reason`, `expiry_date`, `created_at`, `updated_at`) VALUES
(255, 'BOOK255', 19, 'Nguyễn Văn User', '0999999998', 'CHECKED_OUT', 350000.00, 20000.00, 148000.00, NULL, 222000.00, '2026-05-16 14:52:27', '2026-05-16 15:40:00', NULL, NULL, '', NULL, '2026-05-15 10:27:41', '2026-05-16 15:40:00'),
(256, 'BOOK256', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 2100000.00, 0.00, 840000.00, NULL, 2100000.00, '2026-05-16 00:00:00', '2026-05-22 00:00:00', NULL, NULL, '', '2026-05-15 13:49:57', '2026-05-15 13:34:57', '2026-05-15 13:50:29'),
(257, 'BOOK257', 19, 'Nguyễn Văn User', '0999999998', 'CHECKED_OUT', 3500000.00, 120000.00, 1448000.00, NULL, 2172000.00, '2026-05-16 13:04:42', '2026-05-16 13:06:12', NULL, NULL, '', NULL, '2026-05-15 14:15:06', '2026-05-16 13:06:12'),
(258, 'BOOK258', 19, 'a li baba', '0949366866', 'CHECKED_OUT', 1950000.00, 400000.00, 800000.00, NULL, 1550000.00, '2026-05-16 13:23:48', '2026-05-16 14:37:32', NULL, NULL, '', NULL, '2026-05-15 14:21:45', '2026-05-16 14:37:32'),
(324, 'BOOK324', 19, 'angola quiang linh vlog', '0999999998', 'CANCELLED', 900000.00, 0.00, 360000.00, NULL, 900000.00, '2026-06-11 00:00:00', '2026-06-12 00:00:00', NULL, NULL, NULL, '2026-05-30 16:29:18', '2026-05-30 16:14:18', '2026-05-31 10:09:52'),
(388, 'BOOK388', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 0.00, 0.00, 448000.00, NULL, 0.00, '2026-06-24 00:00:00', '2026-06-25 00:00:00', NULL, NULL, 'Hủy đơn do hủy phòng lần lượt', NULL, '2026-06-20 14:39:19', '2026-06-21 22:19:34'),
(389, 'BOOK389', 19, 'Nguyen Thinh', '0949366866', 'CANCELLED', 0.00, 0.00, 0.00, NULL, 0.00, '2026-06-23 00:00:00', '2026-06-24 00:00:00', NULL, NULL, 'Tự động hủy do quá hạn thanh toán', '2026-06-20 15:06:08', '2026-06-20 14:51:08', '2026-06-20 15:06:13'),
(390, 'BOOK390', 19, 'Leo Nguyen', '0949366866', 'CANCELLED', 0.00, 50000.00, 540000.00, NULL, 0.00, '2026-06-24 00:00:00', '2026-06-25 00:00:00', NULL, NULL, 'Khách hủy lần lượt các phòng', NULL, '2026-06-20 14:56:51', '2026-06-20 15:48:12'),
(391, 'BOOK391', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 0.00, 20000.00, 448000.00, NULL, 0.00, '2026-06-23 00:00:00', '2026-06-24 00:00:00', NULL, NULL, 'Khách hủy lần lượt các phòng', NULL, '2026-06-20 15:04:03', '2026-06-20 15:58:27'),
(392, 'BOOK392', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 0.00, 50000.00, 160000.00, NULL, 0.00, '2026-07-07 00:00:00', '2026-07-08 00:00:00', NULL, NULL, 'Hủy đơn do hủy phòng duy nhất', NULL, '2026-06-20 15:19:01', '2026-06-20 15:51:47'),
(412, 'BOOK412', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 0.00, 50000.00, 640000.00, NULL, 0.00, '2026-07-03 00:00:00', '2026-07-04 00:00:00', NULL, NULL, 'Khách tự hủy', NULL, '2026-06-20 22:33:01', '2026-06-20 22:37:57'),
(425, 'BOOK425', 19, 'Nguyễn Văn User', '0999999998', 'CONFIRMED', 3200000.00, 300000.00, 820000.00, NULL, 2680000.00, '2026-07-16 00:00:00', '2026-07-17 00:00:00', NULL, NULL, NULL, NULL, '2026-06-21 21:09:57', '2026-06-22 20:33:23'),
(428, 'BOOK428', 19, 'Leo Nguyen', '0949366866', 'CHECKED_OUT', 350000.00, 360000.00, 188000.00, NULL, 815000.00, '2026-07-14 14:00:00', '2026-07-15 12:00:00', '2026-06-21 22:36:03.990010', '2026-07-15 13:53:11.367382', NULL, NULL, '2026-06-21 22:03:48', '2026-07-15 13:53:42'),
(440, 'BOOK440', 19, 'Nguyễn Văn User', '0999999998', 'CHECKED_OUT', 350000.00, 50000.00, 640000.00, NULL, 505000.00, '2026-09-20 14:00:00', '2026-09-21 12:00:00', '2026-09-19 15:01:19.226416', '2026-09-21 14:25:02.196180', NULL, NULL, '2026-08-20 14:54:10', '2026-09-02 07:45:09'),
(448, 'BOOK448', 19, 'Nguyễn Văn User', '0999999998', 'CHECKED_OUT', 350000.00, 500000.00, 560000.00, NULL, 955000.00, '2026-08-09 07:00:00', '2026-08-10 05:00:00', '2026-08-08 07:19:34.967884', '2026-08-10 07:26:50.437711', NULL, NULL, '2026-06-23 08:18:36', '2026-08-10 07:26:50'),
(455, 'BOOK455', 19, 'Nguyễn Văn User', '0999999998', 'CONFIRMED', 750000.00, 0.00, 140000.00, NULL, 750000.00, '2026-07-08 07:00:00', '2026-07-09 05:00:00', NULL, NULL, NULL, NULL, '2026-06-23 10:14:35', '2026-06-25 06:39:18'),
(461, 'BOOK461', 19, 'Nguyễn Văn User', '0999999998', 'CONFIRMED', 1100000.00, 300000.00, 560000.00, NULL, 1400000.00, '2027-07-08 07:00:00', '2027-07-10 05:00:00', NULL, NULL, NULL, NULL, '2026-06-25 06:22:38', '2026-06-25 06:23:03'),
(470, 'BOOK470', 19, 'Nguyễn Văn User', '0999999998', 'CHECKED_IN', 1200000.00, 0.00, 1300000.00, NULL, -100000.00, '2026-07-13 07:00:00', '2026-07-15 05:00:00', '2026-06-26 14:44:21.058799', NULL, NULL, NULL, '2026-06-26 13:37:22', '2026-06-29 06:47:25'),
(474, 'BOOK474', 19, 'Nguyễn Văn User', '0999999998', 'PENDING_REFUND', 0.00, 0.00, 428000.00, NULL, 0.00, '2026-07-21 07:00:00', '2026-07-22 05:00:00', NULL, NULL, 'Hủy đơn do hủy phòng duy nhất', NULL, '2026-06-26 14:41:48', '2026-06-26 14:42:04'),
(479, 'BOOK479', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 0.00, 0.00, 260000.00, NULL, 0.00, '2026-07-19 07:00:00', '2026-07-20 05:00:00', NULL, NULL, 'Hủy đơn do hủy phòng duy nhất', NULL, '2026-06-26 15:14:15', '2026-06-26 15:17:35'),
(482, 'BOOK482', 19, 'Nguyễn Văn User', '0999999998', 'CHECKED_OUT', 350000.00, 470000.00, 640000.00, 0.00, 820000.00, '2026-07-17 07:00:00', '2026-07-18 05:00:00', '2026-06-29 18:10:55.233591', '2026-06-29 18:11:08.890894', NULL, NULL, '2026-06-29 18:10:28', '2026-06-29 18:11:09'),
(484, 'BOOK484', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 0.00, 0.00, 360000.00, NULL, 0.00, '2026-07-22 07:00:00', '2026-07-23 05:00:00', NULL, NULL, 'Khách tự hủy', NULL, '2026-06-30 14:10:35', '2026-06-30 14:22:14'),
(486, 'BOOK486', 19, 'Nguyễn Văn User', '0999999998', 'CANCELLED', 0.00, 0.00, 0.00, NULL, 0.00, '2026-07-23 07:00:00', '2026-07-25 05:00:00', NULL, NULL, 'Tự động hủy do quá hạn thanh toán', '2026-07-01 07:03:09', '2026-07-01 06:48:09', '2026-07-01 07:03:30');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `booking_rooms`
--

DROP TABLE IF EXISTS `booking_rooms`;
CREATE TABLE IF NOT EXISTS `booking_rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint NOT NULL,
  `room_type_id` bigint NOT NULL,
  `room_id` bigint DEFAULT NULL,
  `price_at_order` decimal(38,2) NOT NULL,
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `status` enum('ACTIVE','CANCELLED') DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_booking_rooms_room_type` (`room_type_id`),
  KEY `idx_booking_rooms_overlap` (`room_id`),
  KEY `idx_booking_rooms_booking` (`booking_id`)
) ENGINE=InnoDB AUTO_INCREMENT=730 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `booking_rooms`
--

INSERT INTO `booking_rooms` (`id`, `booking_id`, `room_type_id`, `room_id`, `price_at_order`, `cancel_reason`, `status`, `created_at`, `updated_at`) VALUES
(278, 255, 5, 27, 350000.00, NULL, NULL, '2026-05-15 03:27:41', '2026-05-16 08:32:41'),
(279, 256, 5, NULL, 350000.00, NULL, NULL, '2026-05-15 06:34:57', '2026-05-15 06:34:57'),
(280, 257, 6, NULL, 550000.00, NULL, NULL, '2026-05-15 07:15:06', '2026-05-15 07:15:06'),
(281, 257, 8, NULL, 1200000.00, NULL, NULL, '2026-05-15 07:15:06', '2026-05-15 07:15:06'),
(282, 258, 7, NULL, 750000.00, NULL, NULL, '2026-05-15 07:21:45', '2026-05-16 07:25:08'),
(283, 258, 8, NULL, 1200000.00, NULL, NULL, '2026-05-15 07:21:45', '2026-05-16 07:25:10'),
(408, 324, 4, NULL, 350000.00, NULL, NULL, '2026-05-30 09:14:18', '2026-05-30 09:14:18'),
(409, 324, 6, NULL, 550000.00, NULL, NULL, '2026-05-30 09:14:18', '2026-05-30 09:14:18'),
(530, 388, 4, NULL, 350000.00, 'Khách tự hủy phòng cuối trên website', 'CANCELLED', '2026-06-20 07:39:19', '2026-06-21 15:19:34'),
(531, 388, 7, NULL, 750000.00, 'Khách tự hủy phòng lẻ trên website', 'CANCELLED', '2026-06-20 07:39:19', '2026-06-21 15:19:25'),
(532, 389, 6, NULL, 550000.00, 'Tự động hủy do quá hạn thanh toán', 'CANCELLED', '2026-06-20 07:51:08', '2026-06-20 08:06:13'),
(533, 389, 7, NULL, 750000.00, 'Tự động hủy do quá hạn thanh toán', 'CANCELLED', '2026-06-20 07:51:08', '2026-06-20 08:06:13'),
(534, 390, 6, NULL, 550000.00, 'áđâsd', 'CANCELLED', '2026-06-20 07:56:51', '2026-06-20 08:48:12'),
(535, 390, 7, NULL, 750000.00, 'Khách tự hủy lẻ phòng trên website', 'CANCELLED', '2026-06-20 07:56:51', '2026-06-20 07:58:44'),
(536, 391, 4, NULL, 350000.00, 'Khách tự hủy phòng cuối trên website', 'CANCELLED', '2026-06-20 08:04:03', '2026-06-20 08:58:27'),
(537, 391, 7, NULL, 750000.00, 'Khách tự hủy lẻ phòng trên website', 'CANCELLED', '2026-06-20 08:04:03', '2026-06-20 08:04:33'),
(538, 392, 4, NULL, 350000.00, 'Khách tự hủy phòng cuối trên website', 'CANCELLED', '2026-06-20 08:19:01', '2026-06-20 08:51:47'),
(577, 412, 5, NULL, 350000.00, 'Hủy theo đơn tổng: Khách tự hủy', 'CANCELLED', '2026-06-20 15:33:01', '2026-06-20 15:37:57'),
(578, 412, 8, NULL, 1200000.00, 'Hủy theo đơn tổng: Khách tự hủy', 'CANCELLED', '2026-06-20 15:33:01', '2026-06-20 15:37:57'),
(621, 425, 5, NULL, 350000.00, NULL, NULL, '2026-06-21 14:09:57', '2026-06-21 14:09:57'),
(622, 425, 4, NULL, 350000.00, NULL, NULL, '2026-06-21 14:09:57', '2026-06-22 13:33:07'),
(623, 425, 8, NULL, 1200000.00, NULL, NULL, '2026-06-21 14:09:57', '2026-06-21 14:09:57'),
(626, 428, 4, 24, 350000.00, NULL, NULL, '2026-06-21 15:03:48', '2026-06-22 13:40:45'),
(628, 425, 6, NULL, 550000.00, NULL, 'ACTIVE', '2026-06-21 16:53:14', '2026-06-21 16:53:22'),
(629, 425, 7, 31, 750000.00, NULL, 'ACTIVE', '2026-06-22 13:33:00', '2026-06-22 13:33:12'),
(644, 440, 5, NULL, 350000.00, NULL, NULL, '2026-08-20 07:54:10', '2026-08-20 07:54:10'),
(645, 440, 8, NULL, 1200000.00, '1', 'CANCELLED', '2026-08-20 07:54:10', '2026-09-19 08:01:13'),
(660, 448, 5, NULL, 350000.00, NULL, NULL, '2026-06-23 01:18:36', '2026-06-23 01:18:36'),
(661, 448, 6, NULL, 550000.00, '3', 'CANCELLED', '2026-06-23 01:18:36', '2026-08-08 00:19:31'),
(671, 455, 7, 25, 750000.00, NULL, NULL, '2026-06-23 03:14:36', '2026-06-24 23:39:18'),
(677, 461, 6, NULL, 550000.00, NULL, NULL, '2026-06-24 23:22:38', '2026-06-24 23:22:38'),
(688, 470, 8, NULL, 1200000.00, NULL, NULL, '2026-06-26 06:37:22', '2026-06-26 07:44:14'),
(689, 470, 5, NULL, 350000.00, 'Khách tự hủy phòng lẻ trên website', 'CANCELLED', '2026-06-26 06:37:22', '2026-06-26 06:38:33'),
(690, 470, 7, NULL, 750000.00, 'Khách tự hủy phòng lẻ trên website', 'CANCELLED', '2026-06-26 06:37:22', '2026-06-26 06:38:44'),
(696, 474, 5, NULL, 350000.00, 'Khách tự hủy', 'CANCELLED', '2026-06-26 07:41:48', '2026-06-26 07:42:04'),
(703, 479, 5, NULL, 350000.00, 'Khách tự hủy', 'CANCELLED', '2026-06-26 08:14:15', '2026-06-26 08:14:42'),
(708, 482, 5, NULL, 350000.00, NULL, NULL, '2026-06-29 11:10:28', '2026-06-29 11:10:28'),
(709, 482, 8, NULL, 1200000.00, 'a', 'CANCELLED', '2026-06-29 11:10:28', '2026-06-29 11:10:44'),
(713, 484, 7, NULL, 750000.00, 'Hủy theo đơn tổng: Khách tự hủy', 'CANCELLED', '2026-06-30 07:10:35', '2026-06-30 07:22:14'),
(717, 486, 5, NULL, 350000.00, 'Tự động hủy do quá hạn thanh toán', 'CANCELLED', '2026-06-30 23:48:09', '2026-07-01 00:03:30'),
(718, 486, 5, NULL, 350000.00, 'Tự động hủy do quá hạn thanh toán', 'CANCELLED', '2026-06-30 23:48:09', '2026-07-01 00:03:30'),
(719, 486, 7, NULL, 750000.00, 'Tự động hủy do quá hạn thanh toán', 'CANCELLED', '2026-06-30 23:48:09', '2026-07-01 00:03:30');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `booking_services`
--

DROP TABLE IF EXISTS `booking_services`;
CREATE TABLE IF NOT EXISTS `booking_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `price_at_order` decimal(38,2) NOT NULL,
  `status` enum('ACTIVE','CANCELLED') DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhhofk6n050slfqp0v6e65axk3` (`service_id`),
  KEY `FK1etky587qu1tqlr3t1r7w59gx` (`booking_id`)
) ENGINE=InnoDB AUTO_INCREMENT=442 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `booking_services`
--

INSERT INTO `booking_services` (`id`, `booking_id`, `service_id`, `quantity`, `price_at_order`, `status`, `created_at`, `updated_at`) VALUES
(170, 255, 9, 1, 20000.00, NULL, '2026-05-15 10:27:41.306251', '2026-05-15 10:27:41.306251'),
(171, 257, 7, 1, 120000.00, NULL, '2026-05-15 14:15:05.979761', '2026-05-15 14:15:05.979761'),
(172, 258, 5, 1, 50000.00, NULL, '2026-05-15 14:21:45.461085', '2026-05-15 14:21:45.461085'),
(174, 258, 6, 2, 150000.00, NULL, '2026-05-16 13:24:13.431926', '2026-05-16 13:24:13.431926'),
(175, 258, 5, 1, 50000.00, NULL, '2026-05-16 13:24:36.120692', '2026-05-16 13:24:36.120692'),
(296, 388, 9, 1, 20000.00, 'CANCELLED', '2026-06-20 14:39:18.748177', '2026-06-21 22:19:34.250522'),
(297, 389, 5, 1, 50000.00, 'ACTIVE', '2026-06-20 14:51:08.395056', '2026-06-20 14:51:08.395056'),
(298, 390, 5, 1, 50000.00, 'ACTIVE', '2026-06-20 14:56:50.657194', '2026-06-20 14:56:50.657194'),
(299, 391, 9, 1, 20000.00, 'ACTIVE', '2026-06-20 15:04:03.012927', '2026-06-20 15:04:03.012927'),
(300, 392, 5, 1, 50000.00, 'ACTIVE', '2026-06-20 15:19:01.146575', '2026-06-20 15:19:01.146575'),
(320, 412, 5, 1, 50000.00, 'ACTIVE', '2026-06-20 22:33:01.198531', '2026-06-20 22:33:01.198531'),
(335, 425, 6, 4, 150000.00, 'CANCELLED', '2026-06-21 21:09:56.658107', '2026-06-22 20:33:22.977801'),
(337, 428, 7, 3, 120000.00, 'ACTIVE', '2026-06-21 22:03:48.194757', '2026-06-21 22:46:44.660561'),
(338, 425, 8, 1, 300000.00, 'ACTIVE', '2026-06-22 20:33:16.501035', '2026-06-22 20:33:16.501035'),
(368, 440, 5, 1, 50000.00, 'ACTIVE', '2026-08-20 14:54:09.602592', '2026-08-20 14:54:09.603580'),
(377, 448, 6, 1, 150000.00, 'ACTIVE', '2026-06-23 08:18:35.796255', '2026-06-23 08:18:35.796255'),
(378, 448, 8, 1, 300000.00, 'ACTIVE', '2026-06-23 08:18:35.799250', '2026-06-23 08:18:35.799250'),
(379, 448, 5, 1, 50000.00, 'ACTIVE', '2026-06-23 08:18:35.803261', '2026-06-23 08:18:35.803261'),
(394, 461, 8, 1, 300000.00, 'ACTIVE', '2026-06-25 06:22:38.052549', '2026-06-25 06:22:38.052549'),
(407, 470, 6, 2, 150000.00, 'CANCELLED', '2026-06-26 13:37:22.015147', '2026-06-26 13:38:28.964001'),
(408, 470, 5, 1, 50000.00, 'CANCELLED', '2026-06-26 13:37:22.017149', '2026-06-29 06:47:24.820465'),
(415, 474, 7, 1, 120000.00, 'CANCELLED', '2026-06-26 14:41:47.633124', '2026-06-26 14:42:04.319657'),
(416, 474, 8, 2, 300000.00, 'CANCELLED', '2026-06-26 14:41:47.636125', '2026-06-26 14:42:04.320657'),
(424, 479, 8, 1, 300000.00, 'CANCELLED', '2026-06-26 15:14:15.081203', '2026-06-26 15:14:41.972440'),
(428, 482, 5, 1, 50000.00, 'ACTIVE', '2026-06-29 18:10:28.199445', '2026-06-29 18:10:28.199445'),
(429, 482, 7, 1, 120000.00, 'ACTIVE', '2026-06-29 18:10:53.160166', '2026-06-29 18:10:53.160166'),
(430, 482, 8, 1, 300000.00, 'ACTIVE', '2026-06-29 18:11:05.644392', '2026-06-29 18:11:05.644392'),
(432, 484, 6, 1, 150000.00, 'CANCELLED', '2026-06-30 14:10:35.296911', '2026-06-30 14:22:14.224769'),
(435, 486, 5, 1, 50000.00, 'CANCELLED', '2026-07-01 06:48:09.056725', '2026-07-01 07:03:30.458078');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `customers`
--

DROP TABLE IF EXISTS `customers`;
CREATE TABLE IF NOT EXISTS `customers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `identity_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `uc_phone_number` (`phone_number`),
  UNIQUE KEY `uc_identity_card` (`identity_card`),
  KEY `idx_customers_user` (`user_id`),
  KEY `idx_customers_search` (`full_name`,`phone_number`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `customers`
--

INSERT INTO `customers` (`id`, `user_id`, `full_name`, `phone_number`, `identity_card`, `email`, `gender`, `nationality`, `address`, `birthday`, `is_active`, `created_at`, `updated_at`) VALUES
(19, 6, 'Nguyễn Văn User', '0999999998', '089204012004', 'user@gmail.com', 'FEMALE', 'Việt Nam', 'abc', '2026-05-13', 1, '2026-05-15 03:18:30', '2026-06-30 13:54:03');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `employees`
--

DROP TABLE IF EXISTS `employees`;
CREATE TABLE IF NOT EXISTS `employees` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_employee_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `employees`
--

INSERT INTO `employees` (`id`, `user_id`, `full_name`, `phone_number`, `email`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 2, 'Nguyễn Phú Thịnh (Admin)', NULL, 'thinh.admin@hotel.com', b'1', '2026-06-15 07:14:47', '2026-06-30 06:51:37'),
(2, 3, 'Nguyễn Văn An (Lễ Tân)', '0907654321', 'an.letan@hotel.com', b'1', '2026-06-15 07:14:47', '2026-06-25 10:30:29');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint DEFAULT NULL,
  `amount` decimal(38,2) DEFAULT NULL,
  `payment_type` varchar(20) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc52o2b1jkxttngufqp3t7jr3h` (`booking_id`)
) ENGINE=InnoDB AUTO_INCREMENT=483 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `payments`
--

INSERT INTO `payments` (`id`, `booking_id`, `amount`, `payment_type`, `method`, `transaction_id`, `status`, `paid_at`, `created_at`, `updated_at`) VALUES
(48, 255, 148000.00, 'FINAL_PAYMENT', 'VNPAY', '15539480', 'SUCCESS', '2026-05-15 10:28:05', '2026-05-15 10:28:05.486720', '2026-05-15 10:28:05.486720'),
(49, 255, 148000.00, 'FINAL_PAYMENT', 'VNPAY', 'PAY255433', 'SUCCESS', '2026-05-15 10:28:06', '2026-05-15 10:28:05.650716', '2026-05-15 10:28:05.650716'),
(50, 257, 1448000.00, 'FINAL_PAYMENT', 'VNPAY', '15539775', 'SUCCESS', '2026-05-15 14:15:27', '2026-05-15 14:15:27.090824', '2026-05-15 14:15:27.090824'),
(51, 257, 1448000.00, 'FINAL_PAYMENT', 'VNPAY', 'PAY257125', 'SUCCESS', '2026-05-15 14:15:27', '2026-05-15 14:15:27.162825', '2026-05-15 14:15:27.162825'),
(52, 258, 800000.00, 'FINAL_PAYMENT', 'VNPAY', '15539789', 'SUCCESS', '2026-05-15 14:22:02', '2026-05-15 14:22:02.182484', '2026-05-15 14:22:02.182484'),
(53, 258, 800000.00, 'FINAL_PAYMENT', 'VNPAY', 'PAY258358', 'SUCCESS', '2026-05-15 14:22:02', '2026-05-15 14:22:02.349485', '2026-05-15 14:22:02.349485'),
(54, 257, 2172000.00, 'FINAL_PAYMENT', 'CASH', 'OUT-257-1778911572067', 'SUCCESS', NULL, '2026-05-16 13:06:12.078766', '2026-05-16 13:06:12.078766'),
(57, 258, 1550000.00, 'FINAL_PAYMENT', 'CASH', 'OUT-258-1778917052347', 'SUCCESS', NULL, '2026-05-16 14:37:32.380958', '2026-05-16 14:37:32.380958'),
(60, 255, 222000.00, 'FINAL_PAYMENT', 'CASH', 'OUT-255-1778920800287', 'SUCCESS', NULL, '2026-05-16 15:40:00.311682', '2026-05-16 15:40:00.311682'),
(277, 388, 448000.00, 'DEPOSIT', 'VNPAY', '15591225', 'SUCCESS', '2026-06-20 14:39:37', '2026-06-20 14:39:37.257693', '2026-06-20 14:39:37.257693'),
(278, 390, 540000.00, 'DEPOSIT', 'VNPAY', '15591241', 'SUCCESS', '2026-06-20 14:57:12', '2026-06-20 14:57:11.546680', '2026-06-20 14:57:11.546680'),
(279, 391, 448000.00, 'DEPOSIT', 'VNPAY', '15591246', 'SUCCESS', '2026-06-20 15:04:18', '2026-06-20 15:04:18.002425', '2026-06-20 15:04:18.002425'),
(280, 391, 39000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1781942672796', 'SUCCESS', '2026-06-20 15:04:33', '2026-06-20 15:04:32.797537', '2026-06-20 15:04:32.797537'),
(281, 392, 160000.00, 'DEPOSIT', 'VNPAY', '15591254', 'SUCCESS', '2026-06-20 15:19:14', '2026-06-20 15:19:14.269417', '2026-06-20 15:19:14.269417'),
(282, 390, 270000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1781945292055', 'SUCCESS', '2026-06-20 15:48:12', '2026-06-20 15:48:12.060129', '2026-06-20 15:48:12.060129'),
(283, 392, 160000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1781945507058', 'SUCCESS', '2026-06-20 15:51:47', '2026-06-20 15:51:47.071990', '2026-06-20 15:51:47.071990'),
(284, 391, 204500.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1781945906837', 'SUCCESS', '2026-06-20 15:58:27', '2026-06-20 15:58:26.837699', '2026-06-20 15:58:26.837699'),
(339, 412, 640000.00, 'DEPOSIT', 'VNPAY', '15591561', 'SUCCESS', '2026-06-20 22:33:27', '2026-06-20 22:33:26.878508', '2026-06-20 22:33:26.878508'),
(340, 412, 240000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1781969867639', 'SUCCESS', '2026-06-20 22:37:48', '2026-06-20 22:37:47.639624', '2026-06-20 22:37:47.639624'),
(341, 412, 400000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1781969876611', 'SUCCESS', '2026-06-20 22:37:57', '2026-06-20 22:37:56.611049', '2026-06-20 22:37:56.611049'),
(372, 425, 820000.00, 'DEPOSIT', 'VNPAY', '15592280', 'SUCCESS', '2026-06-21 21:10:14', '2026-06-21 21:09:56.667106', '2026-06-21 21:10:13.663416'),
(376, 428, 188000.00, 'DEPOSIT', 'VNPAY', '15592346', 'SUCCESS', '2026-06-21 22:04:02', '2026-06-21 22:03:48.208751', '2026-06-21 22:04:02.452395'),
(377, 388, 39000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1782055164990', 'SUCCESS', '2026-06-21 22:19:25', '2026-06-21 22:19:24.991988', '2026-06-21 22:19:24.991988'),
(378, 388, 204500.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1782055174229', 'SUCCESS', '2026-06-21 22:19:34', '2026-06-21 22:19:34.234519', '2026-06-21 22:19:34.234519'),
(382, 428, 627000.00, 'FINAL_PAYMENT', 'VNPAY', 'VNPAY-1784099782866-667', 'PENDING', '2026-07-15 14:16:23', '2026-07-15 14:16:22.893879', '2026-07-15 14:16:22.893879'),
(384, 428, 627000.00, 'FINAL_PAYMENT', 'VNPAY', 'VNPAY-1784098391370-652', 'PENDING', '2026-07-15 13:53:11', '2026-07-15 13:53:11.374379', '2026-07-15 13:53:11.374379'),
(385, 428, 627000.00, 'FINAL_PAYMENT', 'VNPAY', '15594204', 'SUCCESS', NULL, '2026-07-15 13:53:41.641133', '2026-07-15 13:53:41.641133'),
(398, 440, 640000.00, 'DEPOSIT', 'VNPAY', '15594251', 'SUCCESS', '2026-08-20 14:54:34', '2026-08-20 14:54:09.605582', '2026-08-20 14:54:33.974181'),
(400, 440, 135000.00, 'REFUND', 'CASH', 'RFND-MANUAL-1788335108842', 'SUCCESS', '2026-09-02 07:45:09', '2026-09-21 14:25:02.219146', '2026-09-02 07:45:08.850082'),
(412, 448, 560000.00, 'DEPOSIT', 'VNPAY', '15595185', 'SUCCESS', '2026-06-23 08:19:00', '2026-06-23 08:18:35.805246', '2026-06-23 08:18:59.556120'),
(415, 448, 395000.00, 'FINAL_PAYMENT', 'CASH', 'OUT-448-1786346810440', 'SUCCESS', NULL, '2026-08-10 07:26:50.442740', '2026-08-10 07:26:50.442740'),
(427, 455, 140000.00, 'DEPOSIT', 'VNPAY', '15595526', 'SUCCESS', '2026-06-23 10:14:54', '2026-06-23 10:14:35.517702', '2026-06-23 10:14:54.214595'),
(435, 461, 560000.00, 'DEPOSIT', 'VNPAY', '15598403', 'SUCCESS', '2026-06-25 06:23:03', '2026-06-25 06:22:38.059580', '2026-06-25 06:23:02.736629'),
(444, 470, 1300000.00, 'DEPOSIT', 'VNPAY', '15600554', 'SUCCESS', '2026-06-26 13:37:57', '2026-06-26 13:37:22.019147', '2026-06-26 13:37:56.522902'),
(445, 470, 550000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1782481124455', 'SUCCESS', '2026-06-26 13:38:44', '2026-06-26 13:38:44.456244', '2026-06-26 13:38:44.456244'),
(451, 474, 428000.00, 'DEPOSIT', 'CASH', 'CASH-1782484907642', 'SUCCESS', '2026-06-26 14:41:48', '2026-06-26 14:41:47.643125', '2026-06-26 14:41:47.643125'),
(452, 474, 428000.00, 'REFUND', 'BANK_TRANSFER', 'RFND-MANUAL-1782484924300', 'PENDING', NULL, '2026-06-26 14:42:04.301660', '2026-06-26 14:42:04.301660'),
(461, 479, 260000.00, 'DEPOSIT', 'CASH', 'CASH-1782486855086', 'SUCCESS', '2026-06-26 15:14:15', '2026-06-26 15:14:15.087201', '2026-06-26 15:14:15.087201'),
(462, 479, 260000.00, 'REFUND', 'BANK_TRANSFER', 'RFND-MANUAL-1782486881950', 'SUCCESS', '2026-06-26 15:17:35', '2026-06-26 15:14:41.951426', '2026-06-26 15:17:34.866169'),
(467, 482, 640000.00, 'DEPOSIT', 'CASH', 'CASH-1782756628202', 'SUCCESS', '2026-06-29 18:10:28', '2026-06-29 18:10:28.202445', '2026-06-29 18:10:28.202445'),
(468, 482, 180000.00, 'FINAL_PAYMENT', 'CASH', 'OUT-482-1782756668892', 'SUCCESS', '2026-06-29 18:11:09', '2026-06-29 18:11:08.893929', '2026-06-29 18:11:08.893929'),
(472, 484, 360000.00, 'DEPOSIT', 'VNPAY', '15605138', 'SUCCESS', '2026-06-30 14:18:10', '2026-06-30 14:10:35.308917', '2026-06-30 14:18:10.140814'),
(474, 484, 360000.00, 'REFUND', 'VNPAY', 'MOCK_VNP_REFUND_1782829334210', 'SUCCESS', '2026-06-30 14:22:14', '2026-06-30 14:22:14.211787', '2026-06-30 14:22:14.211787'),
(476, 486, 1180000.00, 'DEPOSIT', 'VNPAY', 'VNPAY-1782888489057-250', 'FAILED', '2026-07-01 06:48:09', '2026-07-01 06:48:09.058724', '2026-07-01 07:03:30.453075');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `roles`
--

INSERT INTO `roles` (`id`, `name`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'ROLE_ADMIN', 1, '2026-03-23 12:11:22', '2026-03-23 12:11:22'),
(2, 'ROLE_RECEPTIONIST', 1, '2026-03-23 12:11:22', '2026-06-15 14:13:10'),
(3, 'ROLE_USER', 1, '2026-03-23 12:11:22', '2026-03-23 12:11:22');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `rooms`
--

DROP TABLE IF EXISTS `rooms`;
CREATE TABLE IF NOT EXISTS `rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_number` varchar(10) NOT NULL,
  `room_type_id` bigint DEFAULT NULL,
  `status` varchar(20) DEFAULT 'AVAILABLE',
  `floor` int DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `room_number` (`room_number`),
  KEY `idx_room_type` (`room_type_id`),
  KEY `idx_rooms_room_type` (`room_type_id`),
  KEY `idx_rooms_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `rooms`
--

INSERT INTO `rooms` (`id`, `room_number`, `room_type_id`, `status`, `floor`, `is_active`, `created_at`, `updated_at`) VALUES
(22, '101', 4, 'AVAILABLE', 1, 1, '2026-05-09 16:33:15', '2026-06-25 14:36:46'),
(23, '102', 4, 'AVAILABLE', 1, 1, '2026-05-09 16:33:15', '2026-06-26 21:44:54'),
(24, '201', 4, 'AVAILABLE', 2, 1, '2026-05-09 16:33:15', '2026-06-26 21:44:56'),
(25, '103', 5, 'AVAILABLE', 1, 1, '2026-05-09 16:33:15', '2026-06-26 21:44:58'),
(26, '104', 5, 'AVAILABLE', 1, 1, '2026-05-09 16:33:15', '2026-06-26 15:15:53'),
(27, '202', 5, 'CLEANING', 2, 1, '2026-05-09 16:33:15', '2026-06-26 15:16:55'),
(28, '301', 6, 'AVAILABLE', 3, 1, '2026-05-09 16:33:15', '2026-05-20 13:30:39'),
(29, '302', 6, 'OCCUPIED', 3, 1, '2026-05-09 16:33:15', '2026-07-01 07:10:41'),
(30, '401', 6, 'AVAILABLE', 4, 1, '2026-05-09 16:33:15', '2026-05-20 17:50:01'),
(31, '303', 7, 'AVAILABLE', 3, 1, '2026-05-09 16:33:15', '2026-06-22 21:23:16'),
(32, '304', 7, 'AVAILABLE', 3, 1, '2026-05-09 16:33:15', '2026-06-22 21:23:16'),
(33, '402', 7, 'OCCUPIED', 4, 1, '2026-05-09 16:33:15', '2026-07-01 07:10:46'),
(34, '501', 8, 'AVAILABLE', 5, 1, '2026-05-09 16:33:15', '2026-06-19 20:21:16'),
(35, '502', 8, 'AVAILABLE', 5, 1, '2026-05-09 16:33:15', '2026-06-19 20:21:18');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `room_types`
--

DROP TABLE IF EXISTS `room_types`;
CREATE TABLE IF NOT EXISTS `room_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `max_guest` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `room_types`
--

INSERT INTO `room_types` (`id`, `name`, `price`, `max_guest`, `description`, `image_url`, `is_active`, `created_at`, `updated_at`) VALUES
(4, 'Standard Double', 350000.00, 2, 'Phòng tiêu chuẩn, 1 giường đôi, hướng nội khu', '/uploads/standard_double.jpg', 1, '2026-05-09 16:07:54', '2026-05-12 21:47:48'),
(5, 'Standard Twin', 350000.00, 2, 'Phòng tiêu chuẩn, 2 giường đơn, hướng phố', '/uploads/standard_twin.jpg', 1, '2026-05-09 16:07:54', '2026-05-12 21:48:01'),
(6, 'Deluxe City View', 550000.00, 3, 'Phòng cao cấp, hướng thành phố, đầy đủ tiện nghi', '/uploads/deluxe_city_view.jpg', 1, '2026-05-09 16:07:54', '2026-06-30 14:39:01'),
(7, 'Deluxe Ocean View', 750000.00, 3, 'Phòng cao cấp, hướng biển trực diện, có ban công', '/uploads/deluxe_ocean_view.jpg', 1, '2026-05-09 16:07:54', '2026-05-12 21:48:13'),
(8, 'Suite Family', 1200000.00, 4, 'Phòng hạng sang cho gia đình, view toàn cảnh', '/uploads/suite_family.jpg', 1, '2026-05-09 16:07:54', '2026-05-12 21:48:15');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `services`
--

DROP TABLE IF EXISTS `services`;
CREATE TABLE IF NOT EXISTS `services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_name` varchar(255) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `service_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'REGULAR',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `services`
--

INSERT INTO `services` (`id`, `service_name`, `price`, `description`, `service_type`, `is_active`, `created_at`, `updated_at`) VALUES
(5, 'Giặt ủi', 50000.00, 'Giặt sấy quần áo tính theo kg, lấy trong ngày.', 'REGULAR', 1, '2026-05-12 14:16:07', '2026-05-12 14:16:07'),
(6, 'Thuê xe máy', 150000.00, 'Xe tay ga đời mới, bao gồm 2 mũ bảo hiểm.', 'REGULAR', 1, '2026-05-12 14:16:07', '2026-05-12 14:16:07'),
(7, 'Ăn sáng Buffet', 120000.00, 'Phục vụ tại nhà hàng tầng trệt từ 6h-10h.', 'REGULAR', 1, '2026-05-12 14:16:07', '2026-05-12 14:16:07'),
(8, 'Đưa đón sân bay', 300000.00, 'Xe 4 chỗ đưa đón sân bay Tân Sơn Nhất.', 'REGULAR', 1, '2026-05-12 14:16:07', '2026-05-12 14:16:07'),
(9, 'Nước suối/Nước ngọt', 20000.00, 'Cung cấp thêm nước uống tại phòng.', 'REGULAR', 1, '2026-05-12 14:16:07', '2026-05-12 14:16:07'),
(10, 'Giường phụ (Extra Bed)', 300000.00, 'Thêm 01 giường đơn trong phòng, bao gồm đầy đủ chăn gối.', 'ADDITIONAL', 1, '2026-05-12 14:17:45', '2026-05-12 15:07:34'),
(14, 'Dịch vụ Spa massage', 250000.00, 'Spa thư giãn', 'REGULAR', 1, '2026-06-30 14:40:10', '2026-06-30 14:40:10');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` bigint DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `password_changed_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_users_role` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role_id`, `is_active`, `created_at`, `updated_at`, `password_changed_at`) VALUES
(2, 'admin', '$2a$10$6tc6qng77ZyeveLNubMiHeMzD3i7L9SSRdkpH97vcgcBMkUfyN.pG', 1, 1, '2026-06-15 14:14:47', '2026-07-17 08:30:47', NULL),
(3, 'letan', '$2a$10$xh2.jhn.iiAZyEGYLN9GSukUXkLNXCAaqXYtBChkfxDouPYZjyEXO', 2, 1, '2026-06-15 14:14:47', '2026-07-05 06:13:23', NULL),
(6, 'user', '$2a$10$kD93c.dCePI1JNfuzVWpGuej1ROoReNPxiXk1D311obSzJSSCJnnq', 3, 1, '2026-05-15 10:18:30', '2026-07-05 06:15:13', NULL);

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `FK_audit_logs_employees` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `FKbvfibgflhsb0g2hnjauiv5khs` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`);

--
-- Các ràng buộc cho bảng `booking_rooms`
--
ALTER TABLE `booking_rooms`
  ADD CONSTRAINT `FK71qgxnmri4s08xrlny5wptrej` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  ADD CONSTRAINT `FKcjk0abrppkbsw5w03uq8tvgfc` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
  ADD CONSTRAINT `FKuwvjd8ongt6cah39rnj4xbv1` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`);

--
-- Các ràng buộc cho bảng `booking_services`
--
ALTER TABLE `booking_services`
  ADD CONSTRAINT `FK1etky587qu1tqlr3t1r7w59gx` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  ADD CONSTRAINT `FKhhofk6n050slfqp0v6e65axk3` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`);

--
-- Các ràng buộc cho bảng `customers`
--
ALTER TABLE `customers`
  ADD CONSTRAINT `FKrh1g1a20omjmn6kurd35o3eit` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `FK_employees_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

--
-- Các ràng buộc cho bảng `rooms`
--
ALTER TABLE `rooms`
  ADD CONSTRAINT `FKh9m2n1paq5hmd3u0klfl7wsfv` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`);

--
-- Các ràng buộc cho bảng `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
