package vn.ClothingStore.domain.response.review;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ClothingStore.domain.Product;
import vn.ClothingStore.util.constant.ColorEnum;
import vn.ClothingStore.util.constant.SizeEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResReviewDTO {
    private int id;
    private int rating;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
    private UserDTO user;
    private OrderDetailDTO orderDetail;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private int id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailDTO {
        private int id;
        private String nameProduct;
        private ColorEnum color;
        private SizeEnum size;
    }

}
