// src/app/core/interceptors/token.interceptor.ts
import { Injectable } from '@angular/core';
import {
    HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { TokenService } from 'src/app/service/token.service';     // ✅ sửa đường dẫn
import { AuthService } from 'src/app/service/auth.service';       // ✅ sửa đường dẫn
import { environment } from 'src/app/environments/environments';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    private isRefreshing = false;
    private refreshTokenSubject = new BehaviorSubject<string | null>(null);

    // Endpoint public mà KHÔNG gắn Authorization
    private readonly PUBLIC_PATTERNS: RegExp[] = [
        /\/auth\/(login|refresh|register)(\?|$)/i,
        /\/email\//i,
        /\/storage\//i
    ];

    constructor(
        private tokenService: TokenService,
        private authService: AuthService,
        private router: Router
    ) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // Bypass: { headers: { skipAuth: 'true' } }
        if (req.headers.has('skipAuth')) {
            const clean = req.clone({ headers: req.headers.delete('skipAuth') });
            return next.handle(clean);
        }

        const token = this.tokenService.getToken();

        const base = environment.apiBaseUrl.replace(/\/+$/, '');
        const isApi = req.url.startsWith(base) || req.url.startsWith('/api');   // ✅ robust hơn
        const isPublic = this.PUBLIC_PATTERNS.some(rx => rx.test(req.url));

        let authReq = req;

        if (token && isApi && !isPublic && !req.headers.has('Authorization')) {
            authReq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
            // console.log('[TokenInterceptor] attached Authorization to', req.method, req.url);
        }

        return next.handle(authReq).pipe(
            catchError((error: any) => {
                if (error instanceof HttpErrorResponse && error.status === 401 && !isPublic) {
                    return this.handle401Error(authReq, next);
                }
                return throwError(() => error);
            })
        );
    }

    private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (this.isRefreshing) {
            return this.refreshTokenSubject.pipe(
                filter((t): t is string => t !== null),
                take(1),
                switchMap((newToken) => next.handle(
                    request.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } })
                ))
            );
        }

        this.isRefreshing = true;
        this.refreshTokenSubject.next(null);

        return this.authService.refresh().pipe(
            switchMap((res: any) => {
                this.isRefreshing = false;
                const newToken = res?.data?.access_token;
                if (!newToken) {
                    this.forceLogoutToLogin();
                    return throwError(() => new Error('Refresh response missing access_token'));
                }
                this.tokenService.setToken(newToken);
                this.refreshTokenSubject.next(newToken);

                return next.handle(
                    request.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } })
                );
            }),
            catchError((err) => {
                this.isRefreshing = false;
                this.forceLogoutToLogin();
                return throwError(() => err);
            })
        );
    }

    private forceLogoutToLogin() {
        this.tokenService.removeToken();
        const redirectUrl = this.router.url;
        this.router.navigate(['/login'], { queryParams: { redirectUrl } });
    }
}
