import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from 'src/app/service/auth.service';
import { LoginDTO } from 'src/app/dtos/user/login.dto';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  @ViewChild('loginForm') loginForm!: NgForm;

  email = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private toastr: ToastrService,
  ) { }

  onSubmit(form: NgForm) {
    this.errorMessage = '';
    if (form.invalid) { form.form.markAllAsTouched(); return; }

    const payload: LoginDTO = { email: this.email.trim(), password: this.password };
    this.loading = true;

    this.authService.login(payload).subscribe({
      next: (res: any) => {
        this.loading = false;

        const token = res?.data?.access_token;
        if (token) this.authService.setLogin(token); // 🔑 quan trọng

        this.toastr.success('Đăng nhập thành công 🎉', 'Thông báo');

        const redirectUrl = this.route.snapshot.queryParamMap.get('redirectUrl');
        if (redirectUrl) { this.router.navigateByUrl(redirectUrl); return; }

        const role = (res?.data?.user?.role?.name || '').toUpperCase();
        if (role === 'ADMIN') this.router.navigate(['/admin/products']);
        else this.router.navigate(['/']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || err?.error?.error || err?.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
        this.toastr.error(this.errorMessage, 'Lỗi');
      }
    });
  }
}
