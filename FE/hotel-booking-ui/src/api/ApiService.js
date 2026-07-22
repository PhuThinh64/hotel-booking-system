import axios from 'axios';
import { message } from 'antd';
import { getErrorMessage } from '../utils/ErrorMapping';

const BASE_URL = "http://localhost:8080/api/v1";
const TOKEN_KEY = 'accessToken';

const api = axios.create({
    baseURL: BASE_URL,
    timeout: 5000,
    headers: { 'Content-Type': 'application/json' },
});


api.interceptors.request.use((config) => {
    const token = localStorage.getItem(TOKEN_KEY) || localStorage.getItem('token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
}, (error) => Promise.reject(error));


api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            const currentPath = window.location.pathname;
            if (currentPath.startsWith('/admin')) {
                message.error("Phiên đăng nhập hết hạn!");
                localStorage.clear();
                window.location.href = '/auth';
            }
        } else {
            
            const backendMessage = error.response?.data?.message;
            const backendCode = error.response?.data?.code;

            
            if (backendMessage) {
                
                message.error(backendMessage);
            } else {
                
                const fallbackMessage = getErrorMessage(backendCode) || "Có lỗi xảy ra, vui lòng thử lại sau";
                message.error(fallbackMessage);
            }
        }
        return Promise.reject(error);
    }
);


const handleResponse = (response) => response.data?.result ?? response.data;


export const login = (authRequest) => api.post('/auth/login', authRequest).then(res => res.data);
export const register = (registerRequest) => api.post('/auth/register', registerRequest).then(res => res.data);
export const forgotPassword = (data) => api.post('/auth/forgot-password', data).then(res => res.data);
export const resetPassword = (data) => api.post('/auth/reset-password', data).then(res => res.data);


export const getProfile = () => api.get('/users/profile').then(handleResponse);
export const updateProfile = (data) => api.put('/users/profile/employee', data).then(handleResponse);
export const changePassword = (data) => api.put('/users/change-password', data).then(handleResponse);


export const getAllEmployees = (params) =>
    api.get('/employees', { params }).then(handleResponse);
export const getEmployeeById = (id) =>
    api.get(`/employees/${id}`).then(handleResponse);
export const createEmployee = (data) =>
    api.post('/employees', data).then(handleResponse);
export const updateEmployee = (id, data) =>
    api.put(`/employees/${id}`, data).then(handleResponse);
export const deleteEmployee = (id) =>
    api.delete(`/employees/${id}`).then(handleResponse);
export const resetEmployeePassword = (userId) =>
    api.post(`/employees/${userId}/reset-password`).then(handleResponse);


export const getExtraServices = (
    page = 0,
    size = 10,
    name = "",
    active = null,
    serviceType = null
) => {
    const params = {
        page,
        size
    };

    if (name?.trim()) {
        params.name = name.trim();
    }

    if (active !== null && active !== undefined) {
        params.active = active;
    }

    if (serviceType && serviceType !== "ALL") {
        params.serviceType = serviceType;
    }

    return api.get('/services', { params })
        .then(handleResponse);
};

export const getPublicServices = (page = 0,size = 10,name = "") => {
    const params = {
        page,
        size
    };

    if (name?.trim()) {
        params.name = name.trim();
    }

    return api.get('/services/public', { params })
        .then(handleResponse);
};

export const getExtraServiceById = (id) =>
    api.get(`/services/${id}`)
        .then(handleResponse);

export const createExtraService = (data) =>
    api.post('/services', data)
        .then(handleResponse);

export const updateExtraService = (id, data) =>
    api.put(`/services/${id}`, data)
        .then(handleResponse);

export const deleteExtraService = (id) =>
    api.delete(`/services/${id}`)
        .then(handleResponse);


export const getRooms = (params) => api.get('/rooms', { params }).then(handleResponse);
export const getRoomById = (id) => api.get(`/rooms/${id}`).then(handleResponse);
export const createRoom = (data) => api.post('/rooms', data).then(handleResponse);
export const updateRoom = (id, data) => api.put(`/rooms/${id}`, data).then(handleResponse);
export const updateRoomStatus = (roomId, status) => api.put(`/rooms/${roomId}/status`, null, { params: { status } }).then(handleResponse);
export const deleteRoom = (id) => api.delete(`/rooms/${id}`).then(handleResponse);
export const confirmCleaned = (roomId) => api.post(`/rooms/${roomId}/confirm-cleaned`).then(handleResponse);
export const getDistinctFloors = () => api.get('/rooms/floors').then(handleResponse);



export const addRoom = (bookingId, roomTypeId) => api.post('/booking-rooms/add', { bookingId, roomTypeId }).then(handleResponse);
export const getAvailableRoomTypes = (checkIn, checkOut) => api.get('/roomtype/available', { params: { checkIn, checkOut } }).then(handleResponse);
export const getAvailableRoomTypesWithCount = (checkIn, checkOut) => api.get('/roomtype/available-with-count', { params: { checkIn, checkOut } }).then(handleResponse);
export const getRoomTypes = (params) => api.get('/roomtype', { params }).then(handleResponse);
export const createRoomType = (data) => api.post('/roomtype', data).then(handleResponse);
export const updateRoomType = (id, data) => api.put(`/roomtype/${id}`, data).then(handleResponse);
export const deleteRoomType = (id) => api.delete(`/roomtype/${id}`).then(handleResponse);


export const getCustomers = (page = 0, size = 10, keyword = "", active = null) => {
    const params = { page, size };
    if (keyword) params.keyword = keyword;
    if (active !== null && active !== undefined) params.active = active;
    return api.get('/customers', { params }).then(handleResponse);
};
export const getNewCustomersCount = (startDate, endDate) => api.get('/customers/stats/count-new', { params: { startDate, endDate } }).then(handleResponse);
export const createCustomer = (data) => api.post('/customers', data).then(handleResponse);
export const updateCustomer = (id, data) => api.put(`/customers/${id}`, data).then(handleResponse);
export const updateCustomerProfile = (data) => api.put('/users/profile/customer', data).then(handleResponse);
export const getCustomerByPhone = (phoneNumber) => api.get(`/customers/phone/${phoneNumber}`).then(handleResponse);
export const deleteCustomer = (id) => api.delete(`/customers/${id}`).then(handleResponse);


export const getAllBookings = (params) => api.get('/bookings', { params: { sort: 'id,desc', ...params } }).then(handleResponse);
export const getBookingById = (id) => api.get(`/bookings/${id}`).then(handleResponse);
export const createBooking = (data) => api.post('/bookings', data).then(handleResponse);
export const checkIn = (id) => api.post(`/bookings/${id}/check-in`).then(handleResponse);
export const getCheckoutPreview = (bookingId) => api.get(`/bookings/${bookingId}/checkout-preview`).then(handleResponse);
export const checkOut = (id, paymentMethod, refundMethod) => api.post(`/bookings/${id}/check-out`, null, { params: { paymentMethod: paymentMethod || 'CASH',refundMethod: refundMethod } }).then(handleResponse);
export const cancelFullBooking = (id, refundMethod, reason) => api.post(`/bookings/${id}/cancel-full`, null, { params: { refundMethod, reason } }).then(handleResponse);
export const cancelSingleRoom = (bookingId, bookingRoomId, refundMethod, reason) => api.post(`/bookings/${bookingId}/cancel-room/${bookingRoomId}`, null, { params: { refundMethod, reason } }).then(handleResponse);
export const approveManualRefund = (bookingId, refundMethod) => api.post(`/bookings/${bookingId}/approve-manual-refund`, null, { params: { refundMethod } }).then(handleResponse);
export const confirmDeposit = (id, params) => api.post(`/bookings/${id}/confirm-deposit`, null, { params }).then(handleResponse);
export const updateBookingStatus = (id, data) => api.patch(`/bookings/${id}/status`, data).then(handleResponse);
export const lookupBooking = (phone, bookingCode) => api.get('/bookings/lookup', { params: { phone, bookingCode } }).then(handleResponse);
export const getPendingRefunds = (params) => { return api.get('/payments/pending-refunds', { params }).then(handleResponse);};


export const updateBookingRoom = (id, data) => api.put('/booking-rooms/' + id, data).then(handleResponse);
export const assignRoom = (bookingRoomId, roomId) => api.put(`/booking-rooms/${bookingRoomId}/assign?roomId=${roomId}`).then(handleResponse);
export const changeRoomType = (bookingRoomId, newRoomTypeId) =>api.put(`/booking-rooms/${bookingRoomId}/change-type?roomTypeId=${newRoomTypeId}`).then(handleResponse);


export const addService = (data) => api.post('/booking-service-details', data).then(handleResponse);
export const updateBookingService = (id, quantity) => api.put(`/booking-service-details/${id}`, { quantity }).then(handleResponse);
export const cancelBookingService = (id) => api.patch(`/booking-service-details/cancel/${id}`).then(handleResponse);
export const getBookingServices = (bookingId) => api.get(`/booking-service-details/booking/${bookingId}`).then(handleResponse);


export const getOperationalStats = () => api.get('/dashboard/operational').then(handleResponse);
export const getAnalyticalStats = (startDate, endDate) => api.get('/dashboard/analytical', { params: { startDate, endDate } }).then(handleResponse);


export const getAuditLogs = (params) => api.get('/audit-logs', { params }).then(handleResponse);

export default api;