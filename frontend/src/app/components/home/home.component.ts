import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/app/service/product.service';
import { ProductImageService } from 'src/app/service/productImage.service';
import { ProductDTO } from 'src/app/dtos/product';
import { ProductImageDTO } from 'src/app/dtos/productImage';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  slides = [
    { img: "/assets/images/slide-03.jpg" },
    { img: "/assets/images/slide-02.jpg" },
    { img: "/assets/images/slide-01.jpg" }
  ];
  slideConfig = {
    slidesToShow: 1,
    slidesToScroll: 1,
    dots: true,
    infinite: true,
    arrows: true,
    autoplay: true,
    autoplaySpeed: 5000
  };
  products: (ProductDTO & { images?: ProductImageDTO[] })[] = [];
  meta: any;

  constructor(
    private productService: ProductService,
    private productImageService: ProductImageService
  ) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(page: number = 1): void {
    this.productService.getProducts(page, 8).subscribe({
      next: res => {
        this.products = res.products;
        this.meta = res.meta;

        // load ảnh cho từng product
        this.products.forEach(p => {
          this.productImageService.listImages(p.id).subscribe({
            next: imgs => p.images = imgs
          });
        });
      }
    });
  }

  getThumbnail(p: ProductDTO & { images?: ProductImageDTO[] }): string {
    if (p.images && p.images.length > 0) {
      const thumb = p.images.find(i => i.thumbnail);
      return thumb ? thumb.url : p.images[0].url;
    }
    return '/assets/images/no-image.png';
  }
}
