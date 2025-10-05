import { Component, OnDestroy, OnInit } from '@angular/core';
import { ReportService, RevenueReport } from 'src/app/service/report.service';

declare const Chart: any;

type Mode = 'daily' | 'monthly' | 'yearly';

interface RevenuePoint { label: string; total: number; }
interface RevenueSummary { totalRevenue: number; orderCount: number; avgOrderValue: number; }

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit, OnDestroy {

  mode: Mode = 'daily';

  dateRange = {
    start: this.formatDate(new Date(new Date().setDate(new Date().getDate() - 29))),
    end: this.formatDate(new Date())
  };

  year = new Date().getFullYear();

  yearRange = {
    start: new Date().getFullYear() - 4,
    end: new Date().getFullYear()
  };

  rows: RevenuePoint[] = [];
  summary: RevenueSummary | null = null;

  chart: any;
  chartType: 'bar' | 'line' = 'bar';

  constructor(private reportService: ReportService) { }

  ngOnInit(): void { this.reload(); }
  ngOnDestroy(): void { this.destroyChart(); }

  onModeChange() {
    if (this.mode === 'daily') {
      this.dateRange = {
        start: this.formatDate(new Date(new Date().setDate(new Date().getDate() - 29))),
        end: this.formatDate(new Date())
      };
    } else if (this.mode === 'monthly') {
      this.year = new Date().getFullYear();
    } else {
      this.yearRange = { start: new Date().getFullYear() - 4, end: new Date().getFullYear() };
    }
    this.reload();
  }

  reload() {
    if (this.mode === 'daily') this.fetchDaily();
    else if (this.mode === 'monthly') this.fetchMonthly();
    else this.fetchYearly();
  }


  fetchDaily() {
    this.reportService.getRevenueDaily(this.dateRange.start, this.dateRange.end)
      .subscribe((payload: RevenueReport) => {
        this.rows = (payload.points || []).map(x => ({ label: x.label, total: Number(x.total) || 0 }));
        this.summary = payload.summary || { totalRevenue: 0, orderCount: 0, avgOrderValue: 0 };
        this.drawChart('Doanh thu theo ngày');
      });
  }

  fetchMonthly() {
    this.reportService.getRevenueMonthly(this.year)
      .subscribe((payload: RevenueReport) => {
        this.rows = (payload.points || []).map(x => ({ label: x.label, total: Number(x.total) || 0 }));
        this.summary = payload.summary || { totalRevenue: 0, orderCount: 0, avgOrderValue: 0 };
        this.drawChart('Doanh thu theo tháng');
      });
  }

  fetchYearly() {
    this.reportService.getRevenueYearly(this.yearRange.start, this.yearRange.end)
      .subscribe((payload: RevenueReport) => {
        this.rows = (payload.points || []).map(x => ({ label: x.label, total: Number(x.total) || 0 }));
        this.summary = payload.summary || { totalRevenue: 0, orderCount: 0, avgOrderValue: 0 };
        this.drawChart('Doanh thu theo năm');
      });
  }

  // =================== BIỂU ĐỒ ===================
  drawChart(title: string) {
    const canvas = document.getElementById('revenueChart') as HTMLCanvasElement | null;
    if (!canvas) { this.destroyChart(); return; }
    const ctx = canvas.getContext('2d');
    if (!ctx || !(window as any).Chart) { this.destroyChart(); return; }

    const labels = this.rows.map(r => r.label);
    const values = this.rows.map(r => r.total);

    this.destroyChart();
    this.chart = new Chart(ctx, {
      type: this.chartType,
      data: {
        labels,
        datasets: [{
          label: title,
          data: values,
          borderWidth: 2,
          fill: false,
          tension: 0.3
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { ticks: { callback: (val: number) => this.formatMoney(val) } } },
        plugins: {
          tooltip: { callbacks: { label: (c: any) => ` ${this.formatMoney(c.parsed.y)} đ` } },
          legend: { display: false }
        }
      }
    });
  }

  switchChart(type: 'bar' | 'line') {
    if (this.chartType === type) return;
    this.chartType = type;
    this.drawChart(this.chart?.data?.datasets?.[0]?.label || 'Doanh thu');
  }

  destroyChart() { if (this.chart) { this.chart.destroy(); this.chart = null; } }


  private formatDate(d: Date) {
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }
  private formatMoney(n: number) {
    try { return new Intl.NumberFormat('vi-VN').format(n); }
    catch { return n; }
  }
}
