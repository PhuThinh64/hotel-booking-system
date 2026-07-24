package com.example.hotel_booking.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Container class for Dashboard operational and analytical Data Transfer Objects")
public class DashboardDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Operational statistics response containing real-time daily metrics")
    public static class OperationalStatsResponse {

        @Schema(description = "Current hotel business date", example = "2026-07-24")
        private LocalDate businessDate;

        @Schema(description = "Total number of rooms registered in system", example = "50")
        private long totalRooms;

        @Schema(description = "Number of check-ins scheduled for today", example = "5")
        private long todayCheckInCount;

        @Schema(description = "Number of check-outs scheduled for today", example = "3")
        private long todayCheckOutCount;

        @Schema(description = "Count of currently available rooms", example = "30")
        private long availableRooms;

        @Schema(description = "Count of currently occupied rooms", example = "15")
        private long occupiedRooms;

        @Schema(description = "Count of rooms under cleaning state", example = "3")
        private long cleaningRooms;

        @Schema(description = "Count of rooms under maintenance state", example = "2")
        private long maintenanceRooms;

        @Schema(description = "List of room groups categorized by room type")
        private List<RoomTypeGroup> roomTypeGroups;

        @Schema(description = "List of bookings scheduled for check-in today")
        private List<BookingShortInfo> todayCheckInList;

        @Schema(description = "List of bookings scheduled for check-out today")
        private List<BookingShortInfo> todayCheckOutList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Group of rooms categorized by room type name")
    public static class RoomTypeGroup {

        @Schema(description = "Name of the room type", example = "Deluxe Suite")
        private String roomTypeName;

        @Schema(description = "Rooms belonging to this room type category")
        private List<RoomBasicInfo> rooms;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Basic room information for dashboard layout")
    public static class RoomBasicInfo {

        @Schema(description = "Room ID", example = "101")
        private Long id;

        @Schema(description = "Room number code", example = "301")
        private String roomNumber;

        @Schema(description = "Current operational status of the room", example = "AVAILABLE")
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Analytical statistics response containing financial and business performance metrics")
    public static class AnalyticalStatsResponse {

        @Schema(description = "Total revenue generated within the specified period", example = "150000000.00")
        private BigDecimal totalRevenue;

        @Schema(description = "Overall occupancy rate percentage", example = "75.5")
        private double occupancyRate;

        @Schema(description = "Average Daily Rate (ADR)", example = "850000.00")
        private double adr;

        @Schema(description = "Revenue Per Available Room (RevPAR)", example = "641750.00")
        private double revPar;

        @Schema(description = "Total number of bookings made within the period", example = "120")
        private long totalBookings;

        @Schema(description = "Count of confirmed bookings", example = "40")
        private long confirmedBookings;

        @Schema(description = "Count of checked-in bookings", example = "50")
        private long checkedInBookings;

        @Schema(description = "Count of checked-out bookings", example = "25")
        private long checkedOutBookings;

        @Schema(description = "Count of cancelled bookings", example = "5")
        private long cancelledBookings;

        @Schema(description = "Distribution breakdown across booking statuses")
        private List<StatusDistribution> statusDistributions;

        @Schema(description = "Daily revenue breakdown list")
        private List<DailyRevenue> dailyRevenues;

        @Schema(description = "Monthly revenue breakdown list")
        private List<MonthlyRevenue> monthlyRevenues;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Distribution count for a specific booking status")
    public static class StatusDistribution {

        @Schema(description = "Booking status name", example = "CONFIRMED")
        private String status;

        @Schema(description = "Total booking count for this status", example = "40")
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Daily revenue record")
    public static class DailyRevenue {

        @Schema(description = "Date of revenue record", example = "2026-07-24")
        private LocalDate date;

        @Schema(description = "Total revenue collected on this date", example = "5000000.00")
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Monthly revenue record")
    public static class MonthlyRevenue {

        @Schema(description = "Year", example = "2026")
        private int year;

        @Schema(description = "Month of the year (1-12)", example = "7")
        private int month;

        @Schema(description = "Total revenue collected in this month", example = "150000000.00")
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Short booking details for daily check-in/out lists")
    public static class BookingShortInfo {

        @Schema(description = "Booking ID", example = "100")
        private Long id;

        @Schema(description = "Unique booking code", example = "BK20260724001")
        private String bookingCode;

        @Schema(description = "Customer full name", example = "Nguyen Van A")
        private String customerName;

        @Schema(description = "Customer contact phone number", example = "0912345678")
        private String contactPhone;

        @Schema(description = "Total booking price", example = "1200000.00")
        private BigDecimal totalAmount;

        @Schema(description = "Current booking status", example = "CONFIRMED")
        private String status;
    }
}