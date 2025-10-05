package vn.ClothingStore.domain.response.reports;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenuePointDTO {
    private String label; // ngày/tháng/năm
    private BigDecimal total; // tổng doanh thu
}
