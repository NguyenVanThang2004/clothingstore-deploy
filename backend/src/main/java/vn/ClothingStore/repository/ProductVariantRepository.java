package vn.ClothingStore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ClothingStore.domain.ProductVariant;
import vn.ClothingStore.util.constant.ColorEnum;
import vn.ClothingStore.util.constant.SizeEnum;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    List<ProductVariant> findByProductId(int productId);

    List<ProductVariant> findByProduct_Id(int productId);

    Optional<ProductVariant> findByProductIdAndColorAndSize(int productId, ColorEnum color, SizeEnum size);

    boolean existsByProduct_IdAndColorAndSize(int productId, ColorEnum color, SizeEnum size);

    boolean existsByProduct_IdAndColorAndSizeAndIdNot(int productId, ColorEnum color, SizeEnum size, int id);

    ProductVariant findByIdAndProduct_Id(int id, int productId);

}
