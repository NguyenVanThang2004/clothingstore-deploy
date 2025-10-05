package vn.ClothingStore.domain.request.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqProductFilter {
    private Integer categoryId; // lọc theo category
    private Double priceMin; // giá tối thiểu
    private Double priceMax; // giá tối đa
    private String sort; // vd: "price,asc" hoặc "createdAt,desc"
    private String keyword;
}
