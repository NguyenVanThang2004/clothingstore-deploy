import { Component, ElementRef, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from 'src/app/service/user.service';
import { UserDTO } from 'src/app/dtos/user';
import { Modal } from 'bootstrap';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';


@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit, AfterViewInit {
  users: any[] = [];
  meta = { page: 1, pageSize: 10, pages: 1, total: 0 };
  loading = false;
  errorMessage = '';

  @ViewChild('userAddModal') userAddModalRef!: ElementRef<HTMLDivElement>;
  addModal!: Modal;
  saving = false;
  formAdd: UserDTO = {
    id: '',
    email: '',
    password: '',
    fullName: '',
    phoneNumber: '',
    address: '',
    isActive: true,
    dateOfBirth: '',
    facebookLinked: false,
    googleLinked: false,
  };

  @ViewChild('userEditModal') userEditModalRef!: ElementRef<HTMLDivElement>;
  editModal!: Modal;
  savingEdit = false;
  formEdit: UserDTO = {
    id: '',
    email: '',
    password: '',
    fullName: '',
    phoneNumber: '',
    address: '',
    isActive: true,
    dateOfBirth: '',
    facebookLinked: false,
    googleLinked: false,
  };

  keyword: string = '';
  selectedRole: string = '';
  roles = ['ADMIN', 'USER']; // tùy enum BE


  constructor(
    private userService: UserService,
    private toastr: ToastrService
  ) { }

  ngOnInit() { this.load(1); }

  ngAfterViewInit() {
    this.addModal = new Modal(this.userAddModalRef.nativeElement, { backdrop: 'static', keyboard: false });
    this.editModal = new Modal(this.userEditModalRef.nativeElement, { backdrop: 'static', keyboard: false });
  }

  load(page: number) {
    this.loading = true;
    this.errorMessage = '';

    // Nếu có keyword hoặc role thì gọi search
    if (this.keyword || this.selectedRole) {
      this.userService.searchUsers(this.keyword, this.selectedRole, page, this.meta.pageSize).subscribe({
        next: ({ users, meta }) => {
          this.users = users;
          this.meta = meta;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.toastr.error('Không tải được danh sách user');
        }
      });
    } else {
      // Ngược lại thì gọi getAll
      this.userService.getUsers(page, this.meta.pageSize).subscribe({
        next: ({ users, meta }) => {
          this.users = users;
          this.meta = meta;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.toastr.error('Không tải được danh sách user');
        }
      });
    }
  }

  applyFilter() {
    this.load(1); // luôn về trang 1
  }



  prev() { if (this.meta.page > 1) this.load(this.meta.page - 1); }
  next() { if (this.meta.page < this.meta.pages) this.load(this.meta.page + 1); }

  remove(id: string) {
    if (!confirm(`Xóa khách hàng id = ${id}  này ?`)) return;
    this.userService.deleteUser(id).subscribe({
      next: () => this.load(this.meta.page)
    });
  }

  openAdd() {
    this.formAdd = {
      id: '',
      email: '',
      password: '',
      fullName: '',
      phoneNumber: '',
      address: '',
      isActive: true,
      dateOfBirth: '',
      facebookLinked: false,
      googleLinked: false,
    };
    this.addModal.show();
  }

  // nhận NgForm để kiểm tra invalid
  saveAdd(addForm: NgForm) {
    if (addForm.invalid) return;
    this.saving = true;

    const payload: UserDTO = {
      ...this.formAdd,
      dateOfBirth: this.normalizeDate(this.formAdd.dateOfBirth)
    };

    this.userService.createUser(payload).subscribe({
      next: () => {
        this.saving = false;
        this.addModal.hide();
        this.load(this.meta.page);
      },
      error: (err: HttpErrorResponse) => {
        this.saving = false;
        this.errorMessage =
          err?.error?.message ||
          err?.error?.error ||
          err?.message ||
          'Đăng kí thất bại. Vui lòng thử lại.';

        this.toastr.error(this.errorMessage, 'Lỗi');

      }
    });
  }

  openEdit(u: any) {
    this.formEdit = {
      id: String(u.id ?? ''),
      fullName: u.fullName ?? '',
      phoneNumber: u.phoneNumber ?? '',
      email: u.email ?? '',
      dateOfBirth: u.dateOfBirth ?? '',
      address: u.address ?? '',
      isActive: Boolean(u.isActive),
      facebookLinked: Boolean(u.facebookLinked),
      googleLinked: Boolean(u.googleLinked),
      password: ''
    };
    this.editModal.show();
  }

  saveEdit() {
    if (!this.formEdit.id) return;
    if (!(this.formEdit.fullName || '').trim()) return;

    this.savingEdit = true;
    const id = this.formEdit.id;

    const payload: Partial<UserDTO> = {
      fullName: (this.formEdit.fullName || '').trim(),
      address: (this.formEdit.address || '').trim()
    };

    this.userService.updateUser(id, payload).subscribe({
      next: () => {
        this.savingEdit = false;
        this.editModal.hide();
        this.load(this.meta.page);
      },
      error: () => { this.savingEdit = false; }
    });
  }

  private normalizeDate(d: string): string {
    if (!d) return '';
    if (/^\d{4}-\d{2}-\d{2}$/.test(d)) return d;
    const dt = new Date(d);
    if (isNaN(dt.getTime())) return '';
    const yyyy = dt.getFullYear();
    const mm = String(dt.getMonth() + 1).padStart(2, '0');
    const dd = String(dt.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }
}
