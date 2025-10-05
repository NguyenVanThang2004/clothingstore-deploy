package vn.ClothingStore.domain.response.product;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResProductDTO {

    private int id;
    private String name;
    private float price;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

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
