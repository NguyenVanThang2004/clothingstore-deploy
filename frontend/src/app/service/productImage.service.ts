import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { map, Observable } from 'rxjs';
import { ProductImageDTO } from '../dtos/productImage';



@Injectable({
    providedIn: 'root'
})
export class ProductImageService {
    private apiUrl = `${environment.apiBaseUrl}/products`;

    constructor(private http: HttpClient) { }

    // Upload nhiều ảnh cho 1 product
    uploadImages(productId: number, files: File[]): Observable<ProductImageDTO[]> {
        const formData = new FormData();
        files.forEach(f => formData.append('files', f));

        return this.http.post<ProductImageDTO[]>(`${this.apiUrl}/${productId}/images`, formData);
    }

    // Lấy danh sách ảnh của product
    listImages(productId: number): Observable<ProductImageDTO[]> {
        return this.http.get<any>(`${this.apiUrl}/${productId}/images`).pipe(
            map(res => res.data)   // 👈 chỉ lấy data
        );
    }

    // Đặt 1 ảnh làm thumbnail
    setThumbnail(imageId: number): Observable<ProductImageDTO> {
        return this.http.put<ProductImageDTO>(`${this.apiUrl}/images/${imageId}/thumbnail`, {});
    }

    // Xoá ảnh
    deleteImage(imageId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/images/${imageId}`);
    }
}
