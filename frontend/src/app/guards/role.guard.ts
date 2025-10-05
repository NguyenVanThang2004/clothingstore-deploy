import { Injectable } from '@angular/core';
import { CanActivateChild, ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { TokenService } from '../service/token.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivateChild {
    constructor(
        private router: Router,
        private jwt: JwtHelperService,
        private tokenService: TokenService
    ) { }

    private decode(): any | null {
        const t = this.tokenService.getToken();
        if (!t) return null;
        try { return this.jwt.decodeToken(t); } catch { return null; }
    }

    private extractRoles(payload: any): string[] {
        if (!payload) return [];
        const norm = (s: string) => s.replace(/^ROLE_/, '').trim().toUpperCase();

        if (Array.isArray(payload.roles)) return payload.roles.map(norm);
        if (Array.isArray(payload.authorities)) return payload.authorities.map(norm);
        if (typeof payload.role === 'string') return [norm(payload.role)];                // "ROLE_ADMIN" -> "ADMIN"
        if (payload.realm_access?.roles) return payload.realm_access.roles.map(norm);
        return [];
    }

    canActivateChild(route: ActivatedRouteSnapshot): boolean | UrlTree {
        const token = this.tokenService.getToken();
        let expired = true;
        if (token) { try { expired = this.jwt.isTokenExpired(token); } catch { expired = true; } }
        if (!token || expired) {
            return this.router.createUrlTree(['/login'], { queryParams: { redirectUrl: this.router.url } });
        }

        const required = (route.parent?.data?.['roles'] ?? route.data?.['roles'] ?? []) as string[];
        const requiredNorm = required.map(r => r.trim().toUpperCase());

        if (requiredNorm.length === 0) return true;

        const userRoles = this.extractRoles(this.decode());
        const ok = requiredNorm.some(r => userRoles.includes(r));
        return ok ? true : this.router.createUrlTree(['/403']);
    }
}
