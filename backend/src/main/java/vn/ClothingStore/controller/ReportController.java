package vn.ClothingStore.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.ClothingStore.domain.response.reports.RevenuePointDTO;
import vn.ClothingStore.domain.response.reports.RevenueReportDTO;
import vn.ClothingStore.domain.response.reports.RevenueSummaryDTO;
import vn.ClothingStore.service.ReportService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReportController {

    private final ReportService reportService;
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Bangkok"); // GMT+7

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/revenue/daily")
    @ApiMessage("Lấy báo cáo doanh thu theo ngày thành công")
    public RevenueReportDTO revenueDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) throws IdInvalidException {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IdInvalidException("Tham số ngày không hợp lệ (end phải >= start)");
        }
        List<RevenuePointDTO> points = reportService.getDaily(start, end, DEFAULT_ZONE);
        RevenueSummaryDTO summary = reportService.getSummary(start, end, DEFAULT_ZONE);
        return new RevenueReportDTO(points, summary);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/revenue/monthly")
    @ApiMessage("Lấy báo cáo doanh thu theo tháng thành công")
    public RevenueReportDTO revenueMonthly(@RequestParam int year) throws IdInvalidException {
        if (year < 2000 || year > 2100) {
            throw new IdInvalidException("Năm không hợp lệ (2000–2100)");
        }
        List<RevenuePointDTO> points = reportService.getMonthly(year);
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        RevenueSummaryDTO summary = reportService.getSummary(start, end, DEFAULT_ZONE);
        return new RevenueReportDTO(points, summary);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("reports/revenue/yearly")
    @ApiMessage("Lấy báo cáo doanh thu theo năm thành công")
    public RevenueReportDTO revenueYearly(
            @RequestParam int startYear,
            @RequestParam int endYear) throws IdInvalidException {
        if (endYear < startYear) {
            throw new IdInvalidException("Tham số năm không hợp lệ (endYear phải >= startYear)");
        }
        if (startYear < 2000 || endYear > 2100) {
            throw new IdInvalidException("Khoảng năm không hợp lệ (2000–2100)");
        }
        List<RevenuePointDTO> points = reportService.getYearly(startYear, endYear);
        LocalDate start = LocalDate.of(startYear, 1, 1);
        LocalDate end = LocalDate.of(endYear, 12, 31);
        RevenueSummaryDTO summary = reportService.getSummary(start, end, DEFAULT_ZONE);
        return new RevenueReportDTO(points, summary);
    }
}
