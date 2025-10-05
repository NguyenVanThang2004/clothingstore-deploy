import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ReviewDTO, ReqReviewDTO, ReqUpdateReviewDTO } from '../dtos/review';
import { environment } from '../environments/environments';

@Injectable({
    providedIn: 'root'
})
export class ReviewService {
    private apiUrl = `${environment.apiBaseUrl}/review`;

    constructor(private http: HttpClient) { }

    /** Lấy danh sách review theo product */
    getReviewsByProduct(productId: number): Observable<ReviewDTO[]> {
        return this.http
            .get<{ data: ReviewDTO[] }>(
                `${this.apiUrl}/product/${productId}`
            )
            .pipe(map(res => res.data));
    }


    /** Tạo review */
    createReview(req: ReqReviewDTO): Observable<ReviewDTO> {
        return this.http
            .post<{ data: ReviewDTO }>(this.apiUrl, req)
            .pipe(map(res => res.data));
    }


    /** Update review */
    updateReview(id: number, req: ReqUpdateReviewDTO): Observable<ReviewDTO> {
        return this.http.put<ReviewDTO>(`${this.apiUrl}/${id}`, req);
    }

    /** Delete review */
    deleteReview(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
