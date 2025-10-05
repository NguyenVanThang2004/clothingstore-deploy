package vn.ClothingStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ClothingStore.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByOrderDetailId(int orderDetailId);

    List<Review> findByProductId(int productId);
}
