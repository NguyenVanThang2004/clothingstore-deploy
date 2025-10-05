import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Modal } from 'bootstrap';
import { CategoryService } from 'src/app/service/category.service';
import { ProductService } from 'src/app/service/product.service';
import { ProductImageService } from 'src/app/service/productImage.service';
import { ProductDTO, ProductPayload } from 'src/app/dtos/product';
import { ProductImageDTO } from 'src/app/dtos/productImage';
import { ProductVariantDTO } from 'src/app/dtos/productVariant';
import { ProductVariantService } from 'src/app/service/productVariant.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent {

  // ------------------------
  // State & properties
  // ------------------------
  products: ProductDTO[] = [];
  meta = { page: 1, pageSize: 10, pages: 1, total: 0 };
  loading = false;
  saving = false;
  errorMessage = '';

  categories: any[] = [];
  colors = ['BLACK', 'WHITE', 'GRAY', 'BLUE'];
  sizes = ['S', 'M', 'L', 'XL'];
  thumbnailIndex = 0;
  step = 1;
  selectedCategoryId: number | null = null;
  keyword: string = '';


  formAdd: any = {
    name: '',
    price: 0,
    description: '',
    categoryId: 0,
    images: [] as { url: string; file?: File; id?: number; thumbnail?: boolean }[],
    variants: [] as ProductVariantDTO[]
  };

  selectedProduct: ProductDTO | null = null;
  productImages: ProductImageDTO[] = [];
  productVariants: ProductVariantDTO[] = [];

  // ------------------------
  // Modals
  // ------------------------
  @ViewChild('productAddModal') productAddModalRef!: ElementRef<HTMLDivElement>;
  @ViewChild('productDetailModal') productDetailModalRef!: ElementRef<HTMLDivElement>;
  @ViewChild('productEditModal') productEditModalRef!: ElementRef<HTMLDivElement>;
  @ViewChild('variantModal') variantModalRef!: ElementRef<HTMLDivElement>;



  addModal!: Modal;
  detailModal!: Modal;
  editModal!: Modal;
  variantModal!: Modal;
  // ------------------------
  // Constructor
  // ------------------------
  constructor(
    private categoryService: CategoryService,
    private productService: ProductService,
    private productImageService: ProductImageService,
    private productVariantService: ProductVariantService,
    private toastr: ToastrService
  ) { }

  // ------------------------
  // Lifecycle
  // ------------------------
  ngOnInit() {
    this.load(1);
    this.categoryService.getCategory().subscribe({
      next: (cats) => (this.categories = cats),
      error: () => (this.categories = [])
    });
  }

  ngAfterViewInit() {
    this.addModal = new Modal(this.productAddModalRef.nativeElement, { backdrop: 'static', keyboard: false });
    this.detailModal = new Modal(this.productDetailModalRef.nativeElement, { backdrop: 'static', keyboard: false });
    this.editModal = new Modal(this.productEditModalRef.nativeElement, { backdrop: 'static', keyboard: false });
    this.variantModal = new Modal(this.variantModalRef.nativeElement, { backdrop: 'static', keyboard: false });
  }

  // ------------------------
  // Pagination & loading
  // ------------------------
  load(page: number) {
    this.loading = true;
    this.errorMessage = '';
    this.productService.getProductsFiltered({
      page,
      size: this.meta.pageSize,
      categoryId: this.selectedCategoryId,
      keyword: this.keyword
    }).subscribe({
      next: ({ products, meta }) => {
        this.products = products;
        this.meta = meta;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toastr.error('Không tải được sản phẩm');
      }
    });
  }
  applyFilter() {
    this.load(1);  // reset về trang 1 khi đổi filter
  }



  prev() { if (this.meta.page > 1) this.load(this.meta.page - 1); }
  next() { if (this.meta.page < this.meta.pages) this.load(this.meta.page + 1); }

  // ------------------------
  // Add product
  // ------------------------
  openAdd() {
    this.formAdd = { name: '', price: 0, description: '', categoryId: 0, images: [], variants: [] };
    this.thumbnailIndex = 0;
    this.step = 1;
    this.addModal.show();
  }

  nextStep(form: any) {
    if (this.step === 1 && form.invalid) {
      this.toastr.warning('Vui lòng nhập đầy đủ thông tin sản phẩm');
      return;
    }
    if (this.step < 2) this.step++;
  }

  prevStep() {
    if (this.step > 1) this.step--;
  }

  onFilesSelected(event: any) {
    const files: FileList = event.target.files;
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.formAdd.images.push({ url: e.target.result, file });
      };
      reader.readAsDataURL(file);
    }
  }

  saveAll() {
    if (this.formAdd.images.length === 0) {
      this.toastr.error('Vui lòng chọn ít nhất 1 ảnh sản phẩm');
      return;
    }

    this.saving = true;

    const productPayload: ProductPayload = {
      name: this.formAdd.name,
      price: this.formAdd.price,
      description: this.formAdd.description,
      categoryId: Number(this.formAdd.categoryId)
    };

    this.productService.createProduct(productPayload).subscribe({
      next: (createdProduct: ProductDTO) => {
        console.log('Created product:', createdProduct);

        const productId = createdProduct.id;
        if (!productId) {
          this.toastr.error('Không nhận được ID sản phẩm từ server');
          this.saving = false;
          return;
        }

        // Upload ảnh
        const files: File[] = this.formAdd.images
          .map((img: any) => img.file)
          .filter((f: File) => !!f);

        if (files.length > 0) {
          this.productImageService.uploadImages(productId, files).subscribe({
            next: (imgs: ProductImageDTO[]) => {
              const thumb = imgs[this.thumbnailIndex];
              if (thumb?.id) {
                this.productImageService.setThumbnail(thumb.id).subscribe();
              }
            },
            error: (err) => {
              console.error('Upload ảnh lỗi:', err);
              this.toastr.error('Upload ảnh thất bại');
            }
          });
        }

        this.toastr.success('Thêm sản phẩm thành công!');
        this.saving = false;
        this.addModal.hide();
        this.load(this.meta.page);
      },
      error: (err) => {
        console.error('Create product error:', err);
        this.toastr.error('Không tạo được sản phẩm');
        this.saving = false;
      }
    });
  }


  // ------------------------
  // Detail product
  // ------------------------
  openDetail(product: ProductDTO) {
    this.selectedProduct = product;

    this.productImageService.listImages(product.id).subscribe({
      next: (imgs) => (this.productImages = imgs),
      error: () => (this.productImages = [])
    });
    this.productVariantService.getVariantByProductId(product.id).subscribe({
      next: (variants) => (this.productVariants = variants ?? []),
      error: () => (this.productVariants = [])
    });


    // TODO: load variants khi có API
    this.detailModal.show();
  }

  // ------------------------
  // EDIT product
  // ------------------------
  editingId: number | null = null;
  formEdit: { name: string; price: number; description: string; categoryId: number } = {
    name: '', price: 0, description: '', categoryId: 0
  };
  openEdit(p: ProductDTO) {
    if (!p?.id) return;
    this.editingId = p.id;
    this.formEdit = {
      name: p.name ?? '',
      price: Number(p.price) || 0,
      description: p.description ?? '',
      categoryId: p.category?.id ? Number(p.category.id) : 0
    };
    this.editModal.show();
  }

  saveEdit(form: any) {
    if (!this.editingId) return;
    if (form.invalid || !this.formEdit.categoryId) {
      this.toastr.warning('Vui lòng nhập đầy đủ thông tin hợp lệ');
      return;
    }

    const productPayload: ProductPayload = {
      name: this.formEdit.name,
      price: this.formEdit.price,
      description: this.formEdit.description,
      categoryId: Number(this.formEdit.categoryId)
    };

    this.saving = true;
    this.productService.updateProduct(this.editingId, productPayload).subscribe({
      next: () => {
        this.toastr.success('Cập nhật sản phẩm thành công');
        this.saving = false;
        this.editModal.hide();

        // Cách 1: reload trang hiện tại
        this.load(this.meta.page);

        // (Tuỳ chọn) Cách 2: cập nhật tại chỗ không reload:
        // const idx = this.products.findIndex(x => x.id === this.editingId);
        // if (idx > -1) {
        //   this.products[idx] = {
        //     ...this.products[idx],
        //     name: payload.name!,
        //     price: payload.price!,
        //     description: payload.description!,
        //     category: { id: this.formEdit.categoryId, name: this.categories.find(c => c.id===this.formEdit.categoryId)?.name }
        //   } as any;
        // }
      },
      error: () => {
        this.saving = false;
        this.toastr.error('Cập nhật thất bại');
      }
    });
  }

  // ------------------------
  // delete product
  // ------------------------
  openDelete() {
    alert("hiện tại chưa thể xóa được sản phẩm")
  }

  // ------------------------
  // variant product
  // ------------------------
  variantFormModel: Partial<ProductVariantDTO> = { color: 'BLACK', size: 'S', price: 0, stockQuantity: 0 };
  variantEditingId: number | null = null;
  openVariants(p: ProductDTO) {
    this.selectedProduct = p;
    this.productVariantService.getVariantByProductId(p.id).subscribe({
      next: (variants) => (this.productVariants = variants ?? []),
      error: () => (this.productVariants = [])
    });
    this.variantFormModel = { color: 'BLACK', size: 'S', price: 0, stockQuantity: 0 };
    this.variantEditingId = null;
    this.variantModal.show();
  }

  openVariantEdit(v: ProductVariantDTO) {
    this.variantEditingId = v.id!;
    this.variantFormModel = { ...v };
  }

  saveVariant() {
    if (!this.selectedProduct) return;
    const productId = this.selectedProduct.id;

    if (this.variantEditingId) {
      // UPDATE
      const payload = {
        price: this.variantFormModel.price!,
        stockQuantity: this.variantFormModel.stockQuantity!
      };

      this.productVariantService.updateVariant(productId, this.variantEditingId, payload)
        .subscribe({
          next: (updated) => {
            this.toastr.success('Cập nhật biến thể thành công');
            const idx = this.productVariants.findIndex(x => x.id === this.variantEditingId);
            if (idx > -1) {
              // cập nhật lại phần tử và ép Angular re-render
              this.productVariants[idx] = {
                ...this.productVariants[idx],
                ...updated
              };
              this.productVariants = [...this.productVariants];
            }

            // reset form
            this.variantEditingId = null;
            this.variantFormModel = { color: 'BLACK', size: 'S', price: 0, stockQuantity: 0 };
          },
          error: () => this.toastr.error('Cập nhật biến thể thất bại')
        });

    } else {
      // CREATE
      const payload = {
        color: this.variantFormModel.color!,
        size: this.variantFormModel.size!,
        price: this.variantFormModel.price!,
        stockQuantity: this.variantFormModel.stockQuantity!,
        product: { id: productId }
      };

      this.productVariantService.createVariant(payload as any)
        .subscribe({
          next: (created) => {
            this.toastr.success('Thêm biến thể thành công');

            // thêm mới và ép Angular render lại
            this.productVariants = [...this.productVariants, created];

            // reset form
            this.variantFormModel = { color: 'BLACK', size: 'S', price: 0, stockQuantity: 0 };
            this.variantEditingId = null;
          },
          error: () => this.toastr.error('Thêm biến thể thất bại')
        });
    }
  }



}
