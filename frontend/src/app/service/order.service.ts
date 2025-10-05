
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../environments/environments';
import { ReqOrderDTO, ReqUpdateOrderStatusDTO, ResOrderDTO } from '../dtos/order';

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiOrder = `${environment.apiBaseUrl}/orders`;
    private apiVNpay = `${environment.apiBaseUrl}/payment`;
    constructor(private http: HttpClient) { }

    getOrders(page: number, pageSize: number): Observable<{ orders: any[]; meta: any }> {
        const params = new HttpParams()
            .set('page', String(page - 1))  // UI 1-based -> Spring 0-based
            .set('size', String(pageSize));

        return this.http.get<any>(this.apiOrder, { params }).pipe(
            map(res => ({
                orders: res.data.result,   // backend trả về ResultPaginationDTO
                meta: res.data.meta
            }))
        );
    }
    getOrdersByUserId(userId: number, params: { page?: number, size?: number }): Observable<{ orders: any[]; meta: any }> {
        let httpParams = new HttpParams()
            .set('page', String((params.page ?? 1) - 1)) // Spring 0-based
            .set('size', String(params.size ?? 12));

        return this.http.get<any>(`${this.apiOrder}/${userId}`, { params: httpParams }).pipe(
            map(res => ({
                orders: res.data.content,
                meta: {
                    page: res.data.number + 1,
                    pageSize: res.data.size,
                    total: res.data.totalElements,
                    pages: res.data.totalPages
                }
            }))
        );

    }

    createOrder(req: ReqOrderDTO): Observable<ResOrderDTO> {
        return this.http.post<ResOrderDTO>(this.apiOrder, req);
    }
    updateOrderStatus(id: number, req: ReqUpdateOrderStatusDTO
    ): Observable<ResOrderDTO> {
        return this.http.put<ResOrderDTO>(`${this.apiOrder}/${id}`, req);
    }
    filterOrders(status: string | null, page: number, pageSize: number): Observable<{ orders: any[]; meta: any }> {
        let params = new HttpParams()
            .set('page', String(page - 1))
            .set('size', String(pageSize));
        if (status) {
            params = params.set('status', status);
        }

        return this.http.get<any>(`${this.apiOrder}/filter`, { params }).pipe(
            map(res => ({
                orders: res.data.content,
                meta: {
                    page: res.data.number + 1,
                    pageSize: res.data.size,
                    total: res.data.totalElements,
                    pages: res.data.totalPages
                }
            }))
        );
    }

    createPayment(req: { amount: string; orderRequest: ReqOrderDTO }): Observable<string> {
        return this.http.post(this.apiVNpay, req, { responseType: 'text' });
    }



}