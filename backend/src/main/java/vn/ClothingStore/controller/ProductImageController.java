package vn.ClothingStore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.ClothingStore.domain.response.product.ResProductImageDTO;
import vn.ClothingStore.service.ProductImageService;
import vn.ClothingStore.util.error.IdInvalidException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    // Upload nhiều ảnh cho product
    @PostMapping("/{productId}/images")
    public ResponseEntity<List<ResProductImageDTO>> upload(
            @PathVariable int productId,
            @RequestParam("files") List<MultipartFile> files) throws IdInvalidException {

        List<ResProductImageDTO> res = productImageService.uploadImages(productId, files);
        return ResponseEntity.ok(res);
    }

    // Danh sách ảnh theo product
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ResProductImageDTO>> list(@PathVariable Integer productId) throws IdInvalidException {
        return ResponseEntity.ok(productImageService.listByProduct(productId));
    }

    // Đặt thumbnail
    @PutMapping("/images/{imageId}/thumbnail")
    public ResponseEntity<ResProductImageDTO> setThumb(@PathVariable Integer imageId) throws IdInvalidException {
        return ResponseEntity.ok(productImageService.setThumbnail(imageId));
    }

    // Xoá ảnh
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Integer imageId) throws IdInvalidException {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
