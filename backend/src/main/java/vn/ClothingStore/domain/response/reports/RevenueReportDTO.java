package vn.ClothingStore.domain.response.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDTO {
    private List<RevenuePointDTO> points; // danh sách mốc (ngày/tháng/năm) và doanh thu
    private RevenueSummaryDTO summary; // tổng quan: tổng doanh thu, số đơn, AOV
}
