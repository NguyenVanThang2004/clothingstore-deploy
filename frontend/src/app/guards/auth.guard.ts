import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { TokenService } from '../service/token.service';

export const authGuard: CanActivateFn = (_route, state) => {
    const router = inject(Router);
    const jwt = inject(JwtHelperService);
    const tokens = inject(TokenService);

    const token = tokens.getToken();
    let expired = true;
    if (token) { try { expired = jwt.isTokenExpired(token); } catch { expired = true; } }

    return (!token || expired)
        ? router.createUrlTree(['/login'], { queryParams: { redirectUrl: state.url } })
        : true;
};
