package vn.ClothingStore.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ClothingStore.domain.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductIdOrderByUploadAtDesc(int productId);

    long countByProductIdAndThumbnailTrue(int productId);
}
