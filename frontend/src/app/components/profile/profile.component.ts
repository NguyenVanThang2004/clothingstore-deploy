import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/service/user.service';
import { AuthService } from 'src/app/service/auth.service';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
    user: any = {
        id: '',
        fullName: '',
        email: '',
        phoneNumber: '',
        address: '',
        dateOfBirth: '',
        role: null
    };

    // Modal đổi mật khẩu
    showChangePass = false;
    changePass = {
        oldPassword: '',
        newPassword: '',
        reEnterPassword: ''
    };
    changePassLoading = false;
    changePassError = '';
    changePassSuccess = '';

    // show/hide inputs
    showOld = false;
    showNew = false;
    showRe = false;

    constructor(
        private userService: UserService,
        private authService: AuthService,
        private toastr: ToastrService
    ) { }

    ngOnInit(): void {
        this.authService.getCurrentUser().subscribe({
            next: (res) => {
                const id = res.data?.user?.id;
                if (id) {
                    this.userService.getUserByID(String(id)).subscribe({
                        next: (resUser) => {
                            this.user = resUser.data;
                        },
                        error: (err) => console.error(err)
                    });
                }
            },
            error: (err) => console.error(err)
        });
    }

    updateProfile(form: NgForm): void {
        if (form.invalid) {
            form.control.markAllAsTouched();
            return;
        }
        if (!this.user.id) {
            console.warn('User chưa có id, không thể cập nhật');
            return;
        }

        const payload = {
            fullName: this.user.fullName,
            address: this.user.address,
            dateOfBirth: this.user.dateOfBirth
        };

        this.userService.updateUser(this.user.id, payload).subscribe({
            next: () => {
                this.toastr.success('Cập nhật thông tin thành công');
            },
            error: (err) => {
                console.error(err);
                this.toastr.error('Cập nhật thất bại');
            }
        });
    }

    // ====== Đổi mật khẩu ======
    openChangePassword() {
        this.resetChangePassState();
        this.showChangePass = true;
    }

    closeChangePassword() {
        this.showChangePass = false;
    }

    submitChangePassword(form: NgForm) {
        this.changePassError = '';
        this.changePassSuccess = '';

        if (form.invalid || this.changePass.reEnterPassword !== this.changePass.newPassword) {
            form.control.markAllAsTouched();
            return;
        }

        if (!this.user?.id) {
            this.changePassError = 'Không xác định được tài khoản.';
            return;
        }

        this.changePassLoading = true;
        this.userService.changePassword(this.user.id, {
            oldPassword: this.changePass.oldPassword,
            newPassword: this.changePass.newPassword,
            reEnterPassword: this.changePass.reEnterPassword
        }).subscribe({
            next: (res: string) => {
                this.changePassLoading = false;
                this.changePassSuccess = res || 'Thay đổi mật khẩu thành công';
                this.toastr.success(this.changePassSuccess);
                this.closeChangePassword();
            },
            error: (err: HttpErrorResponse) => {
                this.changePassLoading = false;
                const msg =
                    'Đổi mật khẩu thất bại';
                this.changePassError = msg;
                this.toastr.error(this.changePassError);
            }
        });
    }

    private resetChangePassState() {
        this.changePass = { oldPassword: '', newPassword: '', reEnterPassword: '' };
        this.changePassLoading = false;
        this.changePassError = '';
        this.changePassSuccess = '';
        this.showOld = this.showNew = this.showRe = false;
    }
}
