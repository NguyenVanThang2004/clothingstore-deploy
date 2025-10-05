import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CartService } from 'src/app/service/cart.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-payment-result',
  templateUrl: './payment-result.component.html',
  styleUrls: ['./payment-result.component.css']
})
export class PaymentResultComponent implements OnInit, OnDestroy {
  status: 'SUCCESS' | 'FAILED' | null = null;
  redirectIn = 5;               // giây đếm ngược
  private timer: any = null;
  private clearedKey = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private cartService: CartService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const raw = (params.get('status') || '').toUpperCase();
      this.status = raw === 'SUCCESS' ? 'SUCCESS' : raw === 'FAILED' ? 'FAILED' : null;

      const orderId = params.get('orderId') || '';
      const txnRef = params.get('vnp_TxnRef') || params.get('txnRef') || '';
      this.clearedKey = `cart_cleared_${orderId || txnRef || 'default'}`;

      if (this.status === 'SUCCESS') {
        if (!sessionStorage.getItem(this.clearedKey)) {
          this.cartService.clearCart();
          localStorage.removeItem('checkout_items');
          sessionStorage.setItem(this.clearedKey, '1');
          this.toastr.success('Thanh toán thành công! Giỏ hàng đã được làm trống.');
        }
        this.startCountdown(() => this.router.navigate(['/my-order']));
      } else if (this.status === 'FAILED') {
        this.toastr.error('Thanh toán thất bại. Vui lòng thử lại!');
        this.startCountdown(() => this.router.navigate(['/shoping-cart']));
      }
    });

    this.route.queryParamMap.subscribe(params => {
      console.log('status param =', params.get('status'));
    });

  }

  private startCountdown(done: () => void) {
    if (this.timer) clearInterval(this.timer);
    this.redirectIn = 5;
    this.timer = setInterval(() => {
      this.redirectIn--;
      if (this.redirectIn <= 0) {
        clearInterval(this.timer);
        done();
      }
    }, 1000);
  }

  goNow(): void {
    if (this.status === 'SUCCESS') this.router.navigate(['/my-order']);
    else this.router.navigate(['/shoping-cart']);
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
  }


}
