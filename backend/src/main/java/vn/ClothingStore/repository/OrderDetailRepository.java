package vn.ClothingStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.ClothingStore.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

}
