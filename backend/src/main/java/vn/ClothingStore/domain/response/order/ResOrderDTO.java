package vn.ClothingStore.domain.response.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ClothingStore.util.constant.OrderStatusEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResOrderDTO {
    private int id;
    private String fullname;
    private String email;
    private String phoneNumber;
    private String address;
    private String note;
    private Instant orderDate;
    private OrderStatusEnum status;
    private float totalMoney;
    private String shippingMethod;
    private String shippingAddress;
    private Instant shippingDate;
    private String trackingNumber;
    private String paymentMethod;
    private boolean active;
    private UserDTO user;
    private List<OrderDetailDTO> orderDetails;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDTO {
        private int id;
        private String name;
        private String email;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDetailDTO {
        private int id;
        private int productId;
        private String productName;
        private int variantId;
        private String size;
        private String color;
        private int categoryId;
        private String categoryName;
        private float price;
        private int numberOfProducts;
        private float totalMoney;
    }
}
