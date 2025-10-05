package vn.ClothingStore.domain.request.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqProductDTO {

    private String name;
    private float price;
    private String description;
    private Integer categoryId;
}
