import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/app/service/product.service';
import { ProductImageService } from 'src/app/service/productImage.service';
import { ProductDTO } from 'src/app/dtos/product';
import { ProductImageDTO } from 'src/app/dtos/productImage';
import { CategoryService } from 'src/app/service/category.service';
import { CategoryDTO } from 'src/app/dtos/category';

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit {

  // ------------------------
  // UI state
  // ------------------------
  showSearch: boolean = false;
  showFilter: boolean = false;
  keyword: string = '';

  // ------------------------
  // Data state
  // ------------------------
  products: (ProductDTO & { images?: ProductImageDTO[] })[] = [];
  categories: CategoryDTO[] = [];
  meta: any;
  currentPage: number = 1;

  // ------------------------
  // Filter state
  // ------------------------
  selectedCategoryId: number | null = null;
  selectedSort: string = '';
  selectedPriceMin: number | null = null;
  selectedPriceMax: number | null = null;

  // ------------------------
  // Constructor
  // ------------------------
  constructor(
    private productService: ProductService,
    private productImageService: ProductImageService,
    private categoryService: CategoryService
  ) { }

  // ------------------------
  // Lifecycle
  // ------------------------
  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  // ------------------------
  // API calls
  // ------------------------
  loadCategories() {
    this.categoryService.getCategory().subscribe({
      next: res => (this.categories = res),
      error: err => console.error(err)
    });
  }

  loadProducts(page: number = 1, append: boolean = false) {
    this.productService
      .getProductsFiltered({
        page: page,
        size: 12,
        categoryId: this.selectedCategoryId,
        priceMin: this.selectedPriceMin,
        priceMax: this.selectedPriceMax,
        sort: this.selectedSort,
        keyword: this.keyword
      })
      .subscribe({
        next: res => {
          this.meta = res.meta;
          this.currentPage = page;

          this.products = append
            ? [...this.products, ...res.products]
            : res.products;

          this.products.forEach(p => {
            this.productImageService.listImages(p.id).subscribe({
              next: imgs => (p.images = imgs)
            });
          });
        },
        error: err => console.error(err)
      });
  }

  // ------------------------
  // Filter handlers
  // ------------------------
  filterByCategory(categoryId: number | null) {
    this.selectedCategoryId = categoryId;
    this.loadProducts(1);
  }

  applySort(sort: string) {
    this.selectedSort = sort;
    this.loadProducts(1);
  }

  applyPrice(min: number | null, max: number | null) {
    this.selectedPriceMin = min;
    this.selectedPriceMax = max;
    this.loadProducts(1);
  }

  // ------------------------
  // Helper
  // ------------------------
  getThumbnail(p: ProductDTO & { images?: ProductImageDTO[] }): string {
    if (p.images && p.images.length > 0) {
      const thumb = p.images.find(i => i.thumbnail);
      return thumb ? thumb.url : p.images[0].url;
    }
    return '/assets/images/404.jpg';
  }

  // ------------------------
  // UI toggle
  // ------------------------
  toggleSearch() {
    this.showSearch = !this.showSearch;
    if (this.showSearch) this.showFilter = false;
  }

  toggleFilter() {
    this.showFilter = !this.showFilter;
    if (this.showFilter) this.showSearch = false;
  }

  onSearch() {
    console.log('Searching for:', this.keyword);
    // TODO: gọi API search nếu backend hỗ trợ
    this.loadProducts(1);
  }

  // ------------------------
  // Pagination
  // ------------------------
  loadMore() {
    if (this.meta && this.currentPage < this.meta.pages) {
      this.loadProducts(this.currentPage + 1, true);
    }
  }
}
