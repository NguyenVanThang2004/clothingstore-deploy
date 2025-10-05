package vn.ClothingStore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.ClothingStore.domain.Product;
import vn.ClothingStore.domain.ProductImage;
import vn.ClothingStore.domain.response.product.ResCloudinaryDTO;
import vn.ClothingStore.domain.response.product.ResProductImageDTO;
import vn.ClothingStore.repository.ProductImageRepository;
import vn.ClothingStore.repository.ProductRepository;
import vn.ClothingStore.util.FileUploadUtil;
import vn.ClothingStore.util.error.IdInvalidException;
import vn.ClothingStore.util.error.StorageException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;

    // Dùng chính method này, không gọi ProductImageMapper nữa để tránh lỗi compile
    private ResProductImageDTO toDTO(ProductImage e) {
        ResProductImageDTO d = new ResProductImageDTO();
        d.setId(e.getId());
        d.setUrl(e.getUrl());
        d.setThumbnail(e.isThumbnail());
        d.setUploadAt(e.getUploadAt());
        d.setProductID(e.getProduct().getId());
        return d;
    }

    @Transactional
    public List<ResProductImageDTO> uploadImages(final Integer productId, final List<MultipartFile> files)
            throws IdInvalidException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IdInvalidException("Product id " + productId + " không tồn tại"));

        if (files == null || files.isEmpty()) {
            throw new StorageException("Không có file nào được upload");
        }

        boolean hasThumb = productImageRepository.countByProductIdAndThumbnailTrue(productId) > 0;
        List<ProductImage> batch = new ArrayList<>();
        List<ResProductImageDTO> out = new ArrayList<>();
        int idx = 0;

        for (MultipartFile f : files) {
            FileUploadUtil.assertAllowed(f);

            // Gợi ý tên publicId (không kèm đuôi): baseName_yyyyMMddHHmmss
            String baseName = stripExtension(FileUploadUtil.sanitizeFilename(f.getOriginalFilename()));
            String publicIdHint = FileUploadUtil.generateFileName(f, baseName); // dùng util mới của bạn

            // Upload lên Cloudinary vào folder riêng theo product
            ResCloudinaryDTO res = cloudinaryService.uploadToFolder(
                    f,
                    "clothing_store/product/" + productId,
                    publicIdHint);

            // Lưu DB
            ProductImage pi = new ProductImage();
            pi.setProduct(product);
            pi.setUrl(res.getUrl());
            pi.setCloudinaryId(res.getPublicId());
            // đặt thumbnail cho ảnh đầu tiên nếu chưa có thumbnail
            pi.setThumbnail(!hasThumb && idx == 0);

            batch.add(pi);
            idx++;
        }

        // saveAll để tối ưu round-trip
        List<ProductImage> saved = productImageRepository.saveAll(batch);
        for (ProductImage pi : saved) {
            out.add(toDTO(pi));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<ResProductImageDTO> listByProduct(Integer productId) throws IdInvalidException {
        if (!productRepository.existsById(productId)) {
            throw new IdInvalidException("Product id " + productId + " không tồn tại");
        }
        return productImageRepository.findByProductIdOrderByUploadAtDesc(productId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ResProductImageDTO setThumbnail(int imageId) throws IdInvalidException {
        ProductImage target = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IdInvalidException("Image id " + imageId + " không tồn tại"));
        Integer pid = target.getProduct().getId();

        productImageRepository.findByProductIdOrderByUploadAtDesc(pid)
                .forEach(img -> {
                    img.setThumbnail(img.getId() == imageId);
                    productImageRepository.save(img);
                });

        return toDTO(target);
    }

    @Transactional
    public void deleteImage(Integer imageId) throws IdInvalidException {
        ProductImage img = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IdInvalidException("Image id " + imageId + " không tồn tại"));

        // Xoá Cloudinary trước
        cloudinaryService.delete(img.getCloudinaryId());
        // Xoá DB
        productImageRepository.delete(img);
    }

    private String stripExtension(String name) {
        if (name == null)
            return "file";
        int pos = name.lastIndexOf('.');
        return (pos > 0) ? name.substring(0, pos) : name;
    }
}
