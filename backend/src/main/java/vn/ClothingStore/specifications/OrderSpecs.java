package vn.ClothingStore.specifications;

import vn.ClothingStore.domain.Order;
import vn.ClothingStore.util.constant.OrderStatusEnum;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecs {
    public static Specification<Order> hasOrderStatus(OrderStatusEnum status) {
        return (root, query, cb) -> {
            if (status == null)
                return null;
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Order> getOrderByUserId(int userId) {
        return (root, query, cb) -> {
            return cb.equal(root.get("user").get("id"), userId);
        };
    }
}
