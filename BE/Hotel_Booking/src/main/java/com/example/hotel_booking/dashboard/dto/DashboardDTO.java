package com.example.hotel_booking.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardDTO {






    @Data
    @Builder
    public static class OperationalStatsResponse {

        private LocalDate businessDate;
        private long totalRooms;

        private long todayCheckInCount;
        private long todayCheckOutCount;

        private long availableRooms;
        private long occupiedRooms;
        private long cleaningRooms;
        private long maintenanceRooms;

        private List<RoomTypeGroup> roomTypeGroups;

        private List<BookingShortInfo> todayCheckInList;
        private List<BookingShortInfo> todayCheckOutList;
    }

    @Data
    @Builder
    public static class RoomTypeGroup {
        private String roomTypeName;
        private List<RoomBasicInfo> rooms;
    }

    @Data
    @Builder
    public static class RoomBasicInfo {
        private Long id;
        private String roomNumber;
        private String status;
    }





    @Data
    @Builder
    public static class AnalyticalStatsResponse {

        private BigDecimal totalRevenue;

        private double occupancyRate;

        private double adr;

        private double revPar;

        private long totalBookings;

        private long confirmedBookings;

        private long checkedInBookings;

        private long checkedOutBookings;

        private long cancelledBookings;

        private List<StatusDistribution> statusDistributions;

        private List<DailyRevenue> dailyRevenues;

        private List<MonthlyRevenue> monthlyRevenues;
    }





    @Data
    @Builder
    public static class StatusDistribution {
        private String status;
        private long count;
    }

    @Data
    @Builder
    public static class DailyRevenue {

        private LocalDate date;

        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    public static class MonthlyRevenue {
        private int year;
        private int month;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    public static class BookingShortInfo {
        private Long id;
        private String bookingCode;
        private String customerName;
        private String contactPhone;
        private BigDecimal totalAmount;
        private String status;
    }


}
