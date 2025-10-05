import { Component } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { UserService } from 'src/app/service/user.service';


@Component({
    selector: 'app-forgot-password',
    templateUrl: './forgot-password.component.html',
})
export class ForgotPasswordComponent {
    email = '';
    otp = '';
    newPassword = '';
    step = 1;

    constructor(
        private authService: AuthService,
        private userService: UserService,
        private toastr: ToastrService,
        private router: Router
    ) { }

    sendOtp() {
        console.log('[ForgotPassword] email input:', this.email);

        this.authService.forgotPasswordSend(this.email).subscribe({
            next: (res) => {
                console.log('[ForgotPassword] API response (sendOtp):', res);
                this.toastr.success('OTP đã gửi, vui lòng kiểm tra email');
                this.step = 2;
            },
            error: (err) => {
                console.error('[ForgotPassword] API error (sendOtp):', err);
                console.error('[ForgotPassword] API error body:', err.error);
                console.error('[ForgotPassword] API error message:', err.message);
                this.toastr.error(err.error?.message || 'Lỗi gửi OTP');
            }
        });
    }

    verifyOtp() {
        console.log('[ForgotPassword] email input:', this.email);
        console.log('[ForgotPassword] otp input:', this.otp);

        this.authService.forgotPasswordVerify(this.email, this.otp).subscribe({
            next: (res) => {
                console.log('[ForgotPassword] API response (verifyOtp):', res);
                this.toastr.success('Xác thực OTP thành công');
                this.step = 3;
            },
            error: (err) => {
                console.error('[ForgotPassword] API error (verifyOtp):', err);
                console.error('[ForgotPassword] API error body:', err.error);
                console.error('[ForgotPassword] API error message:', err.message);
                this.toastr.error(err.error?.message || 'OTP không hợp lệ');
            }
        });
    }

    resetPassword() {
        console.log('[ForgotPassword] email input:', this.email);
        console.log('[ForgotPassword] newPassword input:', this.newPassword);

        this.userService.forgotPasswordReset(this.email, this.newPassword).subscribe({
            next: (res) => {
                console.log('[ForgotPassword] API response (resetPassword):', res);
                this.toastr.success('Đặt lại mật khẩu thành công');
                this.router.navigate(['/login']);
            },
            error: (err) => {
                console.error('[ForgotPassword] API error (resetPassword):', err);
                console.error('[ForgotPassword] API error body:', err.error);
                console.error('[ForgotPassword] API error message:', err.message);
                this.toastr.error(err.error?.message || 'Lỗi đặt lại mật khẩu');
            }
        });
    }

}
