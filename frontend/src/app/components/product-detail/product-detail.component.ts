import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { ProductService } from 'src/app/service/product.service';
import { ProductImageService } from 'src/app/service/productImage.service';
import { ProductVariantService } from 'src/app/service/productVariant.service';
import { CartService } from 'src/app/service/cart.service';
import { ReviewService } from 'src/app/service/review.service';
import { AuthService } from 'src/app/service/auth.service';

import { ProductDTO } from 'src/app/dtos/product';
import { ProductImageDTO } from 'src/app/dtos/productImage';
import { ProductVariantDTO } from 'src/app/dtos/productVariant';
import { ReviewDTO, ReqReviewDTO } from 'src/app/dtos/review';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {
  // ------------------------
  // Product state
  // ------------------------
  product!: ProductDTO;
  images: ProductImageDTO[] = [];
  variants: ProductVariantDTO[] = [];

  // ------------------------
  // Selection state
  // ------------------------
  sizes: string[] = [];
  colors: string[] = [];
  selectedSize = '';
  selectedColor = '';

  // ------------------------
  // Cart state
  // ------------------------
  selectedPrice: number | null = null;
  quantity = 1;

  // ------------------------
  // Review state
  // ------------------------
  reviews: ReviewDTO[] = [];
  newReview: ReqReviewDTO = {
    rating: 0,
    comment: '',
    userId: 0,
    productId: 0,
    orderDetailId: 0
  };

  currentUserId: number | null = null;
  selectedOrderDetailId: number | null = null;

  // ------------------------
  // Carousel config
  // ------------------------
  slideConfig = {
    slidesToShow: 1,
    slidesToScroll: 1,
    dots: true,
    arrows: true,
    infinite: true,
    autoplay: false,
    prevArrow:
      '<button class="slick-prev slick-arrow" aria-label="Previous" type="button"><i class="fa fa-chevron-left"></i></button>',
    nextArrow:
      '<button class="slick-next slick-arrow" aria-label="Next" type="button"><i class="fa fa-chevron-right"></i></button>'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,

    private productService: ProductService,
    private productImageService: ProductImageService,
    private productVariantService: ProductVariantService,
    private cartService: CartService,
    private reviewService: ReviewService,
    private authService: AuthService
  ) { }

  // ------------------------
  // Lifecycle
  // ------------------------
  ngOnInit(): void {
    const productId = Number(this.route.snapshot.paramMap.get('id'));
    this.selectedOrderDetailId = Number(this.route.snapshot.queryParamMap.get('orderDetailId'));
    this.loadProduct(productId);
    this.loadImages(productId);
    this.loadVariants(productId);
    this.loadReviews(productId);
    this.loadCurrentUser();
  }

  // ------------------------
  // API calls
  // ------------------------
  private loadProduct(productId: number) {
    this.productService.getProductById(productId).subscribe({
      next: res => (this.product = res)
    });
  }

  private loadImages(productId: number) {
    this.productImageService.listImages(productId).subscribe({
      next: res => (this.images = res)
    });
  }

  private loadVariants(productId: number) {
    this.productVariantService.getVariantByProductId(productId).subscribe({
      next: res => {
        this.variants = res;
        this.sizes = [...new Set(this.variants.map(v => v.size))];
        this.colors = [...new Set(this.variants.map(v => v.color))];
      }
    });
  }

  private loadReviews(productId: number) {
    this.reviewService.getReviewsByProduct(productId).subscribe({
      next: res => (this.reviews = res),
      error: () => this.toastr.error('Không thể tải review!')
    });
  }

  private loadCurrentUser() {
    this.authService.getCurrentUser().subscribe({
      next: res => {
        this.currentUserId = res.data?.user?.id || null;
      },
      error: () => {
        this.currentUserId = null;
      }
    });
  }

  // ------------------------
  // Availability checks
  // ------------------------
  isSizeAvailableForColor(size: string): boolean {
    return this.variants.some(
      v =>
        v.size === size &&
        (!this.selectedColor || v.color === this.selectedColor) &&
        v.stockQuantity > 0
    );
  }

  isColorAvailableForSize(color: string): boolean {
    return this.variants.some(
      v =>
        v.color === color &&
        (!this.selectedSize || v.size === this.selectedSize) &&
        v.stockQuantity > 0
    );
  }

  // ------------------------
  // Handlers - Variants
  // ------------------------
  selectSize(size: string) {
    this.selectedSize = this.selectedSize === size ? '' : size;
    this.updatePrice();
  }

  selectColor(color: string) {
    this.selectedColor = this.selectedColor === color ? '' : color;
    this.updatePrice();
  }

  private updatePrice() {
    if (this.selectedSize && this.selectedColor) {
      const variant = this.variants.find(
        v => v.size === this.selectedSize && v.color === this.selectedColor
      );
      this.selectedPrice = variant ? variant.price : null;
    } else {
      this.selectedPrice = null;
    }
  }

  // ------------------------
  // Handlers - Quantity
  // ------------------------
  increaseQty(): void {
    const max = this.selectedVariant?.stockQuantity ?? 1;
    if (this.quantity >= max) {
      this.toastr.warning(`Chỉ còn ${max} sản phẩm trong kho`);
      this.quantity = max;
      return;
    }
    this.quantity++;
  }

  decreaseQty(): void {
    if (this.quantity > 1) this.quantity--;
  }

  onQuantityInputChange(): void {
    const max = this.selectedVariant?.stockQuantity ?? 1;
    if (this.quantity < 1) this.quantity = 1;
    if (this.quantity > max) {
      this.toastr.warning(`Chỉ còn ${max} sản phẩm trong kho`);
      this.quantity = max;
    }
  }

  // ------------------------
  // Cart
  // ------------------------
  addToCart(): void {
    if (!this.selectedSize || !this.selectedColor) {
      this.toastr.warning('Vui lòng chọn Size và Màu sắc trước khi thêm vào giỏ!');
      return;
    }

    const variant = this.selectedVariant;
    if (!variant) {
      this.toastr.error('Biến thể sản phẩm không tồn tại!', 'Lỗi');
      return;
    }

    if (variant.stockQuantity <= 0) {
      this.toastr.error('Sản phẩm này đã hết hàng!', 'Thông báo');
      return;
    }

    if (this.quantity > variant.stockQuantity) {
      this.toastr.error(`Chỉ còn ${variant.stockQuantity} sản phẩm trong kho`);
      this.quantity = variant.stockQuantity;
      return;
    }

    const cartItem = {
      productId: this.product.id,
      variantId: variant.id,
      categoryId: this.product.category.id,
      name: this.product.name,
      price: variant.price,
      size: this.selectedSize,
      color: this.selectedColor,
      quantity: this.quantity,
      stockQuantity: variant.stockQuantity,
      image: this.images[0]?.url || ''
    };

    this.cartService.addItem(cartItem);
    this.toastr.success('Đã thêm sản phẩm vào giỏ hàng!', 'Thành công');
    this.router.navigate(['/shoping-cart']);
  }

  get selectedVariant(): ProductVariantDTO | undefined {
    if (!this.selectedSize || !this.selectedColor) return undefined;
    return this.variants.find(
      v => v.size === this.selectedSize && v.color === this.selectedColor
    );
  }

  // ------------------------
  // Review
  // ------------------------
  submitReview() {
    if (this.newReview.rating <= 0) {
      this.toastr.warning('Vui lòng chọn số sao!');
      return;
    }
    if (!this.newReview.comment.trim()) {
      this.toastr.warning('Vui lòng nhập nội dung review!');
      return;
    }
    if (!this.currentUserId) {
      this.toastr.error('Bạn cần đăng nhập để đánh giá!');
      return;
    }
    if (!this.selectedOrderDetailId) {
      this.toastr.error('Không tìm thấy orderDetailId để đánh giá!');
      return;
    }

    const payload: ReqReviewDTO = {
      rating: this.newReview.rating,
      comment: this.newReview.comment,
      userId: this.currentUserId,
      productId: this.product.id,
      orderDetailId: this.selectedOrderDetailId


    };
    console.log('Submitting review:', payload);

    this.reviewService.createReview(payload).subscribe({
      next: (res) => {
        this.toastr.success('Thêm review thành công!');
        this.reviews.push(res);
        this.newReview = { rating: 0, comment: '', userId: 0, productId: 0, orderDetailId: 0 };
      },
      error: (err) => {
        const msg = err.error?.message || 'Không thể thêm review';
        this.toastr.error(msg);
      }
    });
  }
}
