import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environments';
import { map, Observable } from 'rxjs';
import { UserDTO } from '../dtos/user';


@Injectable({
    providedIn: 'root'
})
export class UserService {

    private apiUser = `${environment.apiBaseUrl}/users`;


    constructor(private http: HttpClient) { }


    // user.service.ts
    getUsers(page = 1, pageSize = 10): Observable<{ users: any[]; meta: any }> {
        // Spring cần page (0-based) và size
        const params = new HttpParams()
            .set('page', String(page - 1))   // chuyển 1-based của UI -> 0-based cho Spring
            .set('size', String(pageSize));  // tên đúng là 'size'

        return this.http.get<any>(this.apiUser, { params }).pipe(
            map(res => ({
                users: res.data.result,
                meta: res.data.meta
            }))
        );
    }

    getUserByID(id: string): Observable<any> {
        return this.http.get<any>(`${this.apiUser}/${id}`);
    }

    createUser(user: UserDTO): Observable<any> {
        return this.http.post<any>(this.apiUser, user);
    }
    updateUser(id: string, user: Partial<UserDTO>): Observable<any> {
        return this.http.put<any>(`${this.apiUser}/${id}`, user);
    }
    deleteUser(id: string): Observable<any> {
        return this.http.delete<any>(`${this.apiUser}/${id}`);
    }

    searchUsers(keyword: string | null, role: string | null, page = 1, pageSize = 10): Observable<{ users: any[]; meta: any }> {
        let params = new HttpParams()
            .set('page', String(page - 1))
            .set('size', String(pageSize));

        if (keyword) {
            params = params.set('keyword', keyword);
        }
        if (role) {
            params = params.set('role', role);
        }

        return this.http.get<any>(`${this.apiUser}/search`, { params }).pipe(
            map(res => ({
                users: res.data.content,
                meta: {
                    page: res.data.number + 1,
                    pageSize: res.data.size,
                    total: res.data.totalElements,
                    pages: res.data.totalPages
                }
            }))
        );
    }

    forgotPasswordReset(email: string, newPassword: string) {
        return this.http.post(
            `${this.apiUser}/forgot-password-reset`,
            { email, newPassword },
            { responseType: 'text' }
        );
    }

    changePassword(id: number | string, payload: {
        oldPassword: string;
        newPassword: string;
        reEnterPassword: string;
    }) {
        return this.http.put(
            `${this.apiUser}/${id}/change-password`,
            payload,
            { responseType: 'text' }
        );
    }



}
