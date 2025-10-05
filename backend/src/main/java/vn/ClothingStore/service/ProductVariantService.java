package vn.ClothingStore.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.ClothingStore.domain.Product;
import vn.ClothingStore.domain.ProductVariant;
import vn.ClothingStore.domain.request.variant.ReqUploadProductVariantDTO;
import vn.ClothingStore.domain.response.product.ResProductVariantDTO;
import vn.ClothingStore.repository.ProductVariantRepository;
import vn.ClothingStore.util.error.IdInvalidException;

@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductService productService;

    public ProductVariantService(ProductVariantRepository productVariantRepository, ProductService productService) {
        this.productVariantRepository = productVariantRepository;
        this.productService = productService;
    }

    public ResProductVariantDTO convertToResProductVariantDTO(ProductVariant productVariant) {

        ResProductVariantDTO res = new ResProductVariantDTO();
        res.setId(productVariant.getId());
        res.setColor(productVariant.getColor());
        res.setSize(productVariant.getSize());
        res.setPrice(productVariant.getPrice());
        res.setStockQuantity(productVariant.getStockQuantity());
        res.setProductID(productVariant.getProduct().getId());

        return res;

    }

    public ProductVariant createProductVariant(ProductVariant productVariant) throws IdInvalidException {

        // kiem tra san pham có mau va size ay da ton tai chua
        int productId = productVariant.getProduct().getId();
        Product product = this.productService.fetchProductById(productId);

        if (product == null) {
            throw new IdInvalidException("Product id " + productId + " không tồn tại");
        }
        // neu ton tai product thi minh se kiem tra product ay co cung mau cung size
        // khong
        boolean existed = this.productVariantRepository.existsByProduct_IdAndColorAndSize(productId,
                productVariant.getColor(), productVariant.getSize());
        if (existed) {
            throw new IdInvalidException("biến thể của sản phẩm này đã tồn tại rồi");
        }
        return this.productVariantRepository.save(productVariant);
    }

    public ProductVariant updateProductVariant(int variantId, int productId, ReqUploadProductVariantDTO req)
            throws IdInvalidException {
        ProductVariant pv = this.productVariantRepository.findByIdAndProduct_Id(variantId, productId);

        if (pv == null) {
            throw new IdInvalidException("Biến thể không tồn tại");
        }

        pv.setPrice(req.getPrice());
        pv.setStockQuantity(req.getStockQuantity());

        return this.productVariantRepository.save(pv);
    }

    public List<ResProductVariantDTO> getProductVariantByProductId(int productId) throws IdInvalidException {
        List<ProductVariant> variants = this.productVariantRepository.findByProduct_Id(productId);
        if (variants == null || variants.isEmpty()) {
            throw new IdInvalidException("Product này không có biến thể nào");
        }

        return variants.stream().map(this::convertToResProductVariantDTO).collect(Collectors.toList());
    }

}
