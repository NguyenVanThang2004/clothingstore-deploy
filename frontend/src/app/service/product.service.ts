import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { map, Observable } from 'rxjs';
import { ProductDTO, ProductPayload } from '../dtos/product';



@Injectable({
    providedIn: 'root'
})

export class ProductService {
    private apiProduct = `${environment.apiBaseUrl}/products`;
    constructor(private http: HttpClient) { }


    // Lấy danh sách sản phẩm có phân trang
    getProducts(page = 1, pageSize = 10): Observable<{ products: any[]; meta: any }> {
        const params = new HttpParams()
            .set('page', String(page - 1))  // UI 1-based -> Spring 0-based
            .set('size', String(pageSize));

        return this.http.get<any>(this.apiProduct, { params }).pipe(
            map(res => ({
                products: res.data.result,   // backend trả về ResultPaginationDTO
                meta: res.data.meta
            }))
        );
    }

    // Lấy chi tiết sản phẩm theo ID
    getProductById(id: number): Observable<any> {
        return this.http.get<any>(`${this.apiProduct}/${id}`).pipe(
            map(res => res.data)
        );
    }

    createProduct(payload: ProductPayload): Observable<ProductDTO> {
        return this.http.post<{ data: ProductDTO }>(`${this.apiProduct}`, payload)
            .pipe(map(res => res.data));
    }


    updateProduct(id: number, payload: ProductPayload): Observable<ProductDTO> {
        return this.http.put<ProductDTO>(`${this.apiProduct}/${id}`, payload);
    }

    // Xóa sản phẩm
    deleteProduct(id: number): Observable<any> {
        return this.http.delete<any>(`${this.apiProduct}/${id}`).pipe(
            map(res => res.data)
        );
    }

    getProductsFiltered(params: {
        page?: number;
        size?: number;
        categoryId?: number | null;
        priceMin?: number | null;
        priceMax?: number | null;
        sort?: string | null;
        keyword?: string | null;
    }): Observable<{ products: any[]; meta: any }> {
        let httpParams = new HttpParams()
            .set('page', String((params.page ?? 1) - 1)) // Spring 0-based
            .set('size', String(params.size ?? 12));

        if (params.categoryId != null) {
            httpParams = httpParams.set('categoryId', String(params.categoryId));
        }
        if (params.priceMin != null) {
            httpParams = httpParams.set('priceMin', String(params.priceMin));
        }
        if (params.priceMax != null) {
            httpParams = httpParams.set('priceMax', String(params.priceMax));
        }
        if (params.sort) {
            httpParams = httpParams.set('sort', params.sort);
        }
        if (params.keyword) {
            httpParams = httpParams.set('keyword', params.keyword);
        }


        return this.http.get<any>(`${this.apiProduct}/filter`, { params: httpParams }).pipe(
            map(res => ({
                products: res.data.content,    // 👈 lấy trong data
                meta: {
                    page: res.data.number + 1,   // Spring trả 0-based, FE dùng 1-based
                    pageSize: res.data.size,
                    total: res.data.totalElements,
                    pages: res.data.totalPages
                }
            }))
        );
    }





}