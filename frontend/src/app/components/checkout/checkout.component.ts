import { Component, OnInit } from '@angular/core';
import { CartService, CartItem } from 'src/app/service/cart.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { UserService } from 'src/app/service/user.service';
import { AuthService } from 'src/app/service/auth.service';
import { OrderService } from 'src/app/service/order.service';
import { ReqOrderDTO } from 'src/app/dtos/order';
@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  checkoutItems: CartItem[] = [];
  subtotal: number = 0;
  total: number = 0;
  currentUserId: number | null = null;   // 👈 thêm biến này

  orderForm = {
    fullName: '',
    phone: '',
    email: '',
    address: '',
    payment: ''
  };

  constructor(
    private cartService: CartService,
    private toastr: ToastrService,
    private router: Router,
    private userService: UserService,
    private authService: AuthService,
    private orderService: OrderService
  ) { }

  ngOnInit(): void {
    // lấy danh sách sản phẩm đã chọn
    this.checkoutItems = this.cartService.getCheckoutItems();
    this.calculateTotals();

    // lấy userId từ authService
    this.authService.getCurrentUser().subscribe({
      next: (res) => {
        const user = res.data?.user;
        if (user) {
          this.currentUserId = user.id;   // 👈 gán userId

          // fill thông tin form
          this.userService.getUserByID(String(user.id)).subscribe({
            next: (resUser) => {
              const u = resUser.data;
              this.orderForm.fullName = u.fullName;
              this.orderForm.email = u.email;
              this.orderForm.phone = u.phoneNumber;
              this.orderForm.address = u.address;
            }
          });
        }
      },
      error: (err) => console.error(err)
    });
  }

  calculateTotals(): void {
    this.subtotal = this.checkoutItems.reduce((sum, i) => sum + i.price * i.quantity, 0);
    this.total = this.subtotal;
  }

  placeOrder(): void {
    if (!this.currentUserId) {
      this.toastr.error('Không tìm thấy user, vui lòng đăng nhập lại!');
      return;
    }
    if (!this.orderForm.fullName || !this.orderForm.phone ||
      !this.orderForm.email || !this.orderForm.address || !this.orderForm.payment) {
      this.toastr.error("Vui lòng nhập đầy đủ thông tin!");
      return;
    }

    const orderPayload: ReqOrderDTO = {
      fullname: this.orderForm.fullName,
      email: this.orderForm.email,
      phoneNumber: this.orderForm.phone,
      address: this.orderForm.address,
      note: '',
      shippingMethod: 'GHN',
      shippingAddress: this.orderForm.address,
      paymentMethod: this.orderForm.payment,
      userId: this.currentUserId!,
      orderDetails: this.checkoutItems.map(i => ({
        productId: i.productId,
        variantId: i.variantId ?? 0,
        categoryId: i.categoryId,
        numberOfProducts: i.quantity,
        price: i.price
      }))
    };

    if (this.orderForm.payment === 'COD') {
      this.orderService.createOrder(orderPayload).subscribe({
        next: () => {
          this.toastr.success('Đặt hàng thành công!');
          this.cartService.clearCart();
          localStorage.removeItem('checkout_items');
          this.router.navigate(['/my-order']);
        },
        error: () => this.toastr.error('Đặt hàng thất bại!')
      });
    } else if (this.orderForm.payment === 'VNPAY') {
      this.orderService.createPayment({ amount: this.subtotal.toString(), orderRequest: orderPayload })
        .subscribe({
          next: (paymentUrl) => {
            window.location.href = paymentUrl; // sang VNPAY
          },
          error: () => this.toastr.error('Không khởi tạo được thanh toán VNPAY!')
        });
    }
  }

}

