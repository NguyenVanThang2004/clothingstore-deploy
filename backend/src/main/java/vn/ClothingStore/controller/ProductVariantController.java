package vn.ClothingStore.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ClothingStore.domain.ProductVariant;
import vn.ClothingStore.domain.request.variant.ReqUploadProductVariantDTO;
import vn.ClothingStore.domain.response.product.ResProductVariantDTO;
import vn.ClothingStore.service.ProductVariantService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/productVariant")
    public ResponseEntity<ResProductVariantDTO> createProductVariant(@RequestBody ProductVariant productVariant)
            throws IdInvalidException {

        ProductVariant p = this.productVariantService.createProductVariant(productVariant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productVariantService.convertToResProductVariantDTO(p));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/productVariant/{productId}/variants/{variantId}")
    public ResponseEntity<ResProductVariantDTO> updateProductVariant(
            @PathVariable("productId") int productId,
            @PathVariable("variantId") int variantId,
            @RequestBody ReqUploadProductVariantDTO req) throws IdInvalidException {

        ProductVariant pv = this.productVariantService.updateProductVariant(variantId, productId, req);

        return ResponseEntity.ok().body(this.productVariantService.convertToResProductVariantDTO(pv));
    }

    @GetMapping("/productVariant/{productId}/variants")
    public ResponseEntity<List<ResProductVariantDTO>> getProductVariantByProductId(
            @PathVariable("productId") int productId)
            throws IdInvalidException {
        List<ResProductVariantDTO> ls = this.productVariantService.getProductVariantByProductId(productId);
        return ResponseEntity.ok().body(ls);
    }

}
