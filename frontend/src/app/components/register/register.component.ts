import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  @ViewChild('registerForm') registerForm!: NgForm;

  // form fields
  fullname = '';
  phonenumber = '';
  email = '';
  password = '';
  otp = '';

  // UI state
  step: 1 | 2 = 1;
  loading = false;
  resendCooldown = 0;
  cooldownTimer: any;

  // messages
  errorMessage = '';
  infoMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) { }

  // === Step 1: gửi OTP ===
  sendOtp(form: NgForm) {
    this.resetMessages();
    if (form.invalid) {
      form.form.markAllAsTouched();
      return;
    }

    const payload = {
      fullName: this.fullname.trim(),
      phoneNumber: this.phonenumber.trim(),
      email: this.email.trim(),
      password: this.password
    };

    this.loading = true;
    this.authService.sendRegisterOtp(payload).subscribe({
      next: (res: string) => {
        this.loading = false;
        this.toastr.info(res || 'OTP đã được gửi về email', 'Thông báo');
        this.infoMessage = res;
        this.step = 2;
        this.startCooldown(); // hạn chế spam gửi lại OTP
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.pickErr(err, 'Gửi OTP thất bại, vui lòng thử lại.');
        this.toastr.error(this.errorMessage, 'Lỗi');
      }
    });
  }

  // === Step 2: xác thực OTP & tạo tài khoản ===
  verifyOtp(form: NgForm) {
    this.resetMessages();
    if (form.invalid) {
      form.form.markAllAsTouched();
      return;
    }

    const payload = {
      fullName: this.fullname.trim(),
      phoneNumber: this.phonenumber.trim(),
      email: this.email.trim(),
      password: this.password,
      otp: this.otp.trim()
    };

    this.loading = true;
    this.authService.createAccountAfterOtp(payload).subscribe({
      next: (res: string) => {
        this.loading = false;
        this.successMessage = res || 'Tạo tài khoản thành công!';
        this.toastr.success(this.successMessage, 'Thành công');
        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.pickErr(err, 'Xác thực OTP thất bại, vui lòng thử lại.');
        this.toastr.error(this.errorMessage, 'Lỗi');
      }
    });
  }

  // Gửi lại OTP (giữ nguyên dữ liệu step 1)
  resendOtp(e: Event) {
    e.preventDefault();
    if (this.resendCooldown > 0 || this.loading) return;

    const payload = {
      fullName: this.fullname.trim(),
      phoneNumber: this.phonenumber.trim(),
      email: this.email.trim(),
      password: this.password
    };

    this.loading = true;
    this.authService.sendRegisterOtp(payload).subscribe({
      next: (res: string) => {
        this.loading = false;
        this.toastr.info(res || 'OTP đã được gửi lại.', 'Thông báo');
        this.infoMessage = res;
        this.startCooldown();
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.pickErr(err, 'Gửi lại OTP thất bại.');
        this.toastr.error(this.errorMessage, 'Lỗi');
      }
    });
  }

  backToStep1() {
    this.step = 1;
  }

  // helpers
  private resetMessages() {
    this.errorMessage = '';
    this.infoMessage = '';
    this.successMessage = '';
  }

  private startCooldown(seconds: number = 60) {
    this.clearCooldown();
    this.resendCooldown = seconds;
    this.cooldownTimer = setInterval(() => {
      this.resendCooldown--;
      if (this.resendCooldown <= 0) this.clearCooldown();
    }, 1000);
  }

  private clearCooldown() {
    if (this.cooldownTimer) {
      clearInterval(this.cooldownTimer);
      this.cooldownTimer = null;
    }
    if (this.resendCooldown < 0) this.resendCooldown = 0;
  }

  private pickErr(err: HttpErrorResponse, fallback: string): string {
    return (
      err?.error?.message ||
      err?.error?.error ||
      err?.message ||
      fallback
    );
  }

}
