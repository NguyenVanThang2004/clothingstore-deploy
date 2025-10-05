
import { Injectable } from "@angular/core";

@Injectable({
    providedIn: `root`,
}
)

export class TokenService {

    private readonly TOKEN_KEY = `access_token`;

    constructor() {

    }

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }
    setToken(token: string): void {
        localStorage.setItem(this.TOKEN_KEY, token);
    }
    removeToken(): void {
        localStorage.removeItem(this.TOKEN_KEY);
    }

    // token.service.ts
    hasRole(role: string): boolean {
        const t = this.getToken();
        if (!t) return false;
        try {
            const payload = JSON.parse(atob(t.split('.')[1]));
            const r = (payload.role || payload.roles || payload.authorities || '').toString();
            const list = Array.isArray(r) ? r : [r];
            return list.map((x: string) => x.replace(/^ROLE_/, '').toUpperCase()).includes(role.toUpperCase());
        } catch { return false; }
    }





}