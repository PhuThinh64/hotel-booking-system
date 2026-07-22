import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';


import AdminLayout from './layouts/AdminLayout';
import CustomerLayout from './layouts/CustomerLayout';


import ProtectedRoute from './components/ProtectedRoute'; 



import RoomList from './pages/customer/RoomList';
import AuthPage from './pages/customer/AuthPage';
import UserProfile from './pages/customer/UserProfile';
import MyBookings from './pages/customer/MyBookings';
import PaymentResult from './pages/customer/PaymentResult';
import ResetPasswordPage from './pages/customer/ResetPasswordPage';


import DashboardPage from './pages/admin/DashboardPage';
import BookingPage from './pages/admin/booking/BookingPage';
import RoomPage from './pages/admin/RoomPage';
import RoomTypePage from './pages/admin/RoomTypePage';
import ServicePage from './pages/admin/ServicePage';
import CustomerPage from './pages/admin/CustomerPage';
import EmployeePage from './pages/admin/EmployeePage';
import AdminProfile from './pages/admin/AdminProfile';
import RefundManagement from './pages/admin/RefundManagement';
import CheckoutSuccess from './pages/admin/CheckoutSuccess';
import AuditLogPage from './pages/admin/AuditLogPage';
import BookingPolicy from './pages/customer/BookingPolicy';

import './App.css';

function App() {
  return (
    <Router>
      <Routes>
        
        {/* --- 1. CÁC TRANG CÔNG KHAI (CustomerLayout) --- */}
        <Route element={<CustomerLayout />}>
          <Route path="/" element={<RoomList />} />
          <Route path="/payment-result" element={<PaymentResult />} />
          <Route path="/my-bookings" element={<MyBookings />} />
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route path="/booking-policy" element={<BookingPolicy />} />
        </Route>

        {/* --- 2. CÁC TRANG CẦN ĐĂNG NHẬP VỚI ROLE_USER --- */}
        <Route element={<ProtectedRoute allowedRoles={['ROLE_USER']} />}>
          <Route element={<CustomerLayout />}>
            <Route path="/profile" element={<UserProfile />} />
      
          </Route>
        </Route>

        {/* --- 3. CÁC TRANG CỦA ADMIN/LỄ TÂN (Đã bảo vệ bằng ProtectedRoute) --- */}
       <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_RECEPTIONIST']} />}>
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<DashboardPage />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="profile" element={<AdminProfile />} />
            <Route path="bookings" element={<BookingPage />} />
            <Route path="rooms" element={<RoomPage />} />
            <Route path="room-types" element={<RoomTypePage />} />
            <Route path="services" element={<ServicePage />} />
            <Route path="customers" element={<CustomerPage />} />
            <Route path="audit-logs" element={<AuditLogPage />} />
            <Route path="refund-management" element={<RefundManagement />} />
            <Route path="checkout-success" element={<CheckoutSuccess />} />

            <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
                <Route path="employees" element={<EmployeePage />} />
            </Route>

           </Route>
        </Route>

        {/* --- 4. CATCH-ALL ROUTE (Nếu gõ sai URL -> về trang chủ) --- */}
        <Route path="*" element={<Navigate to="/" replace />} />
        
      </Routes>
    </Router>
  );
}

export default App;