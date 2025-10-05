package vn.ClothingStore.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.ClothingStore.domain.Category;
import vn.ClothingStore.domain.Product;
import vn.ClothingStore.domain.User;
import vn.ClothingStore.domain.request.product.ReqProductDTO;
import vn.ClothingStore.domain.request.product.ReqProductFilter;
import vn.ClothingStore.domain.response.ResultPaginationDTO;
import vn.ClothingStore.domain.response.product.ResProductDTO;
import vn.ClothingStore.domain.response.product.ResUpdateProductDTO;
import vn.ClothingStore.repository.ProductRepository;
import vn.ClothingStore.specifications.ProductSpecs;
import vn.ClothingStore.util.error.IdInvalidException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product createProduct(ReqProductDTO req) throws IdInvalidException {
        Category category = categoryService.fetchCategoryById(req.getCategoryId());
        if (category == null) {
            throw new IdInvalidException("Category với id = " + req.getCategoryId() + " không tồn tại");
        }

        Product product = new Product();
        product.setName(req.getName());
        product.setPrice(req.getPrice());
        product.setDescription(req.getDescription());
        product.setCategory(category);

        return productRepository.save(product);
    }

    public Product updateProduct(int id, ReqProductDTO req) throws IdInvalidException {
        Product currentProduct = this.fetchProductById(id);
        if (currentProduct == null) {
            throw new IdInvalidException("Product với id = " + id + " không tồn tại");
        }

        Category category = categoryService.fetchCategoryById(req.getCategoryId());
        if (category == null) {
            throw new IdInvalidException("Category với id = " + req.getCategoryId() + " không tồn tại");
        }

        currentProduct.setName(req.getName());
        currentProduct.setPrice(req.getPrice());
        currentProduct.setDescription(req.getDescription());
        currentProduct.setCategory(category);

        return productRepository.save(currentProduct);
    }

    public Product fetchProductById(int id) {
        return this.productRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllProduct(Specification<Product> spec, Pageable pageable) {
        Page<Product> pageProduct = this.productRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageProduct.getTotalPages());
        mt.setTotal(pageProduct.getTotalElements());
        rs.setMeta(mt);

        List<ResProductDTO> listProduct = pageProduct.getContent()
                .stream()
                .map(this::convertToResProductDTO)
                .collect(Collectors.toList());

        rs.setResult(listProduct);
        return rs;
    }

    public Page<ResProductDTO> filterProducts(ReqProductFilter req, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecs.hasCategory(req.getCategoryId()))
                .and(ProductSpecs.hasPriceBetween(req.getPriceMin(), req.getPriceMax()))
                .and(ProductSpecs.hasKeyword(req.getKeyword()));

        // xử lý sort nếu FE truyền vào
        Sort sort = Sort.unsorted();
        if (req.getSort() != null && !req.getSort().isEmpty()) {
            String[] parts = req.getSort().split(",");
            String field = parts[0];
            Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(dir, field);
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Product> page = productRepository.findAll(spec, sortedPageable);

        return page.map(this::convertToResProductDTO);
    }

    public void deleteProduct(int id) throws IdInvalidException {
        Product product = this.fetchProductById(id);
        if (product == null) {
            throw new IdInvalidException("Product với id = " + id + " không tồn tại");
        }
        this.productRepository.delete(product);
    }

    public ResProductDTO convertToResProductDTO(Product product) {
        ResProductDTO res = new ResProductDTO();
        res.setId(product.getId());
        res.setName(product.getName());
        res.setPrice(product.getPrice());
        res.setDescription(product.getDescription());
        res.setCreatedAt(product.getCreatedAt());
        res.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            res.setCategory(new ResProductDTO.CategoryDTO(
                    product.getCategory().getId(),
                    product.getCategory().getName()));
        }

        return res;
    }

    // public ResUpdateProductDTO convertToResUpdateProductDTO(Product product) {
    // ResUpdateProductDTO res = new ResUpdateProductDTO();
    // res.setId(product.getId());
    // res.setName(product.getName());
    // res.setPrice(product.getPrice());
    // res.setDescription(product.getDescription());

    // if (product.getCategory() != null) {
    // res.setCategory(new ResUpdateProductDTO.CategoryDTO(
    // product.getCategory().getId(),
    // product.getCategory().getName()));
    // }
    // return res;
    // }
}
