import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { TokenService } from '../service/token.service';

export const adminMatchGuard: CanMatchFn = (_route, _segs) => {
    const router = inject(Router);
    const jwt = inject(JwtHelperService);
    const tokens = inject(TokenService);

    const token = tokens.getToken();
    let expired = true;
    if (token) { try { expired = jwt.isTokenExpired(token); } catch { expired = true; } }
    if (!token || expired) return router.createUrlTree(['/login'], { queryParams: { redirectUrl: router.url } });

    // đọc role từ payload và chuẩn hoá
    const payload = JSON.parse(atob(token.split('.')[1]));
    const role = (payload.role || payload.roles?.[0] || '')
        .toString().replace(/^ROLE_/, '').trim().toUpperCase();

    return role === 'ADMIN' ? true : router.createUrlTree(['/403']);
};
