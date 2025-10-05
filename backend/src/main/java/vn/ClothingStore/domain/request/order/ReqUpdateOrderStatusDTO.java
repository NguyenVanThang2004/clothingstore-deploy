package vn.ClothingStore.domain.request.order;

import lombok.Getter;
import lombok.Setter;
import vn.ClothingStore.util.constant.OrderStatusEnum;

@Getter
@Setter
public class ReqUpdateOrderStatusDTO {
    private OrderStatusEnum status;
}
