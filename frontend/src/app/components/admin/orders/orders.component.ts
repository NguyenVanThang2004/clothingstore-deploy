import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Modal } from 'bootstrap';
import { OrderService } from 'src/app/service/order.service';
import { ResOrderDTO, ReqUpdateOrderStatusDTO } from 'src/app/dtos/order';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent {

  orders: ResOrderDTO[] = [];
  meta = { page: 1, pageSize: 10, pages: 1, total: 0 };
  loading = false;
  saving = false;

  // Modal
  @ViewChild('orderDetailModal') orderDetailModalRef!: ElementRef<HTMLDivElement>;
  @ViewChild('editStatusModal') editStatusModalRef!: ElementRef<HTMLDivElement>;
  detailModal!: Modal;
  editModal!: Modal;

  selectedOrder: ResOrderDTO | null = null;
  selectedStatus: string = '';
  newStatus: string = '';

  statusOptions = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  constructor(private orderService: OrderService, private toastr: ToastrService) { }

  ngOnInit() {
    this.load(1);
  }

  ngAfterViewInit() {
    this.detailModal = new Modal(this.orderDetailModalRef.nativeElement, { backdrop: 'static', keyboard: false });
    this.editModal = new Modal(this.editStatusModalRef.nativeElement, { backdrop: 'static', keyboard: false });
  }

  load(page: number) {
    this.loading = true;
    if (this.selectedStatus) {
      this.orderService.filterOrders(this.selectedStatus, page, this.meta.pageSize).subscribe({
        next: ({ orders, meta }) => {
          this.orders = orders;
          this.meta = { ...meta, pages: Math.ceil(meta.total / meta.pageSize) };
          this.loading = false;
        },
        error: () => { this.loading = false; this.toastr.error('Không tải được đơn hàng'); }
      });
    } else {
      this.orderService.getOrders(page, this.meta.pageSize).subscribe({
        next: ({ orders, meta }) => {
          this.orders = orders;
          this.meta = { ...meta, pages: Math.ceil(meta.total / meta.pageSize) };
          this.loading = false;
        },
        error: () => { this.loading = false; this.toastr.error('Không tải được đơn hàng'); }
      });
    }
  }

  onStatusChange(e: any) {
    this.selectedStatus = e.target.value;
    this.load(1);
  }

  prev() { if (this.meta.page > 1) this.load(this.meta.page - 1); }
  next() { if (this.meta.page < this.meta.pages) this.load(this.meta.page + 1); }

  openDetail(o: ResOrderDTO) {
    this.selectedOrder = o;
    this.detailModal.show();
  }

  openEdit(o: ResOrderDTO) {
    this.selectedOrder = o;
    this.newStatus = o.status;
    this.editModal.show();
  }

  updateStatus() {
    if (!this.selectedOrder) return;
    this.saving = true;
    const req: ReqUpdateOrderStatusDTO = { status: this.newStatus };
    this.orderService.updateOrderStatus(this.selectedOrder.id, req).subscribe({
      next: () => {
        const idx = this.orders.findIndex(x => x.id === this.selectedOrder!.id);
        if (idx > -1) {
          // chỉ cập nhật status thôi
          this.orders[idx] = {
            ...this.orders[idx],
            status: this.newStatus
          };
          this.orders = [...this.orders]; // ép Angular refresh bảng
        }

        // đồng bộ luôn selectedOrder nếu đang mở detail
        this.selectedOrder = {
          ...this.selectedOrder!,
          status: this.newStatus
        };

        this.toastr.success('Cập nhật trạng thái thành công');
        this.saving = false;
        this.editModal.hide();
      },
      error: () => {
        this.toastr.error('Cập nhật trạng thái thất bại');
        this.saving = false;
      }
    });
  }


  statusClass(s: string) {
    switch (s) {
      case 'PENDING':
        return 'badge bg-warning text-dark';     // Vàng
      case 'PROCESSING':
        return 'badge bg-primary';               // Xanh dương
      case 'SHIPPING':
        return 'badge bg-purple';                // Tím
      case 'DELIVERED':
        return 'badge bg-success';               // Xanh lá
      case 'CANCELLED':
        return 'badge bg-danger';                // Đỏ
      default:
        return 'badge bg-secondary';             // Xám
    }
  }

}
