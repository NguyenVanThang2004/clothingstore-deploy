import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { RegisterDTO } from '../dtos/user/register.dto';
import { LoginDTO } from '../dtos/user/login.dto';
import { environment } from '../environments/environments';
import { TokenService } from './token.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private apiRegister = `${environment.apiBaseUrl}/auth/register`;
    private apiLogin = `${environment.apiBaseUrl}/auth/login`;
    private apiRefresh = `${environment.apiBaseUrl}/auth/refresh`;
    private apiLogout = `${environment.apiBaseUrl}/auth/logout`;
    private apiAccount = `${environment.apiBaseUrl}/auth/account`;
    private apiForgotSend = `${environment.apiBaseUrl}/auth/forgot-password-send-email`;
    private apiForgotVerify = `${environment.apiBaseUrl}/auth/forgot-password-verify-otp`;

    // ✅ Trạng thái đăng nhập phát cho toàn app
    private loggedIn = new BehaviorSubject<boolean>(this.tokenService.getToken() != null);
    loggedIn$ = this.loggedIn.asObservable();

    constructor(
        private http: HttpClient,
        private tokenService: TokenService
    ) { }

    register(registerDTO: RegisterDTO): Observable<any> {
        return this.http.post<any>(this.apiRegister, registerDTO);
    }

    login(loginDTO: LoginDTO): Observable<any> {
        return this.http.post<any>(this.apiLogin, loginDTO);
    }

    refresh(): Observable<any> {
        return this.http.get<any>(this.apiRefresh, { withCredentials: true });
    }

    logout(): Observable<any> {
        return this.http.post<any>(this.apiLogout, {}, { withCredentials: true });
    }

    getCurrentUserName(): Observable<string> {
        return this.http.get<any>(this.apiAccount).pipe(
            map(res => res?.data?.user?.name as string)
        );
    }

    getCurrentUser(): Observable<any> {
        return this.http.get<any>(this.apiAccount);
    }

    forgotPasswordSend(email: string) {
        return this.http.get(
            `${environment.apiBaseUrl}/auth/forgot-password-send-email?email=${encodeURIComponent(email)}`,
            { responseType: 'text' }
        );
    }

    forgotPasswordVerify(email: string, otp: string) {
        return this.http.post(
            this.apiForgotVerify,
            null,
            {
                params: { email, otp },
                responseType: 'text'
            }
        );
    }

    sendRegisterOtp(payload: any) {
        return this.http.post<any>(
            `${environment.apiBaseUrl}/auth/verify-register-otp`,
            payload,
            { responseType: 'text' as 'json' }
        );
    }


    createAccountAfterOtp(payload: any) {
        return this.http.post(
            `${environment.apiBaseUrl}/auth/create-verify-otp`,
            payload,
            { responseType: 'text' }
        );
    }

    // 🔑 Gọi khi login thành công
    setLogin(token: string) {
        this.tokenService.setToken(token);
        this.loggedIn.next(true);
    }

    // 🔑 Gọi khi logout (hoặc token hết hạn)
    setLogout() {
        this.tokenService.removeToken();
        this.loggedIn.next(false);
    }
}
