package vn.ClothingStore.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.ClothingStore.domain.Category;
import vn.ClothingStore.domain.Product;
import vn.ClothingStore.domain.request.product.ReqProductDTO;
import vn.ClothingStore.domain.request.product.ReqProductFilter;
import vn.ClothingStore.domain.response.ResultPaginationDTO;
import vn.ClothingStore.domain.response.product.ResProductDTO;
import vn.ClothingStore.domain.response.product.ResUpdateProductDTO;
import vn.ClothingStore.service.CategoryService;
import vn.ClothingStore.service.ProductService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/filter")
    public Page<ResProductDTO> filterProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ReqProductFilter req = new ReqProductFilter();

        req.setCategoryId(categoryId);
        req.setPriceMin(priceMin);
        req.setPriceMax(priceMax);
        req.setSort(sort);
        req.setKeyword(keyword);

        Pageable pageable = PageRequest.of(page, size);
        return productService.filterProducts(req, pageable);
    }

    @GetMapping
    @ApiMessage("Get products success")
    public ResponseEntity<ResultPaginationDTO> getAllProduct(
            @Filter Specification<Product> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.productService.fetchAllProduct(spec, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ApiMessage("Create product success")
    public ResponseEntity<ResProductDTO> createProduct(
            @Valid @RequestBody ReqProductDTO req) throws IdInvalidException {
        Product product = this.productService.createProduct(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productService.convertToResProductDTO(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @ApiMessage("Update product success")
    public ResponseEntity<ResProductDTO> updateProduct(
            @PathVariable("id") int id,
            @Valid @RequestBody ReqProductDTO req) throws IdInvalidException {
        Product saved = this.productService.updateProduct(id, req);
        return ResponseEntity.ok(this.productService.convertToResProductDTO(saved));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ApiMessage("Delete product success")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") int id) throws IdInvalidException {
        Product currentProduct = this.productService.fetchProductById(id);
        if (currentProduct == null) {
            throw new IdInvalidException("Product với id = " + id + " không tồn tại");
        }
        this.productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("Get product by id success")
    public ResponseEntity<ResProductDTO> getProductById(@PathVariable("id") int id) throws IdInvalidException {
        Product product = this.productService.fetchProductById(id);
        if (product == null) {
            throw new IdInvalidException("Product với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.productService.convertToResProductDTO(product));
    }

}
