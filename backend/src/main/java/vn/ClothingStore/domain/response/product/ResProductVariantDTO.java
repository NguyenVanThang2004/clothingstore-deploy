package vn.ClothingStore.domain.response.product;

import lombok.Getter;
import lombok.Setter;
import vn.ClothingStore.util.constant.ColorEnum;
import vn.ClothingStore.util.constant.SizeEnum;

@Getter
@Setter
public class ResProductVariantDTO {
    private int id;
    private ColorEnum color;
    private SizeEnum size;
    private float price;
    private int stockQuantity;
    private int productID;
}
