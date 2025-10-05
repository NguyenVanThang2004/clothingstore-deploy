package vn.ClothingStore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.ClothingStore.domain.Order;
import vn.ClothingStore.util.constant.OrderStatusEnum;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    boolean existsByUserIdAndStatusAndOrderDetailsProductId(int userId, OrderStatusEnum status, int productId);

    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

}
