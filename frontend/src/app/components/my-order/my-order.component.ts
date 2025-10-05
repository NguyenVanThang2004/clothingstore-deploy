import { Component, OnInit } from '@angular/core';
import { OrderService } from 'src/app/service/order.service';
import { ToastrService } from 'ngx-toastr';
import { ReqUpdateOrderStatusDTO } from 'src/app/dtos/order';
import { AuthService } from 'src/app/service/auth.service'; // 👈 thêm

@Component({
    selector: 'app-my-order',
    templateUrl: './my-order.component.html'
})
export class MyOrdersComponent implements OnInit {
    orders: any[] = [];
    meta: any;
    page = 1;
    pageSize = 2;

    currentUserId!: number;


    constructor(
        private orderService: OrderService,
        private toastr: ToastrService,
        private authService: AuthService // 👈 inject
    ) { }

    ngOnInit(): void {
        // lấy user hiện tại 1 lần, rồi gọi API theo userId
        this.authService.getCurrentUser().subscribe({
            next: (res) => {
                const user = res.data?.user;

                if (!user) {
                    console.error('Không lấy được userId từ currentUser');
                    return;
                }
                this.currentUserId = user.id;   // 👈 gán userId
                this.loadOrders();
            },
            error: (err) => console.error(err)
        });

        console.log(this.orders);
    }

    loadOrders(): void {
        // 👇 gọi đúng API theo userId + phân trang
        this.orderService.getOrdersByUserId(this.currentUserId, {
            page: this.page,
            size: this.pageSize
        }).subscribe({
            next: (res) => {
                console.log('[Orders raw]', res);
                this.orders = res.orders;
                this.meta = res.meta;
                console.log('[Orders mapped]', this.orders);
            },
            error: (err) => console.error(err)
        });
    }

    changePage(newPage: number): void {
        this.page = newPage;
        this.loadOrders();
    }

    cancelOrder(order: any): void {
        if (!confirm(`Bạn có chắc muốn hủy đơn #${order.id}?`)) return;

        const req: ReqUpdateOrderStatusDTO = { status: 'CANCELLED' };

        this.orderService.updateOrderStatus(order.id, req).subscribe({
            next: () => {
                const idx = this.orders.findIndex(o => o.id === order.id);
                if (idx > -1) this.orders[idx] = { ...this.orders[idx], status: 'CANCELLED' };
                this.toastr.success('Đơn hàng đã được hủy');
            },
            error: () => this.toastr.error('Hủy đơn hàng thất bại')
        });
    }

    reviewProduct(item: any): void {
        window.location.href = `/product-detail/${item.productId}?orderDetailId=${item.id}`;
    }
}
