import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from 'src/app/environments/environments';

export interface RevenuePoint {
    label: string;
    total: number;
}

export interface RevenueSummary {
    totalRevenue: number;
    orderCount: number;
    avgOrderValue: number;
}

export interface RevenueReport {
    points: RevenuePoint[];
    summary: RevenueSummary;
}

@Injectable({
    providedIn: 'root'
})
export class ReportService {

    private base = `${environment.apiBaseUrl}/reports`;

    constructor(private http: HttpClient) { }

    getRevenueDaily(start: string, end: string): Observable<RevenueReport> {
        const params = new HttpParams().set('start', start).set('end', end);
        return this.http.get<any>(`${this.base}/revenue/daily`, { params }).pipe(
            map(res => (res?.data ?? res) as RevenueReport)
        );
    }
    getRevenueMonthly(year: number): Observable<RevenueReport> {
        const params = new HttpParams().set('year', year);
        return this.http.get<any>(`${this.base}/revenue/monthly`, { params }).pipe(
            map(res => (res?.data ?? res) as RevenueReport)
        );
    }
    getRevenueYearly(startYear: number, endYear: number): Observable<RevenueReport> {
        const params = new HttpParams().set('startYear', startYear).set('endYear', endYear);
        return this.http.get<any>(`${this.base}/revenue/yearly`, { params }).pipe(
            map(res => (res?.data ?? res) as RevenueReport)
        );
    }
}
