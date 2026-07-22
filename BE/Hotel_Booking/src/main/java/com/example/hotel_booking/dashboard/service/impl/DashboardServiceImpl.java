package com.example.hotel_booking.dashboard.service.impl;

import com.example.hotel_booking.booking.entity.Booking;
import com.example.hotel_booking.booking.repository.BookingRepository;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.RoomStatus;
import com.example.hotel_booking.dashboard.dto.DashboardDTO;
import com.example.hotel_booking.dashboard.service.DashboardService;
import com.example.hotel_booking.room.entity.Room;
import com.example.hotel_booking.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {


    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Override
    public DashboardDTO.OperationalStatsResponse getOperationalStats() {

        LocalDate businessDate = LocalDate.now();

        LocalDateTime startOfDay = businessDate.atStartOfDay();
        LocalDateTime endOfDay = businessDate.atTime(23, 59, 59, 999999999);

        long todayCheckInCount =
                bookingRepository.countTodayCheckIns(startOfDay, endOfDay);

        long todayCheckOutCount =
                bookingRepository.countTodayCheckOuts(startOfDay, endOfDay);

        long availableRooms =
                roomRepository.countByStatus(RoomStatus.AVAILABLE);

        long totalRooms = roomRepository.countByActiveTrue();

        long occupiedRooms =
                roomRepository.countByStatus(RoomStatus.OCCUPIED);

        long cleaningRooms =
                roomRepository.countByStatus(RoomStatus.CLEANING);

        long maintenanceRooms =
                roomRepository.countByStatus(RoomStatus.MAINTENANCE);

        List<DashboardDTO.BookingShortInfo> todayCheckInList =
                bookingRepository.findTodayCheckInList(startOfDay, endOfDay)
                        .stream()
                        .map(this::mapToBookingShortInfo)
                        .collect(Collectors.toList());

        List<DashboardDTO.BookingShortInfo> todayCheckOutList =
                bookingRepository.findTodayCheckOutList(startOfDay, endOfDay)
                        .stream()
                        .map(this::mapToBookingShortInfo)
                        .collect(Collectors.toList());

        List<Room> allRooms = roomRepository.findAllWithRoomType();

        List<DashboardDTO.RoomTypeGroup> roomTypeGroups =
                allRooms.stream()
                        .collect(Collectors.groupingBy(
                                room -> room.getRoomType().getName()
                        ))
                        .entrySet()
                        .stream()
                        .sorted(java.util.Map.Entry.comparingByKey())
                        .map(entry ->
                                DashboardDTO.RoomTypeGroup.builder()
                                        .roomTypeName(entry.getKey())
                                        .rooms(
                                                entry.getValue()
                                                        .stream()
                                                        .sorted(
                                                                Comparator.comparing(
                                                                        Room::getRoomNumber
                                                                )
                                                        )
                                                        .map(room ->
                                                                DashboardDTO.RoomBasicInfo.builder()
                                                                        .id(room.getId())
                                                                        .roomNumber(room.getRoomNumber())
                                                                        .status(room.getStatus().name())
                                                                        .build()
                                                        )
                                                        .collect(Collectors.toList())
                                        )
                                        .build()
                        )
                        .collect(Collectors.toList());

        return DashboardDTO.OperationalStatsResponse.builder()
                .businessDate(businessDate)

                .todayCheckInCount(todayCheckInCount)
                .todayCheckOutCount(todayCheckOutCount)

                .availableRooms(availableRooms)
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .cleaningRooms(cleaningRooms)
                .maintenanceRooms(maintenanceRooms)

                .todayCheckInList(todayCheckInList)
                .todayCheckOutList(todayCheckOutList)

                .roomTypeGroups(roomTypeGroups)
                .build();
    }

    @Override
    public DashboardDTO.AnalyticalStatsResponse getAnalyticalStats(LocalDate startDate, LocalDate endDate) {

        
        if (startDate == null) startDate = YearMonth.now().atDay(1);
        if (endDate == null) endDate = YearMonth.now().atEndOfMonth();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59, 999999999);

        List<BookingStatus> revenueStatuses = List.of(
                BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN, BookingStatus.CHECKED_OUT
        );

        
        BigDecimal totalRevenue = bookingRepository.calculateTotalRevenueByDateRange(revenueStatuses, start, end);
        totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;

        long totalBookings = bookingRepository.countSuccessfulBookingsByDateRange(revenueStatuses, start, end);

        
        long occupiedRooms = bookingRepository.countOccupiedRoomsInDateRange(start, end);
        long totalRooms = roomRepository.countByActiveTrue();

        
        long totalDaysInPeriod = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        long totalCapacityInNights = totalRooms * totalDaysInPeriod;

        double occupancyRate = (totalCapacityInNights > 0)
                ? (double) occupiedRooms * 100 / totalCapacityInNights
                : 0.0;

        
        double revPar = (totalCapacityInNights > 0)
                ? totalRevenue.divide(BigDecimal.valueOf(totalCapacityInNights), 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        
        double adr = (totalBookings > 0)
                ? totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        
        List<DashboardDTO.StatusDistribution> statusDistributions = bookingRepository.countByEachStatusByDateRange(start, end)
                .stream()
                .map(row -> DashboardDTO.StatusDistribution.builder()
                        .status(row[0].toString())
                        .count((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        
        List<DashboardDTO.DailyRevenue> dailyRevenues = bookingRepository.getDailyRevenueByDateRange(revenueStatuses, start, end)
                .stream()
                .map(row -> DashboardDTO.DailyRevenue.builder()
                        .date(((java.sql.Date) row[0]).toLocalDate())
                        .totalAmount((BigDecimal) row[1])
                        .build())
                .collect(Collectors.toList());

        
        List<DashboardDTO.MonthlyRevenue> monthlyRevenues = bookingRepository.getMonthlyRevenueByDateRange(revenueStatuses, start, end)
                .stream()
                .map(row -> DashboardDTO.MonthlyRevenue.builder()
                        .year(((Number) row[0]).intValue())
                        .month(((Number) row[1]).intValue())
                        .totalAmount((BigDecimal) row[2])
                        .build())
                .collect(Collectors.toList());

        
        return DashboardDTO.AnalyticalStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .occupancyRate(occupancyRate)
                .adr(adr)
                .revPar(revPar)
                .totalBookings(totalBookings)
                .confirmedBookings(bookingRepository.countByStatusAndDateRange(BookingStatus.CONFIRMED, start, end))
                .checkedInBookings(bookingRepository.countByStatusAndDateRange(BookingStatus.CHECKED_IN, start, end))
                .checkedOutBookings(bookingRepository.countByStatusAndDateRange(BookingStatus.CHECKED_OUT, start, end))
                .cancelledBookings(bookingRepository.countByStatusAndDateRange(BookingStatus.CANCELLED, start, end))
                .statusDistributions(statusDistributions)
                .dailyRevenues(dailyRevenues)
                .monthlyRevenues(monthlyRevenues)
                .build();
    }

    private DashboardDTO.BookingShortInfo mapToBookingShortInfo(
            Booking booking
    ) {

        return DashboardDTO.BookingShortInfo.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .customerName(
                        booking.getContactName() != null
                                ? booking.getContactName()
                                : "Khách vãng lai"
                )
                .contactPhone(booking.getContactPhone())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .build();
    }


}
