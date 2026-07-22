package com.example.hotel_booking.dashboard.service;

import com.example.hotel_booking.dashboard.dto.DashboardDTO;
import java.time.LocalDate;

public interface DashboardService {
    DashboardDTO.OperationalStatsResponse getOperationalStats();
    DashboardDTO.AnalyticalStatsResponse getAnalyticalStats(LocalDate startDate, LocalDate endDate);
}