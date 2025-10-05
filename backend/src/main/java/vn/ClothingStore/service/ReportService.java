package vn.ClothingStore.service;

import org.springframework.stereotype.Service;
import vn.ClothingStore.domain.response.reports.RevenuePointDTO;
import vn.ClothingStore.domain.response.reports.RevenueSummaryDTO;
import vn.ClothingStore.repository.ReportRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    private Timestamp startOfDay(LocalDate d, ZoneId zone) {
        return Timestamp.from(d.atStartOfDay(zone).toInstant());
    }

    private Timestamp endOfDay(LocalDate d, ZoneId zone) {
        return Timestamp.from(d.plusDays(1).atStartOfDay(zone).toInstant().minusNanos(1));
    }

    public List<RevenuePointDTO> getDaily(LocalDate start, LocalDate end, ZoneId zone) {
        return reportRepository.revenueDaily(startOfDay(start, zone), endOfDay(end, zone))
                .stream()
                .map(r -> new RevenuePointDTO(r.getLabel(), safeBig(r.getTotal())))
                .collect(Collectors.toList());
    }

    public List<RevenuePointDTO> getMonthly(int year) {
        return reportRepository.revenueMonthly(year)
                .stream()
                .map(r -> new RevenuePointDTO(r.getLabel(), safeBig(r.getTotal())))
                .collect(Collectors.toList());
    }

    public List<RevenuePointDTO> getYearly(int startYear, int endYear) {
        return reportRepository.revenueYearly(startYear, endYear)
                .stream()
                .map(r -> new RevenuePointDTO(r.getLabel(), safeBig(r.getTotal())))
                .collect(Collectors.toList());
    }

    public RevenueSummaryDTO getSummary(LocalDate start, LocalDate end, ZoneId zone) {
        var row = reportRepository.revenueSummary(startOfDay(start, zone), endOfDay(end, zone));
        return new RevenueSummaryDTO(
                safeBig(row.getTotalRevenue()),
                row.getOrderCount() == null ? 0L : row.getOrderCount(),
                safeBig(row.getAvgOrderValue()));
    }

    private BigDecimal safeBig(BigDecimal b) {
        return b == null ? BigDecimal.ZERO : b;
    }
}
