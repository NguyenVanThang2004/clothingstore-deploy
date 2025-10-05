
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { map, Observable } from 'rxjs';
import { CategoryDTO } from '../dtos/category';



@Injectable({
    providedIn: 'root'
})


export class CategoryService {

    private apiCategory = `${environment.apiBaseUrl}/categories`;

    constructor(private http: HttpClient) { }

    getCategory(): Observable<any> {
        return this.http.get<any>(`${this.apiCategory}`).pipe(
            map(res => res.data)
        );
    }
}