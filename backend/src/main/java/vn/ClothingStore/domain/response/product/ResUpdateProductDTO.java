package vn.ClothingStore.domain.response.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateProductDTO {
    private int id;
    private String name;
    private float price;
    private String description;
    private CategoryDTO category;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDTO {
        private int id;
        private String name;
    }

}
