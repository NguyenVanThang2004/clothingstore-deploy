import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-forbidden',
    templateUrl: './forbidden.component.html',
    styleUrls: ['./forbidden.component.css']
})
export class ForbiddenComponent {
    constructor(private router: Router) { }

    goHome() { this.router.navigateByUrl('/'); }
    goBack() { history.back(); }
}
