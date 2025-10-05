import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { map, Observable } from 'rxjs';
import { ProductVariantDTO } from '../dtos/productVariant';


@Injectable({
    providedIn: 'root'
})
export class ProductVariantService {
    private apiUrl = `${environment.apiBaseUrl}/productVariant`;

    constructor(private http: HttpClient) { }

    // Tạo biến thể mới
    createVariant(variant: ProductVariantDTO): Observable<ProductVariantDTO> {
        return this.http.post<ProductVariantDTO>(this.apiUrl, variant);
    }

    // Chỉ update giá và tồn kho
    updateVariant(productId: number, variantId: number, payload: { price: number; stockQuantity: number }
    ): Observable<ProductVariantDTO> {
        return this.http.put<ProductVariantDTO>(
            `${this.apiUrl}/${productId}/variants/${variantId}`,
            payload
        );
    }

    // get bien the by product_id
    getVariantByProductId(productId: number): Observable<ProductVariantDTO[]> {
        return this.http.get<any>(`${this.apiUrl}/${productId}/variants`)
            .pipe(map(res => res.data as ProductVariantDTO[]));
    }

}
