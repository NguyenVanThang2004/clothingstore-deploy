package vn.ClothingStore.domain.request.order;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ReqOrderDTO {
    private int userId;
    private String fullname;
    private String email;
    private String phoneNumber;
    private String address;
    private String note;
    private String shippingMethod;
    private String shippingAddress;
    private String paymentMethod;
    private List<ReqOrderDetail> orderDetails;

    @Getter
    @Setter
    public static class ReqOrderDetail {
        private int productId;
        private int variantId;
        private int categoryId;
        private int numberOfProducts;
        private float price;
    }
}
