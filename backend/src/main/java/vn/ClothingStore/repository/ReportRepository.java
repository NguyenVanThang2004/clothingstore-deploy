package vn.ClothingStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.ClothingStore.domain.Order; // sửa import đúng entity của bạn

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface ReportRepository extends JpaRepository<Order, Integer> {

    public interface RevenueRow {
        String getLabel();

        BigDecimal getTotal();
    }

    public interface SummaryRow {
        BigDecimal getTotalRevenue();

        Long getOrderCount();

        BigDecimal getAvgOrderValue();
    }

    // === DAILY: group theo ngày (YYYY-MM-DD)
    @Query(value = """
                SELECT DATE(o.order_date) AS label,
                       COALESCE(SUM(o.total_money),0) AS total
                FROM orders o
                WHERE o.status = 'DELIVERED'
                  AND o.order_date BETWEEN :start AND :end
                GROUP BY DATE(o.order_date)
                ORDER BY DATE(o.order_date)
            """, nativeQuery = true)
    List<RevenueRow> revenueDaily(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end);

    // === MONTHLY: group theo tháng (YYYY-MM)
    @Query(value = """
                SELECT DATE_FORMAT(o.order_date, '%Y-%m') AS label,
                       COALESCE(SUM(o.total_money),0) AS total
                FROM orders o
                WHERE o.status = 'DELIVERED'
                  AND YEAR(o.order_date) = :year
                GROUP BY DATE_FORMAT(o.order_date, '%Y-%m')
                ORDER BY DATE_FORMAT(o.order_date, '%Y-%m')
            """, nativeQuery = true)
    List<RevenueRow> revenueMonthly(@Param("year") int year);

    // === YEARLY: group theo năm (YYYY)
    @Query(value = """
                SELECT YEAR(o.order_date) AS label,
                       COALESCE(SUM(o.total_money),0) AS total
                FROM orders o
                WHERE o.status = 'DELIVERED'
                  AND YEAR(o.order_date) BETWEEN :startYear AND :endYear
                GROUP BY YEAR(o.order_date)
                ORDER BY YEAR(o.order_date)
            """, nativeQuery = true)
    List<RevenueRow> revenueYearly(
            @Param("startYear") int startYear,
            @Param("endYear") int endYear);

    // === SUMMARY: cho bất kỳ khoảng thời gian
    @Query(value = """
                SELECT COALESCE(SUM(o.total_money),0) AS totalRevenue,
                       COUNT(*)                        AS orderCount,
                       COALESCE(AVG(o.total_money),0) AS avgOrderValue
                FROM orders o
                WHERE o.status = 'DELIVERED'
                  AND o.order_date BETWEEN :start AND :end
            """, nativeQuery = true)
    SummaryRow revenueSummary(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end);
}
